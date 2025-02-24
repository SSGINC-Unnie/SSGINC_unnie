package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.DesignerCreateRequest;
import com.ssginc.unnie.mypage.dto.ProcedureCreateRequest;
import com.ssginc.unnie.mypage.dto.ShopCreateRequest;
import com.ssginc.unnie.mypage.service.MyPageShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Map;

@RestController
@RequestMapping("api/mypage/")
@RequiredArgsConstructor
public class MyPageShopController {

    private final MyPageShopService myPageShopService;


    /**
     * ================== 업체 등록 =======================
     */
    @PostMapping("/shop/{memberId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> createShop(
            @PathVariable("memberId") int memberId,
            @RequestBody ShopCreateRequest request) throws URISyntaxException {
        request.setShopMemberId(memberId);
        String shopId = myPageShopService.createShop(request);
        if (shopId == null || "null".equals(shopId)) {
            return new ResponseEntity<>(
                    new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),"사업자 진위 확인에 실패하였습니다.", Map.of("ShopId", "null")), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "업체 등록이 완료되었습니다", Map.of("ShopId", shopId))
        );
    }
    /**
     * ================== 디자이너 등록 =======================
     */

    @PostMapping("/designer/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> createDesigner(
            @PathVariable("shopId") int shopId,
            @RequestBody DesignerCreateRequest request) {
        request.setDesignerShopId(shopId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.CREATED.value(),"디자이너 등록이 완료되었습니다.",Map.of("DesignerId", myPageShopService.createDesigner(request))));
    }

    /**
     * ======================= 시술 등록 ==========================
     */

    @PostMapping("/procedure/{designerId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> createProcedure(
            @RequestBody ProcedureCreateRequest request,
            @PathVariable("designerId") int designerId) {
        request.setProcedureDesignerId(designerId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.CREATED.value(),"시술 등록이 완료되었습니다.",Map.of("ProcedureId", myPageShopService.createProcedure(request))));
    }
}
