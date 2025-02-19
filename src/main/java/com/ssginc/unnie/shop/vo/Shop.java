package com.ssginc.unnie.shop.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class Shop {

    // 업체 번호
    private Integer shopId;
    // 업체명
    private String shopName;
    // 업체 위치
    private String shopLocation;
    // 입점임
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
    private Integer shopMemberId;
    // 위도
    private BigDecimal shopLatitude;
    // 경도
    private BigDecimal shopLongitude;
    // 승인여부
    private Integer shopStatus;
}