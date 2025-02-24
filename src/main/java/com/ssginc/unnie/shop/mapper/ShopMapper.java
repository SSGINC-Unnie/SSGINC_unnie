package com.ssginc.unnie.shop.mapper;

import com.ssginc.unnie.shop.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopMapper {
    List<ShopResponse> selectShopByCategory(String category);
    List<ShopDesignerResponse> findDesignersByShopId(@Param("shopId") Long shopId);
    List<ShopProcedureResponse> findProceduresByShopId(long shopId);
    ShopInfoResponse findShopById(long shopId);
    ShopDetailsResponse findShopDetailsById(long shopId);
}
