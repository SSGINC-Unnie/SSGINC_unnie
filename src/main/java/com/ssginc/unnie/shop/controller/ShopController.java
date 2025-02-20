package com.ssginc.unnie.shop.controller;

import com.ssginc.unnie.shop.dto.ShopDetailsRequest;
import com.ssginc.unnie.shop.dto.ShopDetailsResponse;
import com.ssginc.unnie.shop.dto.ShopResponse;
import com.ssginc.unnie.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;


    @GetMapping("/category")
    public ResponseEntity<List<ShopResponse>> getShopAll(
            @RequestParam(defaultValue = "헤어샵") String category) {
        List<ShopResponse> shops = shopService.selectShopByCategory(category);
    }
}