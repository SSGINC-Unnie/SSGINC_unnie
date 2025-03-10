package com.ssginc.unnie.shop.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopInfoResponse {
    private Long shopId;
    private String shopName;
    private String shopBusinessTime;
    private long shopMemberId;
    private int reviewCount;
    private double avgRate;
    private String shopLocation;
    private String reviewSummary;
    private LocalDateTime latestReviewDate;
    private String latestReviewContent;
    private String latestMemberNickname;
    private String shopTel;
}
