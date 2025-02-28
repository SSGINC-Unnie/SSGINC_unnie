package com.ssginc.unnie.mypage.dto.shop;

import com.ssginc.unnie.shop.vo.ShopCategory;
import lombok.Data;

import java.sql.Time;

@Data
public class ShopUpdateRequest {
    // 업체명
    private String shopName;
    // 업체 주소
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
    private Character shopClosedDay;

    private int shopId;
}
