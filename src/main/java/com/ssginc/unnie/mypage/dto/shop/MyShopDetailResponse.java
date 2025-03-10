package com.ssginc.unnie.mypage.dto.shop;

import com.ssginc.unnie.shop.vo.ShopCategory;
import lombok.Data;

import java.sql.Time;
import java.util.List;

@Data
public class MyShopDetailResponse {
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
    private Character shopClosedDay;
    // 사업자등록번호
    private String shopBusinessNumber;
    // 대표자명
    private String shopRepresentationName;

    private List<MyDesignerDetailResponse> designers;
    private List<MyProcedureDetailResponse> procedures;
}