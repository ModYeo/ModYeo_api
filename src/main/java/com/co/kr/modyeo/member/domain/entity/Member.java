package com.co.kr.modyeo.member.domain.entity;

import com.co.kr.modyeo.member.domain.entity.embed.Address;
import com.co.kr.modyeo.member.domain.enumerate.Authority;
import com.co.kr.modyeo.member.domain.enumerate.Sex;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    private String username;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder(builderClassName = "of",builderMethodName = "of")
    public Member(String email, String password, Authority authority) {
        this.email = email;
        this.password = password;
        this.authority = authority;
    }
}
