package com.ssginc.unnie.mypage.dto;

import com.ssginc.unnie.shop.vo.ShopCategory;
import lombok.Data;

import java.sql.Time;

@Data
public class ShopInsertResponse {

    // 업체 번호
    private Integer shopId;
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
    // 회원 번호
    private Integer shopMemberId;

}
