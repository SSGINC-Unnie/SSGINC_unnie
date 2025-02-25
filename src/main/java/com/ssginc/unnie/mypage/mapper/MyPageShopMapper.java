package com.ssginc.unnie.mypage.mapper;

import com.ssginc.unnie.mypage.dto.*;
import com.ssginc.unnie.shop.vo.Procedure;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyPageShopMapper {
    int insertBusinessVerification(BusinessVerificationRequest request);
    int insertShop(ShopInsertRequest request);
    int existsByShopName(String shopName);
    int existsByShopTel(String shopTel);
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
    int deleteDesignerCascade(int designerId);
    int deleteDesigner(int designerId);
    int deleteProcedure(int procedureId);


}
