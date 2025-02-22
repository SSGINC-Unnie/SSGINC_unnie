package com.ssginc.unnie.shop.dto;

import com.ssginc.unnie.shop.vo.ShopCategory;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class ShopInsertRequest {

    // 업체 번호
    private Integer shopId;
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
    // 회원 번호
    private Integer shopMemberId;

}
