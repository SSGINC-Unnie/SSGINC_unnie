package com.ssginc.unnie.shop.service.ServiceImpl;

import com.ssginc.unnie.common.util.ResponseDto;
import com.ssginc.unnie.common.util.validation.Validator;
import com.ssginc.unnie.shop.dto.ShopDetailsRequest;
import com.ssginc.unnie.shop.dto.ShopDetailsResponse;
import com.ssginc.unnie.shop.dto.ShopResponse;
import com.ssginc.unnie.shop.mapper.ShopMapper;
import com.ssginc.unnie.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopMapper shopMapper;

    @Override
    public List<ShopResponse> selectShopByCategory(String category, Long cursor, int size) {
        return shopMapper.selectShopByCategory(category, cursor, size);
    }

}
