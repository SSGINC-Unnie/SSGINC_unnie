package com.ssginc.unnie.mypage.service;

import com.ssginc.unnie.mypage.dto.*;

import java.net.URISyntaxException;

public interface MyPageShopService {
    String createShop(ShopInsertRequest request) throws URISyntaxException;
    String createDesigner(DesignerRequest request);
    String createProcedure(ProcedureRequest request);

    String updateShop(ShopUpdateRequest request);
    String updateDesigner(DesignerRequest request);
    String updateProcedure(ProcedureRequest request);
    String deleteShop(int shopId);
    String deleteDesigner(int designerId);
    String deleteProcedure(int procedureId);
    boolean isValidBusinessLicense(BusinessVerificationRequest request) throws URISyntaxException;

    BusinessVerificationRequest convertFromShopRequest(String shopRepresentationName,
                                                       String shopBusinessNumber,
                                                       String shopCreatedAt);

}
