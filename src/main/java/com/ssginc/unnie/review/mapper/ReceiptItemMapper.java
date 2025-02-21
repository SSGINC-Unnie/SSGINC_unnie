package com.ssginc.unnie.review.mapper;

import com.ssginc.unnie.review.vo.ReceiptItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReceiptItemMapper {
    void insertReceiptItem(ReceiptItem receiptItem);

    List<ReceiptItem> findItemsByReceiptId(@Param("receiptId")Long receiptId);
}
