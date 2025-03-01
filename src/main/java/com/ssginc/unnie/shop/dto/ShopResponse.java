package com.ssginc.unnie.shop.dto;


import lombok.Data;

@Data
public class ShopResponse {
    private Long shopId;
    private String shopName;
    private String shopBusinessTime;
    private double avgRate;
    private String shopLocation;
    // 업체 상태
    private int shopStatus;
}
