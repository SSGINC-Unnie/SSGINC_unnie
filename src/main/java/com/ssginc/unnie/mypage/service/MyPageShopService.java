package com.ssginc.unnie.mypage.service;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.mypage.dto.shop.*;

import java.net.URISyntaxException;
import java.util.List;

public interface MyPageShopService {
    Integer createShop(ShopInsertRequest request);
    Integer createDesigner(DesignerRequest request);
    Integer createProcedure(ProcedureRequest request);
    Integer updateShop(ShopUpdateRequest request, long memberId);
    Integer updateDesigner(DesignerRequest request, long memberId);
    Integer updateProcedure(ProcedureRequest request, long memberId);
    Integer deleteShop(int shopId, long currentMemberId);
    Integer deleteDesigner(int designerId, long currentMemberId);
    Integer deleteProcedure(int procedureId, long currentMemberId);
    boolean isValidBusinessLicense(BusinessVerificationRequest request) throws URISyntaxException;
    BusinessVerificationRequest convertFromShopRequest(String shopRepresentationName,
                                                       String shopBusinessNumber,
                                                       String shopCreatedAt);

    PageInfo<ShopResponse> getMyShops(long memberId, int page, int pageSize);

    MyShopDetailResponse getMyShopsDetail(int shopId);

}