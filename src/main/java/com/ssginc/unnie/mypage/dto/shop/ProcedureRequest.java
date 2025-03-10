package com.ssginc.unnie.mypage.dto.shop;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcedureRequest {

    private int procedureShopId;
    private String procedureName;
    private int procedurePrice;
    private int procedureId;
    private String procedureThumbnail;

}
