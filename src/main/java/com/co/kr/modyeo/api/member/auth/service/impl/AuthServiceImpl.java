package com.co.kr.modyeo.api.member.auth.service.impl;

import com.co.kr.modyeo.api.category.domain.dto.request.CategoryRequest;
import com.co.kr.modyeo.api.category.domain.entity.Category;
import com.co.kr.modyeo.api.category.repository.CategoryRepository;
import com.co.kr.modyeo.api.member.auth.provider.JwtTokenProvider;
import com.co.kr.modyeo.api.member.domain.entity.Member;
import com.co.kr.modyeo.api.member.domain.entity.link.MemberCategory;
import com.co.kr.modyeo.api.member.repository.MemberCategoryRepository;
import com.co.kr.modyeo.api.member.repository.MemberRepository;
import com.co.kr.modyeo.common.exception.CustomAuthException;
import com.co.kr.modyeo.common.exception.code.AuthErrorCode;
import com.co.kr.modyeo.common.result.JsonResultData;
import com.co.kr.modyeo.api.member.auth.domain.dto.MemberRequestDto;
import com.co.kr.modyeo.api.member.auth.domain.dto.MemberResponseDto;
import com.co.kr.modyeo.api.member.auth.domain.dto.TokenDto;
import com.co.kr.modyeo.api.member.auth.domain.dto.TokenRequestDto;
import com.co.kr.modyeo.api.member.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final StringRedisTemplate redisTemplate;

    private final CategoryRepository categoryRepository;

    private final MemberCategoryRepository memberCategoryRepository;

    @Override
    public MemberResponseDto signup(MemberRequestDto memberRequestDto) {
        if (memberRepository.existsByEmail(memberRequestDto.getEmail())){
            throw new CustomAuthException(JsonResultData
                    .failResultBuilder()
                    .errorCode(AuthErrorCode.ALREADY_JOIN_USER.getCode())
                    .errorMessage(AuthErrorCode.ALREADY_JOIN_USER.getMessage())
                    .build());
        }

        Member member = MemberRequestDto.toMember(memberRequestDto,passwordEncoder);
        member = memberRepository.save(member);

        if (!memberRequestDto.getCategoryRequests().isEmpty()){
            List<Long> categoryIds = memberRequestDto.getCategoryRequests()
                    .stream().map(CategoryRequest::getId).collect(Collectors.toList());

            List<Category> categoryList = categoryRepository.findByCategoryIds(categoryIds);

            Member finalMember = member;
            List<MemberCategory> memberCategoryList = categoryList.stream().map(category -> MemberCategory.createMemberCategoryBuilder()
                    .member(finalMember)
                    .category(category)
                    .build()).collect(Collectors.toList());

            memberCategoryRepository.saveAll(memberCategoryList);
        }

        return MemberResponseDto.toResponse(member);
    }

    @Override
    public TokenDto login(MemberRequestDto memberRequestDto) {
        UsernamePasswordAuthenticationToken authenticationToken = memberRequestDto.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);

        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(),
                        tokenDto.getRefreshToken(),
                        tokenDto.getAccessTokenExpiresIn(),
                        TimeUnit.MILLISECONDS);

        return tokenDto;
    }

    @Override
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        if (!jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new CustomAuthException(JsonResultData
                    .failResultBuilder()
                    .errorCode(AuthErrorCode.NOT_VALID_TOKEN.getCode())
                    .errorMessage(AuthErrorCode.NOT_VALID_TOKEN.getMessage())
                    .build());
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        String refreshToken = redisTemplate.opsForValue().get("RT:" + authentication.getName());
        assert refreshToken != null;
        if (!refreshToken.equals(tokenRequestDto.getRefreshToken())){
            throw new CustomAuthException(JsonResultData
                    .failResultBuilder()
                    .errorCode(AuthErrorCode.NOT_MATCH_TOKEN_INFO.getCode())
                    .errorMessage(AuthErrorCode.NOT_MATCH_TOKEN_INFO.getMessage())
                    .build());
        }

        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);

        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(),
                        tokenDto.getRefreshToken(),
                        tokenDto.getAccessTokenExpiresIn(), TimeUnit.MILLISECONDS);

        return tokenDto;
    }

    @Override
    public void logout(TokenRequestDto tokenRequestDto) {
        if(!jwtTokenProvider.validateToken(tokenRequestDto.getAccessToken())){
            throw new CustomAuthException(JsonResultData
                    .failResultBuilder()
                    .errorCode(AuthErrorCode.NOT_VALID_TOKEN.getCode())
                    .errorMessage(AuthErrorCode.NOT_VALID_TOKEN.getMessage())
                    .build());
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        if(redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null){
            redisTemplate.delete("RT:"+ authentication.getName());
        }

        Long expiration = jwtTokenProvider.getExpiration(tokenRequestDto.getAccessToken());
        redisTemplate.opsForValue()
                .set(tokenRequestDto.getAccessToken(),"logout", expiration, TimeUnit.MILLISECONDS);
    }
}
