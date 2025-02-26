package com.ssginc.unnie.member.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberFindPwRequest {
    private String memberEmail;
}
