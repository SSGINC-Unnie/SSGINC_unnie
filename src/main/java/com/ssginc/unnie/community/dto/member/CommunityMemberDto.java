package com.ssginc.unnie.community.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityMemberDto {
    private long memberId;
    private String memberNickname;
}
