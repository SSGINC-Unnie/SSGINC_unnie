package com.ssginc.unnie.admin.dto.shop;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminShopUpdateRequest {
    private int shopId;
    private int shopStatus;
    private LocalDateTime shopRegistratedAt;
}
