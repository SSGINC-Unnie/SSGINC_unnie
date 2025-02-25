package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.DesignerRequest;
import com.ssginc.unnie.mypage.dto.ProcedureRequest;
import com.ssginc.unnie.mypage.dto.ShopInsertRequest;
import com.ssginc.unnie.mypage.dto.ShopUpdateRequest;
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
            @RequestBody ShopInsertRequest request) throws URISyntaxException {
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
            @RequestBody DesignerRequest request) {
        request.setDesignerShopId(shopId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.CREATED.value(),"디자이너 등록이 완료되었습니다.",Map.of("DesignerId", myPageShopService.createDesigner(request))));
    }

    /**
     * ======================= 시술 등록 ==========================
     */

    @PostMapping("/procedure/{designerId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> createProcedure(
            @RequestBody ProcedureRequest request,
            @PathVariable("designerId") int designerId) {
        request.setProcedureDesignerId(designerId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.CREATED.value(),"시술 등록이 완료되었습니다.",Map.of("ProcedureId", myPageShopService.createProcedure(request))));
    }

    /**
     * ======================= 업체 수정 ==========================
     */
    @PutMapping("/shop/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> updateShop(
        @PathVariable("shopId") int shopId,
        @RequestBody ShopUpdateRequest request) {
        request.setShopId(shopId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(),"업체 수정이 완료되었습니다.",Map.of("memberId", myPageShopService.updateShop(request))));
    }

    /**
     * ======================= 디자이너 수정 ==========================
     */

    @PutMapping("/designer/{designerId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> updateDesigner(
            @PathVariable("designerId") int designerId,
            @RequestBody DesignerRequest request) {
        request.setDesignerId(designerId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(),"디자이너 수정이 완료되었습니다.",Map.of("shopId",myPageShopService.updateDesigner(request))));
    }

    /**
     * ======================= 시술 수정 ==========================
     */

    @PutMapping("/procedure/{procedureId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> updateProcedure(
            @PathVariable("procedureId") int procedureId,
            @RequestBody ProcedureRequest request) {
        request.setProcedureId(procedureId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(),"시술 수정이 완료되었습니다.",Map.of("designerId",myPageShopService.updateProcedure(request))));
    }

    /**
     * ======================= 업체 삭제 ==========================
     */

    @DeleteMapping("/shop/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> deleteShop(
            @PathVariable("shopId") int shopId) {
        String result = myPageShopService.deleteShop(shopId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "업체 삭제가 완료되었습니다.", Map.of("ShopId", result)));
    }

    /**
     * ======================= 디자이너 삭제 ==========================
     */

    @DeleteMapping("/designer/{designerId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> deleteDesigner(
            @PathVariable("designerId") int designerId) {
        String result = myPageShopService.deleteDesigner(designerId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "디자이너 삭제가 완료되었습니다.", Map.of("DesignerId", result)));
    }

    /**
     * ======================= 시술 삭제 ==========================
     */

    @DeleteMapping("/procedure/{procedureId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> deleteProcedure(
            @PathVariable("procedureId") int procedureId) {
        String result = myPageShopService.deleteProcedure(procedureId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "시술 삭제가 완료되었습니다.", Map.of("ProcedureId", result)));
    }


}
