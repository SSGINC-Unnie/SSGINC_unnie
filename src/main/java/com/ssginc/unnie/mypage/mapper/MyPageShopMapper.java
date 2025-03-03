package com.ssginc.unnie.mypage.mapper;

import com.ssginc.unnie.mypage.dto.shop.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    int existsByDesignerName(String designerName);

    int insertProcedure(ProcedureRequest request);

    int existsByProcedureName(String procedureName);

    int existsByDesignerId(int designerid);

    int existsByShopId(int shopid);

    int updateShop(ShopUpdateRequest request);

    int updateDesigner(DesignerRequest request);

    int updateProcedure(ProcedureRequest request);

    int deleteShop(int shopId);

    int deleteShopCascade(int shopId);

    int deleteDesignerCascade(@Param("designerId")int designerId);

    int deleteDesigner(int designerId);

    ProcedureDeleteRequest findProcedureById(@Param("procedureId") int procedureId);

    int deleteProcedure(@Param("procedureId") int procedureId);

    int checkShopOwnership(@Param("shopId") int shopId, @Param("memberId") long memberId);

    int checkProcedureOwnership(@Param("procedureId") int procedureId, @Param("designerId") int designerId,
                                @Param("memberId") long memberId);

    int checkDesignerOwnership(@Param("designerId") int designerId, @Param("memberId") long memberId);

    int findShopIdByDesignerId(@Param("designerId") int designerId);

    ShopDeleteRequest findShopById(@Param("shopId") int shopId);

    DesignerDeleteRequest findDesignerById(@Param("designerId") int designerId);

    List<ShopResponse> findShopsByMemberId(@Param("memberId") long memberId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    MyShopDetailResponse findShopNameById(@Param("shopId") int shopId);

    List<MyDesignerDetailResponse> findDesignersByShopId(@Param("shopId") int shopId);

    List<MyProcedureDetailResponse> findProceduresByDesignerId(@Param("designerId") int designerId);

    int getTotalShopCountByMemberId(long memberId);



}