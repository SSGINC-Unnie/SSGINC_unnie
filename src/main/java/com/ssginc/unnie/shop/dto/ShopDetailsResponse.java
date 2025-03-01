package com.ssginc.unnie.shop.dto;

import com.ssginc.unnie.shop.vo.ShopCategory;
import lombok.Data;

import java.sql.Time;

@Data
public class ShopDetailsResponse {
    // 업체명
    private String shopName;
    // 업체 위치
    private String shopLocation;
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
    // 업체 상태
    private int shopStatus;
}
