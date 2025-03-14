package com.ssginc.unnie.mypage.dto.shop;

import com.ssginc.unnie.shop.vo.ShopCategory;
import lombok.Builder;
import lombok.Data;

import java.sql.Time;

@Data
@Builder
public class ShopInsertRequest {
    private int shopId;
    // 업체명
    private String shopName;
    // 업체 주소
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
    // 대표자명
    private String shopRepresentationName;
    // 사업자등록번호
    private String shopBusinessNumber;
    // 사업자 등록일
    private String shopCreatedAt;
    // 멤버 ID
    private int shopMemberId;

}
