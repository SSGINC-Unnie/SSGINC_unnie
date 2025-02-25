package com.ssginc.unnie.mypage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcedureRequest {

    private int procedureDesignerId;
    private String procedureName;
    private int procedurePrice;
    private int procedureId;

}
