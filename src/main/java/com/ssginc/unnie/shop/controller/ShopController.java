package com.ssginc.unnie.shop.controller;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.shop.dto.ShopResponse;
import com.ssginc.unnie.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    @GetMapping("/category/{category}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getShopByCategory(
            @PathVariable String category,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "5") int size) {
        List<ShopResponse> shops = shopService.selectShopByCategory(category, cursor, size);
        System.out.println("Received category: " + category);
        ResponseDto<Map<String, Object>> response =
                new ResponseDto<>(HttpStatus.OK.value(), "업체 조회에 성공했습니다.", Map.of("shops", shops));
        return ResponseEntity.ok(response);
    }
}

