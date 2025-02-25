package com.ssginc.unnie.mypage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcedureDeleteRequest {
    private int procedureId;
}
