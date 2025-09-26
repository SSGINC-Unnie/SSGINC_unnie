package com.ssginc.unnie.admin.dto.shop; // (패키지 위치는 예시입니다)

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ShopApprovedEvent {
    private final long receiverId;
    private final int shopId;
    private final String shopName;
}