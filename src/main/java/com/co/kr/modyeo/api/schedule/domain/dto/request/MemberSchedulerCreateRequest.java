package com.co.kr.modyeo.api.schedule.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSchedulerCreateRequest {

    private Long memberId;

    private Long schedulerId;

    public MemberSchedulerCreateRequest(Long memberId, Long schedulerId) {
        this.memberId = memberId;
        this.schedulerId = schedulerId;
    }
}
