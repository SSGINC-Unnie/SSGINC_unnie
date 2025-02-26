package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.shop.*;
import com.ssginc.unnie.mypage.service.MyPageShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/mypage/")
@RequiredArgsConstructor
@Slf4j
public class MyPageShopController {

    private final MyPageShopService myPageShopService;


    /**
     * ================== 업체 등록 =======================
     */
    @PostMapping("/shop")
    public ResponseEntity<ResponseDto<Map<String, String>>> createShop(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestBody ShopInsertRequest request) throws URISyntaxException {
        long memberId = memberPrincipal.getMemberId();
        request.setShopMemberId((int) memberId);


        String shopId = myPageShopService.createShop(request);
        if (shopId == null || "null".equals(shopId)) {
            return new ResponseEntity<>(
                    new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),
                            "사업자 진위 확인에 실패하였습니다.",
                            Map.of("ShopId", "null")),
                    HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(),
                        "업체 등록이 완료되었습니다",
                        Map.of("ShopId", shopId))
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
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.CREATED.value(), "디자이너 등록이 완료되었습니다.", Map.of("DesignerId", myPageShopService.createDesigner(request))));
    }

    /**
     * ======================= 시술 등록 ==========================
     */

    @PostMapping("/procedure/{designerId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> createProcedure(
            @RequestBody ProcedureRequest request,
            @PathVariable("designerId") int designerId) {
        request.setProcedureDesignerId(designerId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.CREATED.value(), "시술 등록이 완료되었습니다.", Map.of("ProcedureId", myPageShopService.createProcedure(request))));
    }

    /**
     * ======================= 업체 수정 ==========================
     */
    @PutMapping("/manager/shop/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> updateShop(
            @PathVariable("shopId") int shopId,
            @RequestBody ShopUpdateRequest request,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        request.setShopId(shopId);
        String result = myPageShopService.updateShop(request, memberId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "업체 수정이 완료되었습니다.", Map.of("memberId", result)));
    }

    /**
     * ======================= 디자이너 수정 ==========================
     */

    @PutMapping("/manager/designer/{designerId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> updateDesigner(
            @PathVariable("designerId") int designerId,
            @RequestBody DesignerRequest request,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long MemberId = memberPrincipal.getMemberId();
        request.setDesignerId(designerId);
        String result = myPageShopService.updateDesigner(request, MemberId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "디자이너 수정이 완료되었습니다.", Map.of("DesignerId", result))
        );
    }

    /**
     * ======================= 시술 수정 ==========================
     */

    @PutMapping("/manager/procedure/{designerId}/{procedureId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> updateProcedure(
            @PathVariable("designerId") int designerId,
            @PathVariable("procedureId") int procedureId,
            @RequestBody ProcedureRequest request,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        request.setProcedureDesignerId(designerId);
        request.setProcedureId(procedureId);
        long memberId = memberPrincipal.getMemberId();
        String result = myPageShopService.updateProcedure(request, memberId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "시술 수정이 완료되었습니다.", Map.of("ProcedureId", result))
        );
    }

    /**
     * ======================= 업체 삭제 ==========================
     */

    @DeleteMapping("/manager/shop/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> deleteShop(
            @PathVariable("shopId") int shopId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        String result = myPageShopService.deleteShop(shopId, memberId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "업체 삭제가 완료되었습니다.", Map.of("ShopId", result))
        );
    }

    /**
     * ======================= 디자이너 삭제 ==========================
     */

    @DeleteMapping("/manager/designer/{designerId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> deleteDesigner(
            @PathVariable("designerId") int designerId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        String result = myPageShopService.deleteDesigner(designerId, memberId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "디자이너 삭제가 완료되었습니다.", Map.of("DesignerId", result)));
    }

    /**
     * ======================= 시술 삭제 ==========================
     */

    @DeleteMapping("/manager/procedure/{procedureId}")
    public ResponseEntity<ResponseDto<Map<String, String>>> deleteProcedure(
            @PathVariable("procedureId") int procedureId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        {
            String result = myPageShopService.deleteProcedure(procedureId, memberId);
            return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "시술 삭제가 완료되었습니다.", Map.of("ProcedureId", result)));
        }

    }

    /**
     * ======================= 내 업체 조회 =========================
     */

    @GetMapping("/manager/myshops")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getMyShops(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId(); // memberId 타입 확인
        List<MyShopResponse> result = myPageShopService.getMyShops(memberId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "내 업체 목록 조회에 성공하였습니다.", Map.of("shopId",result)));
    }

    /**
     * 업체 상세 조회
     */



}