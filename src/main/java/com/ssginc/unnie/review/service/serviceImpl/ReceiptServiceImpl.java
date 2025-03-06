package com.ssginc.unnie.review.service.serviceImpl;

import com.ssginc.unnie.common.exception.UnnieReviewException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.review.dto.ReceiptRequest;
import com.ssginc.unnie.review.dto.ReceiptResponse;
import com.ssginc.unnie.review.dto.ReceiptItemRequest;
import com.ssginc.unnie.review.dto.ReceiptItemResponse;
import com.ssginc.unnie.review.mapper.ReceiptItemMapper;
import com.ssginc.unnie.review.mapper.ReceiptMapper;
import com.ssginc.unnie.review.service.ReceiptService;
import com.ssginc.unnie.common.util.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptMapper receiptMapper;
    private final ReceiptItemMapper receiptItemMapper;
    private final Validator<ReceiptRequest> receiptValidator;

    /**
     * OCR 데이터를 기반으로 영수증과 품목 데이터를 DB에 저장 (DTO를 통해 처리)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReceiptResponse saveReceipt(ReceiptRequest receiptRequest) {
        // 1. 영수증 유효성 검증
        if (!receiptValidator.validate(receiptRequest)) {
            log.error("유효하지 않은 영수증 데이터: {}", receiptRequest);
            throw new UnnieReviewException(ErrorCode.INVALID_RECEIPT);
        }

        // shop_name을 기반으로 shop_id 조회
        Integer shopId = receiptMapper.findShopIdByName(receiptRequest.getReceiptShopName());

        // 조회된 shop_id를 receipt 객체에 설정
        if (shopId != null) {
            receiptRequest.setReceiptShopId(shopId);
        }

        // 2. 영수증 저장
        receiptMapper.insertReceipt(receiptRequest);
        log.info("영수증 저장 완료 (ID: {})", receiptRequest.getReceiptId());

        // 3. 품목 저장 - OCRParser에서 임시 값 대신, DB에서 생성된 영수증 ID를 할당
        List<ReceiptItemRequest> items = Optional.ofNullable(receiptRequest.getItems())
                .orElse(Collections.emptyList());
        items.forEach(item -> {
            // DB에서 생성된 영수증 ID를 각 품목에 할당
            item.setReceiptId(receiptRequest.getReceiptId());
            receiptItemMapper.insertReceiptItem(item);
        });

        // 4. 저장된 데이터를 DTO로 변환하여 반환
        List<ReceiptItemResponse> receiptItemResponses = items.stream()
                .map(item -> new ReceiptItemResponse(
                        item.getReceiptId(),
                        item.getReceiptItemId(),
                        item.getReceiptItemName(),
                        item.getReceiptItemPrice(),
                        item.getReceiptItemQuantity()
                ))
                .collect(Collectors.toList());

        return new ReceiptResponse(
                receiptRequest.getReceiptId(),
                receiptRequest.getReceiptDate(),
                receiptRequest.getReceiptAmount(),
                receiptRequest.getReceiptBusinessNumber(),
                receiptRequest.getReceiptApprovalNumber(),
                receiptRequest.getReceiptShopName()
        );
    }

    /**
     * 특정 영수증을 조회하여 응답 DTO로 변환
     */
    @Override
    public ReceiptResponse getReceiptById(long receiptId) {
        ReceiptResponse receiptResponse = receiptMapper.findReceiptById(receiptId);
        if (receiptResponse == null) {
            log.error("영수증을 찾을 수 없음 (ID: {})", receiptId);
            throw new UnnieReviewException(ErrorCode.RECEIPT_NOT_FOUND);
        }

        List<ReceiptItemResponse> receiptItems = receiptItemMapper.findItemsByReceiptId(receiptId);

        return new ReceiptResponse(
                receiptResponse.getReceiptId(),
                receiptResponse.getReceiptDate(),
                receiptResponse.getReceiptAmount(),
                receiptResponse.getReceiptBusinessNumber(),
                receiptResponse.getReceiptApprovalNumber(),
                receiptResponse.getReceiptShopName()
        );
    }

    /**
     * 영수증의 유효성 (인증 여부) 확인
     */
    @Override
    public boolean isReceiptVerified(long receiptId) {
        return Optional.ofNullable(receiptMapper.findReceiptById(receiptId))
                .map(receipt -> {
                    String approvalNumber = receipt.getReceiptApprovalNumber();
                    return approvalNumber != null && !approvalNumber.equals("데이터 없음");
                })
                .orElse(false);
    }
}
