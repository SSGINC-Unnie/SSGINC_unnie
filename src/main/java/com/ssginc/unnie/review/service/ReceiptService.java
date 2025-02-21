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
}