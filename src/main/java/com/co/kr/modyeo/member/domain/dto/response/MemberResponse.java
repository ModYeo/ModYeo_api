package com.co.kr.modyeo.member.domain.dto.response;

import com.co.kr.modyeo.member.domain.entity.Member;
import com.co.kr.modyeo.member.domain.entity.embed.Address;
import com.co.kr.modyeo.member.domain.enumerate.Sex;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponse {

    private Long id;
    private String email;
    private String username;
    private Address address;
    private Sex sex;
    private List<CrewResponse> crewResponses = new ArrayList<>();
    private List<CategoryResponse> categoryResponses = new ArrayList<>();

    @Builder(builderClassName = "of", builderMethodName = "of")
    public MemberResponse(Long id, String email, String username, Address address, Sex sex, List<CrewResponse> crewResponses, List<CategoryResponse> categoryResponses) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.address = address;
        this.sex = sex;
        this.crewResponses = crewResponses;
        this.categoryResponses = categoryResponses;
    }

    public static MemberResponse toRes(Member member){
        return MemberResponse.of()
                .id(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .address(member.getAddress())
                .sex(member.getSex())
                .crewResponses(member.getMemberCrewList().stream()
                        .map(memberCrew -> CrewResponse.toRes(memberCrew.getCrew()))
                        .collect(Collectors.toList()))
                .categoryResponses(member.getInterestCategoryList().stream()
                        .map(memberCategory -> CategoryResponse.toRes(memberCategory.getCategory()))
                        .collect(Collectors.toList()))
                .build();
    }
}