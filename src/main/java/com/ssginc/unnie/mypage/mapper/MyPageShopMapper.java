package com.ssginc.unnie.mypage.mapper;

import com.ssginc.unnie.mypage.dto.BusinessVerificationRequest;
import com.ssginc.unnie.mypage.dto.DesignerCreateRequest;
import com.ssginc.unnie.mypage.dto.ProcedureCreateRequest;
import com.ssginc.unnie.mypage.dto.ShopCreateRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyPageShopMapper {
    int insertBusinessVerification(BusinessVerificationRequest request);
    int insertShop(ShopCreateRequest request);
    int existsByShopName(String shopName);
    int existsByShopTel(String shopTel);
    int insertDesigner(DesignerCreateRequest request);
    int existsByDesignerName(String designerName);
    int insertProcedure(ProcedureCreateRequest request);
    int existsByProcedureName(String procedureName);
    int existsByDesignerId(int designerid);
    int existsByShopId(int shopid);

}
