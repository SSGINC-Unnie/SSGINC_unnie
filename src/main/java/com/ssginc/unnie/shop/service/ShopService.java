package com.ssginc.unnie.shop.service;

import com.ssginc.unnie.shop.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ShopService {
    List<ShopResponse> selectShopByCategory(String category);

    List<ShopAllResponse> getNearbyShops(double userLat, double userLon);

    // Haversine 공식
    double calculateDistance(double lat1, double lon1, double lat2, double lon2);

    List<ShopAllResponse> getAllActiveShops();

    List<ShopDesignerResponse> getDesignersByShopId(int shopId);

    List<ShopProcedureResponse> getProceduresByShopId(int shopId);

    ShopInfoResponse getShopByShopId(int shopId);

    ShopDetailsResponse getShopDetailsByShopId(int shopId);

    ShopScheduleInfoDto findScheduleInfoByShopId(Long shopId);


//    Integer createBookmark(ShopBookmarkRequest request);
//
//    @Transactional
//    Integer deleteBookmark(ShopBookmarkRequest request, long currentMemberId);
}
