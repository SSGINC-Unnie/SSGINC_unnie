package com.ssginc.unnie.shop.service;

import com.ssginc.unnie.shop.dto.ShopInfoResponse;
import com.ssginc.unnie.shop.dto.ShopResponse;
import com.ssginc.unnie.shop.vo.Designer;
import com.ssginc.unnie.shop.vo.Procedure;

import java.util.List;

public interface ShopService {
    List<ShopResponse> selectShopByCategory(String category);

    List<Designer> getDesignersByShopId(long shopId);

    List<Procedure> getProceduresByShopId(long shopId);

    ShopInfoResponse getShopByShopId(long shopId);
}
