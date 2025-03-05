package com.ssginc.unnie.shop.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.shop.dto.*;
import com.ssginc.unnie.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    /**
     * 업체 마커
     */
    @GetMapping
    public ResponseEntity<ResponseDto<Map<String, Object>>> getAllshop() {
        List<ShopAllResponse> shops = shopService.getAllActiveShops();
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("shops", shops);

        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "업체 조회에 성공했습니다.", responseMap));
    }

            /**
             * 업체 조회
             */

    //위치보기에서 나오는 샵 조회
    // 메인 페이지에서 카테고리별 샵 조회 (예: "헤어샵", "네일샵" 등)
    @GetMapping("/category/{category:.+}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getShopByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "업체 조회에 성공했습니다.", Map.of("shops", shopService.selectShopByCategory(category))));
    }

    /**
     * 업체 상세 보기
     */

    // 샵 기본 정보 조회 (홈 탭)
    @GetMapping("/shopdetails/home/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getShopInfo(
            @PathVariable int shopId) {
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "샵 정보 조회에 성공했습니다.",
                Map.of("shop", shopService.getShopByShopId(shopId))));
    }

    // 디자이너 목록 조회 (디자이너 탭)
    @GetMapping("/shopdetails/designer/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getDesignersByShopId(
            @PathVariable int shopId) {
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "디자이너 조회에 성공했습니다.",
                Map.of("designers", shopService.getDesignersByShopId(shopId))));
    }

    // 시술 목록 조회 (시술 탭)
    @GetMapping("/shopdetails/procedure/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getProceduresByShopId(
            @PathVariable int shopId) {
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "시술 조회에 성공했습니다.",
                Map.of("procedures", shopService.getProceduresByShopId(shopId))));
    }

    // 정보 조회 (정보 탭)
    @GetMapping("/shopdetails/info/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getShopDetailsByShopId(
            @PathVariable int shopId) {
        return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(),"업체 상세정보 조회에 성공했습니다.",
                Map.of("shopDetails", shopService.getShopDetailsByShopId(shopId))));
    }

    // 찜 등록
//    @PostMapping("/shopdetails/bookmark/{shopId}")
//    public ResponseEntity<ResponseDto<Map<String, Integer>>> createBookmark(
//            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
//            @RequestBody ShopBookmarkRequest request,
//            @PathVariable("shopId") int shopId) {
//        long memberId = memberPrincipal.getMemberId();
//
//        request.setBookmarkMemberId(memberId);
//        request.setBookmarkShopId(shopId);
//
//        return ResponseEntity.ok(
//                new ResponseDto<>(HttpStatus.OK.value(),
//                        "찜 목록에 추가되었습니다.",
//                        Map.of("shopId", shopService.createBookmark(request))));
//    }
//
//    @DeleteMapping("/shopdetails/bookmark/{shopId}")
//    public ResponseEntity<ResponseDto<Map<String, Integer>>> deleteBookmark(
//            @AuthenticationPrincipal MemberPrincipal memberPrincipal,
//            @RequestBody ShopBookmarkRequest request,
//            @PathVariable("shopId") int shopId) {
//
//        long currentMemberId = memberPrincipal.getMemberId();
//
//        request.setBookmarkShopId(shopId);
//        request.setBookmarkMemberId(currentMemberId);
//
//        return ResponseEntity.ok(
//                new ResponseDto<>(HttpStatus.OK.value(),
//                        "찜 목록에서 삭제되었습니다.",
//                        Map.of("shopId", shopService.deleteBookmark(request, currentMemberId))));
//    }



}
