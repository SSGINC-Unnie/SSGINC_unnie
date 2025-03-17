package com.ssginc.unnie.review.mapper;

import com.ssginc.unnie.review.dto.ReceiptRequest;
import com.ssginc.unnie.review.dto.ReceiptResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReceiptMapper {

    void insertReceipt(ReceiptRequest receiptRequest);

    ReceiptResponse findReceiptById(@Param("receiptId") Long receiptId);

    int findShopIdByName(@Param("shopName") String shopName);

    /**
     * 영수증 ID(receiptId)를 기준으로 해당 영수증에 연결된 shopId를 조회합니다.
     *
     * @param receiptId 영수증 ID
     * @return 해당 영수증의 shopId
     */
    long getShopIdByReceiptId(long receiptId);

}
