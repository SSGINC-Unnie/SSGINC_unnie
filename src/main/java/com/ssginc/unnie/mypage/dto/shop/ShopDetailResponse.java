package com.ssginc.unnie.mypage.dto.shop;

import lombok.Data;
import java.util.List;

@Data
public class ShopDetailResponse {
    private int shopId;
    private String shopBusinessNumber;
    private String shopRepresentationName;
    private String shopCreatedAt;
    private String shopLocation;
    private String shopName;
    private String shopCategory;
    private String shopBusinessTime;
    private String shopTel;
    private String shopClosedDay;
    private String shopIntroduction;

    // 업체 이미지 URL 목록 (미디어 URN)
    private List<String> shopImages;
}
