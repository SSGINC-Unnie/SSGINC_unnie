package com.ssginc.unnie.shop.dto;


import lombok.Data;

@Data
public class ShopResponse {
    private Long shopId;
    private String shopName;
    private double avgRate;
    private int review_count;
    private String shopLocation;
}
