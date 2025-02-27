package com.ssginc.unnie.mypage.service;

import com.ssginc.unnie.mypage.dto.shop.*;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.List;

public interface MyPageShopService {
    String createShop(ShopInsertRequest request) throws URISyntaxException;
    String createDesigner(DesignerRequest request);
    String createProcedure(ProcedureRequest request);
    String updateShop(ShopUpdateRequest request, long memberId);
    String updateDesigner(DesignerRequest request, long memberId);
    String updateProcedure(ProcedureRequest request, long memberId);
    String deleteShop(int shopId, long currentMemberId);
    String deleteDesigner(int designerId, long currentMemberId);
    String deleteProcedure(int procedureId, long currentMemberId);
    boolean isValidBusinessLicense(BusinessVerificationRequest request) throws URISyntaxException;
    BusinessVerificationRequest convertFromShopRequest(String shopRepresentationName,
                                                       String shopBusinessNumber,
                                                       String shopCreatedAt);
    List<MyShopResponse> getMyShops(long memberId);
    MyShopDetailResponse getMyShopsDetail(int shopId);

}