package com.ssginc.unnie.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ReceiptResponse {
    private long receiptId; //영수증 Id
    private LocalDateTime receiptDate; //영수증-결제 일시
    private int receiptAmount; //결제 금액
    private String receiptBusinessNumber; //사업자 번호
    private String receiptApprovalNumber; //카드 승인번호
    private String receiptShopName; //상호명
    private List<ReceiptItemResponse> items; //결제 상품목록
}
