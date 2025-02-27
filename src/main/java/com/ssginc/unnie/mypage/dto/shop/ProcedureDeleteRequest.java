package com.ssginc.unnie.mypage.dto.shop;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcedureDeleteRequest {
    private int procedureId;
    private int procedureDesignerId; // 시술과 연관된 디자이너 ID
    private int shopMemberId;        // 디자이너가 소속된 업체의 소유자 ID
}