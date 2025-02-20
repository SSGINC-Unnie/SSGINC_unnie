package com.ssginc.unnie.review.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 영수증 VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Receipt {
    private long receiptId; //영수증 번호
    private LocalDateTime receiptDate; //결제 일시
    private int receiptAmount; //결제 금액
    private String receiptBusinessNumber; //사업자 번호
    private String receiptApprovalNumber; //승인번호
    private LocalDateTime receiptCreatedAt; //영수증 업로드 일시
    private int receiptShopId; //업체 번호
    private int receiptMemberId; //영수증 업로드 회원
}
