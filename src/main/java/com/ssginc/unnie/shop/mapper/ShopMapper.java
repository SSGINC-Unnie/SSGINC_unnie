package com.ssginc.unnie.shop.mapper;

import com.ssginc.unnie.shop.dto.ShopInfoResponse;
import com.ssginc.unnie.shop.dto.ShopResponse;
import com.ssginc.unnie.shop.vo.Designer;
import com.ssginc.unnie.shop.vo.Procedure;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopMapper {
    List<ShopResponse> selectShopByCategory(String category);
    List<Designer> findDesignersByShopId(@Param("shopId") Long shopId);
    List<Procedure> findProceduresByShopId(long shopId);

    ShopInfoResponse findShopById(long shopId);
}
