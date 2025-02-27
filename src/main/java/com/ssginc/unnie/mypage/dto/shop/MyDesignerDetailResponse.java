package com.ssginc.unnie.mypage.dto.shop;

import lombok.Data;

import java.util.List;

@Data
public class MyDesignerDetailResponse {
    private int designerId;
    private String designerName;
    private String designerIntroduction;
    private String designerThumbnail;
    private List<MyProcedureDetailResponse> procedures;
}
