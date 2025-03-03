package com.ssginc.unnie.admin.service;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.admin.dto.shop.AdminShopResponse;
import com.ssginc.unnie.admin.dto.shop.AdminShopUpdateRequest;
import com.ssginc.unnie.admin.dto.shop.GeocodingCoordinate;
import com.ssginc.unnie.mypage.dto.shop.MyShopDetailResponse;
import com.ssginc.unnie.mypage.dto.shop.ShopResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 관리자 게시글/댓글 관리 인터페이스
 */
public interface AdminShopService {
    List<AdminShopResponse> findAllshop();

    MyShopDetailResponse getShopsDetail(int shopId);

    Integer approveShop(AdminShopUpdateRequest request);

    Integer refuseShop(int shopId);


    PageInfo<ShopResponse> findShops(int page, int pageSize);

    MyShopDetailResponse findShopsDetail(int shopId);

    GeocodingCoordinate getCoordinates(String address);


}
