package com.ssginc.unnie.mypage.dto;

import com.ssginc.unnie.shop.vo.ShopCategory;
import lombok.Builder;
import lombok.Data;

import java.sql.Time;

@Data
@Builder
public class DesignerCreateRequest {

    private String designerName;
    private String designerIntroduction;
    private int designerShopId;
    private String designerThumbnail;

}
