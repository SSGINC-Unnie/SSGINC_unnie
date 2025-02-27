package com.ssginc.unnie.mypage.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.exception.UnnieShopException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.shop.*;
import com.ssginc.unnie.mypage.service.MyPageShopService;
import com.ssginc.unnie.shop.dto.ShopInfoResponse;
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
    public ResponseEntity<ResponseDto<Map<String, Integer>>> createShop(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestBody ShopInsertRequest request) {
        long memberId = memberPrincipal.getMemberId();
        request.setShopMemberId((int) memberId);


        String shop = myPageShopService.createShop(request);
        if (shop == null || "null".equals(shop)) {
            return new ResponseEntity<>(
                    new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),
                            "사업자 진위 확인에 실패하였습니다.",
                            Map.of("ShopId", 0)),
                    HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(),
                        "업체 등록이 완료되었습니다",
                        Map.of("ShopId", request.getShopId()))
        );
    }

    /**
     * ================== 디자이너 등록 =======================
     */

    @PostMapping("/designer/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> createDesigner(
            @PathVariable("shopId") int shopId,
            @RequestBody DesignerRequest request) {
        request.setDesignerShopId(shopId);
        myPageShopService.createDesigner(request);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.CREATED.value(), "디자이너 등록이 완료되었습니다.", Map.of("DesignerId", request.getDesignerId())));
    }

    /**
     * ======================= 시술 등록 ==========================
     */

    @PostMapping("/procedure/{designerId}")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> createProcedure(
            @RequestBody ProcedureRequest request,
            @PathVariable("designerId") int designerId) {
        request.setProcedureDesignerId(designerId);
        myPageShopService.createProcedure(request);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.CREATED.value(), "시술 등록이 완료되었습니다.", Map.of("ProcedureId", request.getProcedureId())));
    }

    /**
     * ======================= 업체 수정 ==========================
     */
    @PutMapping("/manager/shop/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> updateShop(
            @PathVariable("shopId") int shopId,
            @RequestBody ShopUpdateRequest request,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        request.setShopId(shopId);
        myPageShopService.updateShop(request, memberId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "업체 수정이 완료되었습니다.", Map.of("shopId", request.getShopId())));
    }

    /**
     * ======================= 디자이너 수정 ==========================
     */

    @PutMapping("/manager/designer/{designerId}")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> updateDesigner(
            @PathVariable("designerId") int designerId,
            @RequestBody DesignerRequest request,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long MemberId = memberPrincipal.getMemberId();
        request.setDesignerId(designerId);
        myPageShopService.updateDesigner(request, MemberId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "디자이너 수정이 완료되었습니다.", Map.of("DesignerId", request.getDesignerId()))
        );
    }

    /**
     * ======================= 시술 수정 ==========================
     */

    @PutMapping("/manager/procedure/{designerId}/{procedureId}")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> updateProcedure(
            @PathVariable("designerId") int designerId,
            @PathVariable("procedureId") int procedureId,
            @RequestBody ProcedureRequest request,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        request.setProcedureDesignerId(designerId);
        request.setProcedureId(procedureId);
        long memberId = memberPrincipal.getMemberId();
        myPageShopService.updateProcedure(request, memberId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "시술 수정이 완료되었습니다.", Map.of("ProcedureId", request.getProcedureId()))
        );
    }

    /**
     * ======================= 업체 삭제 ==========================
     */

    @DeleteMapping("/manager/shop/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> deleteShop(
            @PathVariable("shopId") int shopId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        myPageShopService.deleteShop(shopId, memberId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "업체 삭제가 완료되었습니다.", Map.of("ShopId", shopId))
        );
    }

    /**
     * ======================= 디자이너 삭제 ==========================
     */

    @DeleteMapping("/manager/designer/{designerId}")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> deleteDesigner(
            @PathVariable("designerId") int designerId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        String result = myPageShopService.deleteDesigner(designerId, memberId);
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "디자이너 삭제가 완료되었습니다.", Map.of("DesignerId", designerId)));
    }

    /**
     * ======================= 시술 삭제 ==========================
     */

    @DeleteMapping("/manager/procedure/{procedureId}")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> deleteProcedure(
            @PathVariable("procedureId") int procedureId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        {
            String result = myPageShopService.deleteProcedure(procedureId, memberId);
            return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "시술 삭제가 완료되었습니다.", Map.of("ProcedureId", procedureId)));
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
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "내 업체 목록 조회에 성공하였습니다.", Map.of("shop",result)));
    }

    /**
     * 업체 상세 조회
     */

    @GetMapping("/manager/shopdetail/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getMyShopDetail(
            @PathVariable int shopId) {
        MyShopDetailResponse shopdetail = myPageShopService.getMyShopsDetail(shopId);
        if (shopdetail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDto<>(HttpStatus.NOT_FOUND.value(), "해당 업체를 찾을 수 없습니다.", null));
        }

        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "샵 정보 조회에 성공했습니다.", Map.of("shop",shopdetail)));
    }



}