package com.ssginc.unnie.mypage.service;

import com.ssginc.unnie.board.dto.BoardCreateRequest;
import com.ssginc.unnie.board.dto.BoardDetailGetResponse;
import com.ssginc.unnie.board.dto.BoardUpdateRequest;
import com.ssginc.unnie.mypage.dto.BusinessVerificationRequest;
import com.ssginc.unnie.mypage.dto.DesignerCreateRequest;
import com.ssginc.unnie.mypage.dto.ProcedureCreateRequest;
import com.ssginc.unnie.mypage.dto.ShopCreateRequest;

import java.net.URISyntaxException;

public interface MyPageShopService {
    String createShop(ShopCreateRequest request) throws URISyntaxException;
    String createDesigner(DesignerCreateRequest request);
    String createProcedure(ProcedureCreateRequest request);
    boolean isValidBusinessLicense(BusinessVerificationRequest request) throws URISyntaxException;
    BusinessVerificationRequest convertFromShopRequest(String shopRepresentationName,
                                                              String shopBusinessNumber,
                                                              String shopCreatedAt);


}
