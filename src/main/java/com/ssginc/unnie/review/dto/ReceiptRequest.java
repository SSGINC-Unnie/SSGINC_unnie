package com.ssginc.unnie.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ReceiptRequest {
    private long receiptId; //영수증 번호
    private Integer receiptShopId; //shop Id
    private LocalDateTime receiptDate; //영수증 내 결제 일시
    private int receiptAmount; //결제 금액
    private String receiptBusinessNumber; //사업자 번호
    private String receiptApprovalNumber; //승인 번호
    private String receiptShopName; //영수증 내 가게 이름
    private List<ReceiptItemRequest> items; //영수증 내 상품들
}
