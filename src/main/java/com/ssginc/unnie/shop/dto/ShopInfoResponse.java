package com.ssginc.unnie.shop.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopInfoResponse {
    private Long shopId;
    private String shopName;
    private String shopBusinessTime;
    private double avgRate;
    private String shopLocation;
    private LocalDateTime latestReviewDate;
    private String latestReviewContent;
    private String reviewSummary;
    private long shopMemberId;
}
