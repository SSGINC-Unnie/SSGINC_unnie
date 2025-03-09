package com.ssginc.unnie.mypage.dto.shop;

import lombok.Data;

@Data
public class MyProcedureDetailResponse {
    private int procedureId;
    private String procedureName;
    private int procedurePrice;
    private String procedureThumbnail;
}
