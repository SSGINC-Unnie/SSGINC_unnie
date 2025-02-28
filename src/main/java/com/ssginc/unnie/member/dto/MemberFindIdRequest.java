package com.ssginc.unnie.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberFindIdRequest {
    private String memberName; // 이름
    private String memberPhone; // 전화번호
}
