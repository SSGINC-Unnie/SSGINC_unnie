package com.ssginc.unnie.admin.controller;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.admin.dto.shop.AdminShopUpdateRequest;
import com.ssginc.unnie.admin.service.AdminShopService;
import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.mypage.dto.shop.ShopResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/shop")
@RequiredArgsConstructor
public class AdminShopController {

    private final AdminShopService adminShopService;

    /**
     * 승인 요청 업체 전체 목록 조회
     */
    @GetMapping("/approve")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getAllShops() {
        return ResponseEntity.ok(
                new ResponseDto<>(
                        HttpStatus.OK.value(),
                        "승인요청 업체 조회에 성공하였습니다.",
                        Map.of("shop", adminShopService.findAllshop())
                )
        );
    }

    /**
     * 특정 업체 상세 조회 (디자이너, 시술 등 포함)
     */
    @GetMapping("/approve/detail/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getShopDetail(@PathVariable int shopId) {
        return ResponseEntity.ok(
                new ResponseDto<>(
                        HttpStatus.OK.value(),
                        "샵 정보 조회에 성공했습니다.",
                        Map.of("shop", adminShopService.getShopsDetail(shopId))
                )
        );
    }

    /**
     * 업체 승인 (shop_status 변경 및 주소 기반 geocoding 처리 포함)
     */
    @PutMapping("/approve")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> approveShop(
            @RequestBody AdminShopUpdateRequest request) {
        int shopId = adminShopService.approveShop(request);
        return ResponseEntity.ok(
                new ResponseDto<>(
                        HttpStatus.OK.value(),
                        "업체 승인이 완료되었습니다.",
                        Map.of("shopId", shopId)
                )
        );
    }

    /**
     * 업체 거절
     */

    @DeleteMapping("/approve/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Integer>>> refuseShop(
            @PathVariable int shopId) {
        shopId = adminShopService.refuseShop(shopId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(),
                        "업체 거절 처리가 완료되었습니다.",
                        Map.of("shopId", shopId))
        );
    }

    /**
     * 모든 업체 조회
     */

    @GetMapping("")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getShops(
            @RequestParam(defaultValue = "1") int page,        // 페이지 번호
            @RequestParam(defaultValue = "5") int pageSize) { // 페이지 크기

        PageInfo<ShopResponse> shopPage = adminShopService.findShops(page, pageSize);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "모든 업체 조회에 성공하였습니다.", Map.of("shop", shopPage))
        );
    }


    /**
     * 모든 업체 상세 조회
     */

    @GetMapping("/detail/{shopId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getShopsdetail(
            @PathVariable int shopId) {
        return ResponseEntity.ok(
                new ResponseDto<>(
                        HttpStatus.OK.value(),
                        "모든 업체 조회에 성공하였습니다.",
                        Map.of("shop", adminShopService.findShopsDetail(shopId))
                )
        );
    }





}
