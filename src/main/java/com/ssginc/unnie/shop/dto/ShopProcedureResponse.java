package com.ssginc.unnie.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopProcedureResponse {
    // 시술 번호 (PK)
    private Integer procedureId;
    // 시술 이름
    private String procedureName;
    // 시술 가격
    private Integer procedurePrice;
    // 디자이너 번호 (FK)
    private Integer procedureDesignerId;
}
