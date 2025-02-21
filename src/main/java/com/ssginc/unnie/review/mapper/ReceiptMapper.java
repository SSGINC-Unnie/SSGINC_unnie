package com.ssginc.unnie.review.mapper;

import com.ssginc.unnie.review.vo.Receipt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReceiptMapper {
    void insertReceipt(Receipt receipt);

    Receipt findReceiptById(@Param("receiptId")Long receiptId);
}
