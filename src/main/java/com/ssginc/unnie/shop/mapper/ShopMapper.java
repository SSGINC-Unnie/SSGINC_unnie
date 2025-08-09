package com.ssginc.unnie.shop.mapper;

import com.ssginc.unnie.shop.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopMapper {
    List<ShopResponse> selectShopByCategory(String category);

    List<ShopDesignerResponse> findDesignersByShopId(@Param("shopId") int shopId);

    List<ShopProcedureResponse> findProceduresByShopId(int shopId);

    ShopInfoResponse findShopById(int shopId);

    ShopDetailsResponse findShopDetailsById(int shopId);

    List<ShopAllResponse> getAllActiveShops();

    List<ShopAllResponse> selectAllShops();





//    int insertBookmark(ShopBookmarkRequest request);
//
//    int deleteBookmark(int shopId);
//
//   ShopBookmarkRequest findBookmarkByShopIdAndMemberId(@Param("bookmarkShopId") int bookmarkShopId,
//                                                       @Param("bookmarkMemberId") long bookmarkMemberId);
}
