package com.ssginc.unnie.admin.mapper;

import com.ssginc.unnie.admin.dto.shop.AdminShopResponse;
import com.ssginc.unnie.mypage.dto.shop.MyShopDetailResponse;
import com.ssginc.unnie.mypage.dto.shop.ShopResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminShopMapper {
    List<AdminShopResponse> findAllShops();

    MyShopDetailResponse findShopApproveDetail(@Param("shopId") int shopId);

    int approveShop(@Param("shopId") int shopId);

    int updateShopCoordinates(@Param("shopId") int shopId,
                              @Param("latitude") double latitude,
                              @Param("longitude") double longitude);

    String findShopAddressById(@Param("shopId") int shopId);

    int refuseShop(@Param("shopId") int shopId);


    Integer findShopMemberId(int shopId);

    int updateMemberRole(@Param("memberId") Integer memberId, @Param("role") String role);

    MyShopDetailResponse findShopDetail(@Param("shopId") int shopId);

    List<ShopResponse> findShops(@Param("offset") int offset, @Param("pageSize") int pageSize);

    int getTotalShopCount();



}