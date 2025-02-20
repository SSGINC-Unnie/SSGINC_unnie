package com.ssginc.unnie.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ReceiptResponse {
    private Long receiptId;
    private LocalDateTime receiptDate;
    private int receiptAmount;
    private String receiptBusinessNumber;
    private String receiptApprovalNumber;
    private int receiptShopId;
    private List<ReceiptItemResponse> items;
}
