package com.ssginc.unnie.mypage.dto.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureRequest {

    private int procedureShopId;
    private String procedureName;
    private int procedurePrice;
    private int procedureId;
    private String procedureThumbnail;

}
