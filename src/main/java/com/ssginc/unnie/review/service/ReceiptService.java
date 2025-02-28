package com.ssginc.unnie.review.service;


import com.ssginc.unnie.review.dto.ReceiptRequest;
import com.ssginc.unnie.review.dto.ReceiptResponse;

public interface ReceiptService {

    /**
     * OCR 데이터를 기반으로 영수증을 저장하는 메서드
     *
     * @param receiptRequest OCR 데이터 기반 영수증 저장 요청 DTO
     * @return 저장된 영수증 정보 응답 DTO
     */
    ReceiptResponse saveReceipt(ReceiptRequest receiptRequest);

    /**
     * 영수증 ID를 기반으로 영수증 조회
     *
     * @param receiptId 조회할 영수증 ID
     * @return 영수증 정보 응답 DTO
     */
    ReceiptResponse getReceiptById(Long receiptId);

    /**
     * 영수증이 인증되었는지 확인하는 메서드.
     * 예: 영수증 승인번호가 "데이터 없음"이 아니거나, 특정 상태 플래그가 인증된 상태인지 체크.
     *
     * @param receiptId 확인할 영수증 ID
     * @return 인증된 영수증이면 true, 아니면 false
     */
    boolean isReceiptVerified(long receiptId);
}