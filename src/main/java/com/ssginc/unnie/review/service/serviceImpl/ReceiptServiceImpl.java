package com.ssginc.unnie.review.service.serviceImpl;

import com.ssginc.unnie.review.dto.ReceiptItemResponse;
import com.ssginc.unnie.review.dto.ReceiptRequest;
import com.ssginc.unnie.review.dto.ReceiptResponse;
import com.ssginc.unnie.review.mapper.ReceiptItemMapper;
import com.ssginc.unnie.review.mapper.ReceiptMapper;
import com.ssginc.unnie.review.service.ReceiptService;
import com.ssginc.unnie.review.vo.Receipt;
import com.ssginc.unnie.review.vo.ReceiptItem;
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
     * OCR 데이터를 기반으로 영수증과 품목 데이터를 DB에 저장
     */
    @Transactional
    @Override
    public ReceiptResponse saveReceipt(ReceiptRequest receiptRequest) {
        // 1. Receipt 저장
        Receipt receipt = new Receipt();
        receipt.setReceiptDate(receiptRequest.getReceiptDate());
        receipt.setReceiptAmount(receiptRequest.getReceiptAmount());
        receipt.setReceiptBusinessNumber(receiptRequest.getReceiptBusinessNumber());
        receipt.setReceiptApprovalNumber(receiptRequest.getReceiptApprovalNumber());
        receipt.setReceiptShopId(receiptRequest.getReceiptShopId());
        receipt.setReceiptMemberId(receiptRequest.getReceiptMemberId());

        receiptMapper.insertReceipt(receipt); // ID 자동 설정됨

        // 2. ReceiptItem 저장
        receiptRequest.getItems().forEach(item -> {
            ReceiptItem receiptItem = new ReceiptItem();
            receiptItem.setReceiptId(receipt.getReceiptId());  // 방금 저장한 영수증 ID 참조
            receiptItem.setReceiptItemName(item.getReceiptItemName());
            receiptItem.setReceiptItemPrice(item.getReceiptItemPrice());
            receiptItem.setReceiptItemQuantity(item.getReceiptItemQuantity());

            receiptItemMapper.insertReceiptItem(receiptItem);
        });

        // 3. 저장된 데이터를 DTO로 변환하여 반환
        List<ReceiptItemResponse> receiptItemResponse = receiptRequest.getItems().stream()
                .map(item -> new ReceiptItemResponse(
                        item.getReceiptId(),
                        item.getReceiptItemId(),
                        item.getReceiptItemName(),
                        item.getReceiptItemPrice(),
                        item.getReceiptItemQuantity()
                ))
                .collect(Collectors.toList());

        return new ReceiptResponse(
                receipt.getReceiptId(),
                receipt.getReceiptDate(),
                receipt.getReceiptAmount(),
                receipt.getReceiptBusinessNumber(),
                receipt.getReceiptApprovalNumber(),
                receiptRequest.getReceiptShopId(),
                receiptItemResponse
        );
    }

    /**
     * 특정 영수증을 조회하여 응답 DTO로 변환
     */
    @Override
    public ReceiptResponse getReceiptById(Long receiptId) {
        Receipt receipt = receiptMapper.findReceiptById(receiptId);
        List<ReceiptItem> receiptItems = receiptItemMapper.findItemsByReceiptId(receiptId);

        List<ReceiptItemResponse> receiptItemResponse = receiptItems.stream()
                .map(item -> new ReceiptItemResponse(
                        item.getReceiptId(),
                        item.getReceiptItemId(),
                        item.getReceiptItemName(),
                        item.getReceiptItemPrice(),
                        item.getReceiptItemQuantity()
                ))
                .collect(Collectors.toList());

        return new ReceiptResponse(
                receipt.getReceiptId(),
                receipt.getReceiptDate(),
                receipt.getReceiptAmount(),
                receipt.getReceiptBusinessNumber(),
                receipt.getReceiptApprovalNumber(),
                receipt.getReceiptShopId(),
                receiptItemResponse
        );
    }
}
