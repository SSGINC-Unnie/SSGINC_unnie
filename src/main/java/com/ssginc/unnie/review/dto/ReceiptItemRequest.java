package com.ssginc.unnie.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReceiptItemRequest {
    private long receiptItemId;
    private long receiptId;
    private String receiptItemName;
    private int receiptItemPrice;
    private int receiptItemQuantity;
}
