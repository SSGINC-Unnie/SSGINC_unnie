package com.ssginc.unnie.review.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 영수증 결제 품목 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptItem {
    private long receiptItemId; //영수증 결제 품목 번호
    private long receiptId; //영수증 번호
    private String receiptItemName; //영수증 품목 이름
    private int receiptItemPrice; //영수증 품목 가격
    private int receiptItemQuantity; //영수증 품목 수량
}
