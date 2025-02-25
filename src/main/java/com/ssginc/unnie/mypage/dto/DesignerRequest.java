package com.ssginc.unnie.mypage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DesignerRequest {

    private String designerName;
    private String designerIntroduction;
    private int designerShopId;
    private String designerThumbnail;
    private int designerId;

}
