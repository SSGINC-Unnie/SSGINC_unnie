package com.ssginc.unnie.shop.service;

import com.ssginc.unnie.shop.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ShopService {
    List<ShopResponse> selectShopByCategory(String category);

    List<ShopDesignerResponse> getDesignersByShopId(int shopId);

    List<ShopProcedureResponse> getProceduresByShopId(int shopId);

    ShopInfoResponse getShopByShopId(int shopId);

    ShopDetailsResponse getShopDetailsByShopId(int shopId);


//    Integer createBookmark(ShopBookmarkRequest request);
//
//    @Transactional
//    Integer deleteBookmark(ShopBookmarkRequest request, long currentMemberId);
}
