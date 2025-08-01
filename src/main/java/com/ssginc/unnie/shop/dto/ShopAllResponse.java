package com.ssginc.unnie.shop.dto;
import com.ssginc.unnie.shop.vo.ShopCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopAllResponse {

    // 업체명
    private String shopName;
    // 위도
    private Double shopLatitude;
    // 경도
    private Double shopLongitude;

    private ShopCategory shopCategory;

    private int shopId;

    private String shopLocation;

    private int reviewCount;

    private double avgRate;



}