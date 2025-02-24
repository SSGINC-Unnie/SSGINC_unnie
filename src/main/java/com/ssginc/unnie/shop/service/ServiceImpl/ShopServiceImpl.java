package com.ssginc.unnie.shop.service.ServiceImpl;

import com.ssginc.unnie.common.exception.UnnieShopException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.shop.dto.*;
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
    public List<ShopResponse> selectShopByCategory(String category) {

        if(category == null || category.isEmpty())
        {
            throw new UnnieShopException(ErrorCode.SHOP_CATEGORY_NOT_FOUND);
        }
        List<ShopResponse> res = shopMapper.selectShopByCategory(category);
        return res;
    }


    @Override
    public List<ShopDesignerResponse> getDesignersByShopId(long shopId) {

        if(shopId <= 0) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        List<ShopDesignerResponse> res = shopMapper.findDesignersByShopId(shopId);
        return res;
    }

    @Override
    public List<ShopProcedureResponse> getProceduresByShopId(long shopId) {
        if(shopId <= 0) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        List<ShopProcedureResponse> res = shopMapper.findProceduresByShopId(shopId);
        return res;
    } // 수정은 하는데 수정일이 없음

    @Override
    public ShopInfoResponse getShopByShopId(long shopId) {
        if(shopId <= 0) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        ShopInfoResponse res = shopMapper.findShopById(shopId);
        if(res == null) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        return res;
    }

    @Override
    public ShopDetailsResponse getShopDetailsByShopId(long shopId) {
        if(shopId <= 0) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        ShopDetailsResponse res = shopMapper.findShopDetailsById(shopId);
        return res;
    }
}
