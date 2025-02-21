package com.ssginc.unnie.review.mapper;

import com.ssginc.unnie.review.dto.ReceiptRequest;
import com.ssginc.unnie.review.dto.ReceiptResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReceiptMapper {
    void insertReceipt(ReceiptRequest receiptRequest);

    ReceiptResponse findReceiptById(@Param("receiptId")Long receiptId);
}
