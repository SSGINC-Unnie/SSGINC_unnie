package com.ssginc.unnie.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ReceiptRequest {
    private LocalDateTime receiptDate;
    private int receiptAmount;
    private String receiptBusinessNumber;
    private String receiptApprovalNumber;
    private int receiptShopId;
    private int receiptMemberId;
    private String shopName;
    private List<ReceiptItemRequest> items;
}
