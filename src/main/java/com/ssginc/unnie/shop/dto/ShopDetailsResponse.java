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
    private String shopBusinessTime;
    // 전화번호
    private String shopTel;
    // 업체 소개
    private String shopIntroduction;
    // 휴무일
    private String shopClosedDay;
}
