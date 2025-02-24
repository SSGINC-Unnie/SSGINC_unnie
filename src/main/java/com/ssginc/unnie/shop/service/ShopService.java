package com.ssginc.unnie.shop.service;

import com.ssginc.unnie.shop.dto.*;

import java.util.List;

public interface ShopService {
    List<ShopResponse> selectShopByCategory(String category);

    List<ShopDesignerResponse> getDesignersByShopId(long shopId);

    List<ShopProcedureResponse> getProceduresByShopId(long shopId);

    ShopInfoResponse getShopByShopId(long shopId);

    ShopDetailsResponse getShopDetailsByShopId(long shopId);
}
