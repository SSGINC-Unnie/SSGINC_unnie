package com.ssginc.unnie.mypage.dto.shop;

import lombok.Data;

@Data
public class DesignerDeleteRequest {
    private int designerId;
    private int designerShopId;  // 디자이너가 속한 샵 ID
    private int shopMemberId;    // 해당 샵의 소유자(member) ID
}