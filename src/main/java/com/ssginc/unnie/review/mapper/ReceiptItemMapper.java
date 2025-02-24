package com.ssginc.unnie.review.mapper;

import com.ssginc.unnie.review.dto.ReceiptItemRequest;
import com.ssginc.unnie.review.dto.ReceiptItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReceiptItemMapper {
    void insertReceiptItem(ReceiptItemRequest receiptItemRequest);

    List<ReceiptItemResponse> findItemsByReceiptId(@Param("receiptId")Long receiptId);
}
