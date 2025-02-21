package com.ssginc.unnie.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ReceiptRequest {
    private long receiptId;
    private LocalDateTime receiptDate;
    private int receiptAmount;
    private String receiptBusinessNumber;
    private String receiptApprovalNumber;
    private String receiptShopName;
    private List<ReceiptItemRequest> items;
}
