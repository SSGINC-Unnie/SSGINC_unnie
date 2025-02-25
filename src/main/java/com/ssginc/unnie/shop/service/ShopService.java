package com.ssginc.unnie.shop.service;

import com.ssginc.unnie.shop.dto.*;

import java.util.List;

public interface ShopService {
    List<ShopResponse> selectShopByCategory(String category);

    List<ShopDesignerResponse> getDesignersByShopId(int shopId);

    List<ShopProcedureResponse> getProceduresByShopId(int shopId);

    ShopInfoResponse getShopByShopId(int shopId);

    ShopDetailsResponse getShopDetailsByShopId(int shopId);

    String createBookmark(ShopBookmarkRequest request);
}
