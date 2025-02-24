package com.ssginc.unnie.mypage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcedureCreateRequest {

    private int procedureDesignerId;
    private String procedureName;
    private int procedurePrice;

}
