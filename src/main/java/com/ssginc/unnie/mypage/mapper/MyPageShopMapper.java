package com.ssginc.unnie.mypage.mapper;

import com.ssginc.unnie.mypage.dto.reservation.DesignerScheduleDto;
import com.ssginc.unnie.mypage.dto.shop.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MyPageShopMapper {
    int insertBusinessVerification(BusinessVerificationRequest request);

    int insertShop(ShopInsertRequest request);

    int existsByShopName(@Param("shopName") String shopName,
                         @Param("shopId") int shopId);

    int existsByShopTel(@Param("shopTel") String shopTel,
                        @Param("shopId") int shopId);

    int insertDesigner(DesignerRequest request);

    int existsByDesignerName(String designerName, int designerId);

    int existByDesignerName(String designerName);


    int insertProcedure(ProcedureRequest request);

    int existsByProcedureName(@Param("procedureName") String procedureName,
                              @Param("shopId") int shopId);

    int existByProcedureName(@Param("procedureName") String procedureName,
                             @Param("shopId") int shopId);

    int existsByShopId(@Param("shopId") int shopId);

    int updateShop(ShopUpdateRequest request);

    int updateDesigner(DesignerRequest request);

    int updateProcedure(ProcedureRequest request);

    int deleteShop(int shopId);

    int deleteShopCascade(int shopId);

    int deleteDesigner(int designerId);

    ProcedureDeleteRequest findProcedureById(@Param("procedureId") int procedureId);

    int deleteProcedure(@Param("procedureId") int procedureId);

    int checkShopOwnership(@Param("shopId") int shopId, @Param("memberId") long memberId);

    int checkProcedureOwnership(@Param("procedureId") int procedureId, @Param("shopId") int shopId,
                                @Param("memberId") long memberId);

    int checkDesignerOwnership(@Param("designerId") int designerId, @Param("memberId") long memberId);

    int findShopIdByDesignerId(@Param("designerId") int designerId);

    ShopDeleteRequest findShopById(@Param("shopId") int shopId);

    DesignerDeleteRequest findDesignerById(@Param("designerId") int designerId);

    List<ShopResponse> findShopsByMemberId(@Param("memberId") long memberId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    MyShopDetailResponse findShopNameById(@Param("shopId") int shopId);

    List<MyDesignerDetailResponse> findDesignersByShopId(@Param("shopId") int shopId);

    List<MyProcedureDetailResponse> findProceduresByShopId(@Param("shopId") int shopId);

    int getTotalShopCountByMemberId(long memberId);


    ShopDetailResponse getShopDetail(int shopId);

    List<String> getShopImages(int shopId);

    List<ShopResponse> findAllShopsByMemberId(@Param("memberId") Long memberId);



}