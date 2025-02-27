package com.ssginc.unnie.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemRequest {
    private long receiptItemId; // 영수증 품목 Id
    private long receiptId;       // 영수증 Id
    private String receiptItemName; // 영수증 품목 이름
    private int receiptItemPrice;   // 영수증 품목 가격
    private int receiptItemQuantity; // 영수증 품목 수량
}
