package com.ssginc.unnie.mypage.controller;

import com.github.pagehelper.PageInfo;
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
import org.springframework.web.multipart.MultipartFile;

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


        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(),
                        "업체 등록이 완료되었습니다",
                        Map.of("shopId",myPageShopService.createShop(request)))
        );
    }

    /**
     * ================== 디자이너 등록 =======================
     */

    @PostMapping("/designer/{shopId}")
    public ResponseEntity<ResponseDto<?>> createDesigners(
            @PathVariable("shopId") int shopId,
            @RequestPart("data") List<DesignerRequest> requests,
            @RequestPart("designerThumbnailFiles") List<MultipartFile> files) {

        // 각 요청에 shopId 설정
        requests.forEach(request -> request.setDesignerShopId(shopId));

        // 서비스에서 전체 목록에 대해 트랜잭션 처리
        myPageShopService.createDesigners(requests, files);

        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.CREATED.value(),
                "디자이너 등록이 완료되었습니다.", null));
    }

    /**
     * ======================= 시술 등록 ==========================
     */

    @PostMapping("/procedure/{shopId}")
    public ResponseEntity<ResponseDto<?>> createProcedures(
            @PathVariable("shopId") int shopId,
            @RequestPart("data") List<ProcedureRequest> requests,
            @RequestPart("procedureThumbnailFiles") List<MultipartFile> files) {

        // 각 요청에 shopId 설정
        requests.forEach(request -> request.setProcedureShopId(shopId));

        // 서비스에서 전체 목록에 대해 트랜잭션 처리
        myPageShopService.createProcedures(requests, files);

        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.CREATED.value(),
                "시술 등록이 완료되었습니다.", null));
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
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "업체 수정이 완료되었습니다.", Map.of("shopId",myPageShopService.updateShop(request, memberId))));
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

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "디자이너 수정이 완료되었습니다.", Map.of("DesignerId", myPageShopService.updateDesigner(request, MemberId))));
    }

    /**
     * ======================= 시술 수정 ==========================
     */

    @PutMapping("/manager/procedure/{procedureId}")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> updateProcedure(
            @PathVariable("procedureId") int procedureId,
            @RequestBody ProcedureRequest request,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        request.setProcedureId(procedureId);
        long memberId = memberPrincipal.getMemberId();
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "시술 수정이 완료되었습니다.", Map.of("ProcedureId", myPageShopService.updateProcedure(request, memberId))));
    }

    /**
     * ======================= 업체 삭제 ==========================
     */

    @DeleteMapping("/manager/shop/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> deleteShop(
            @PathVariable("shopId") int shopId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "업체 삭제가 완료되었습니다.", Map.of("ShopId",myPageShopService.deleteShop(shopId, memberId))));
    }

    /**
     * ======================= 디자이너 삭제 ==========================
     */

    @DeleteMapping("/manager/designer/{designerId}")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> deleteDesigner(
            @PathVariable("designerId") int designerId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "디자이너 삭제가 완료되었습니다.", Map.of("DesignerId", myPageShopService.deleteDesigner(designerId, memberId))));
    }

    /**
     * ======================= 시술 삭제 ==========================
     */

    @DeleteMapping("/manager/procedure/{procedureId}")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> deleteProcedure(
            @PathVariable("procedureId") int procedureId,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
            return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "시술 삭제가 완료되었습니다.", Map.of("ProcedureId", myPageShopService.deleteProcedure(procedureId, memberId))));
    }

    /**
     * ======================= 내 업체 조회 =========================
     */

    @GetMapping("/manager/myshops")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getMyShops(
            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
            @RequestParam(defaultValue = "1") int page,        // 페이지 번호
            @RequestParam(defaultValue = "5") int pageSize) { // 페이지 크기

        long memberId = memberPrincipal.getMemberId();

        PageInfo<ShopResponse> shopPage = myPageShopService.getMyShops(memberId, page, pageSize);

        return ResponseEntity.ok(new ResponseDto<>(
                HttpStatus.OK.value(),
                "내 업체 목록 조회에 성공하였습니다.",
                Map.of("shop", shopPage)
        ));
    }
    /**
     * 업체 상세 조회
     */

    @GetMapping("/manager/shopdetail/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getMyShopDetail(
            @PathVariable int shopId) {

        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "샵 정보 조회에 성공했습니다.", Map.of("shop",myPageShopService.getMyShopsDetail(shopId))));
    }

    /**
     * 디자이너 조회
     */

    @GetMapping("/manager/designer/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getDesigners (
            @PathVariable int shopId) {
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "디자이너 조회에 성공했습니다.", Map.of("shop",myPageShopService.getDesignersByShopId(shopId))));
    }

    /**
     * 시술 조회
     */

    @GetMapping("/manager/procedure/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getProcedures (
            @PathVariable int shopId) {
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "시술 조회에 성공했습니다.", Map.of("shop",myPageShopService.getProceduresByShopId(shopId))));
    }




}