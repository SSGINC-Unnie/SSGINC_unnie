package com.ssginc.unnie.review.service.serviceImpl;

import com.ssginc.unnie.review.dto.ReceiptItemRequest;
import com.ssginc.unnie.review.dto.ReceiptItemResponse;
import com.ssginc.unnie.review.dto.ReceiptRequest;
import com.ssginc.unnie.review.dto.ReceiptResponse;
import com.ssginc.unnie.review.mapper.ReceiptItemMapper;
import com.ssginc.unnie.review.mapper.ReceiptMapper;
import com.ssginc.unnie.review.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptMapper receiptMapper;
    private final ReceiptItemMapper receiptItemMapper;

    /**
     * OCR 데이터를 기반으로 영수증과 품목 데이터를 DB에 저장 (DTO를 통해 처리)
     */
    @Transactional
    @Override
    public ReceiptResponse saveReceipt(ReceiptRequest receiptRequest) {
        // 1. Receipt 저장 (DTO에서 직접 필드 설정)
        receiptMapper.insertReceipt(receiptRequest);

        // 2. ReceiptItem 저장
        List<ReceiptItemRequest> items = receiptRequest.getItems();
        if (items != null && !items.isEmpty()) {
            items.forEach(item -> {
                item.setReceiptId(receiptRequest.getReceiptId()); // 영수증 ID 설정
                receiptItemMapper.insertReceiptItem(item);
            });
        }

        // 3. 저장된 데이터를 DTO로 변환하여 반환
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
                receiptRequest.getReceiptShopName(),
                receiptItemResponses
        );
    }

    /**
     * 특정 영수증을 조회하여 응답 DTO로 변환
     */
    @Override
    public ReceiptResponse getReceiptById(Long receiptId) {
        ReceiptResponse receiptResponse = receiptMapper.findReceiptById(receiptId);
        List<ReceiptItemResponse> receiptItems = receiptItemMapper.findItemsByReceiptId(receiptId);

        return new ReceiptResponse(
                receiptResponse.getReceiptId(),
                receiptResponse.getReceiptDate(),
                receiptResponse.getReceiptAmount(),
                receiptResponse.getReceiptBusinessNumber(),
                receiptResponse.getReceiptApprovalNumber(),
                receiptResponse.getReceiptShopName(),
                receiptItems
        );
    }
}
