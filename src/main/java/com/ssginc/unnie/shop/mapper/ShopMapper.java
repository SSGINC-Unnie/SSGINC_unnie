package com.ssginc.unnie.shop.mapper;

import com.ssginc.unnie.shop.dto.ShopDetailsRequest;
import com.ssginc.unnie.shop.dto.ShopDetailsResponse;
import com.ssginc.unnie.shop.dto.ShopResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShopMapper {
    List<ShopResponse> selectShopByCategory(String category);


}
