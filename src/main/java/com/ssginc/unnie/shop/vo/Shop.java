package com.ssginc.unnie.shop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shop {

    // 업체 번호
    private int shopId;
    // 업체명
    private String shopName;
    // 업체 위치
    private String shopLocation;
    // 입점일
    private Timestamp shopRegisteredAt;
    // 카테고리
    private ShopCategory shopCategory;
    // 영업시간
    private Time shopBusinessTime;
    // 전화번호
    private String shopTel;
    // 업체 소개
    private String shopIntroduction;
    // 휴무일
    private Character ShopClosedDay;
    // 사업자등록번호
    private String shopBusinessNumber;
    // 대표자명
    private String shopRepresentationName;
    // 사업자 등록일
    private LocalDateTime shopCreatedAt;
    //수정일
    private Timestamp shopUpdatedAt;
    // 회원 번호
    private int shopMemberId;
    // 위도
    private Double shopLatitude;
    // 경도
    private Double shopLongitude;
    // 승인여부
    private int shopStatus;
    //리뷰 요약
    private String reviewSummary;
}