package com.ssginc.unnie.shop.service.ServiceImpl;

import com.ssginc.unnie.common.exception.UnnieShopException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.validation.ShopValidator;
import com.ssginc.unnie.shop.dto.*;
import com.ssginc.unnie.shop.mapper.ShopMapper;
import com.ssginc.unnie.shop.service.ShopService;
import com.ssginc.unnie.shop.vo.ShopCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopMapper shopMapper;
    private final ShopValidator validator;


    @Override
    public List<ShopResponse> selectShopByCategory(String category) {
        if (category == null || category.isEmpty() ||
                Arrays.stream(ShopCategory.values())
                        .noneMatch(enumCategory -> enumCategory.getDescription().equals(category))) {
            throw new UnnieShopException(ErrorCode.SHOP_CATEGORY_NOT_FOUND);
        }
        List<ShopResponse> res = shopMapper.selectShopByCategory(category);
        if(res.isEmpty()) {
            throw new UnnieShopException(ErrorCode.SHOP_LIST_NOT_FOUND);
        }
        return res;
    }

    @Override
    public List<ShopDesignerResponse> getDesignersByShopId(int shopId) {
        validator.validateShopId(shopId);
        List<ShopDesignerResponse> res = shopMapper.findDesignersByShopId(shopId);
        if(res.isEmpty()) {
            throw new UnnieShopException(ErrorCode.DESIGNER_NOT_FOUND);
        }
        return res;
    }

    @Override
    public List<ShopProcedureResponse> getProceduresByShopId(int shopId) {
        validator.validateShopId(shopId);
        List<ShopProcedureResponse> res = shopMapper.findProceduresByShopId(shopId);
        if(res.isEmpty()) {
            throw new UnnieShopException(ErrorCode.PROCEDURE_NOT_FOUND);
        }
        return res;
    }

    @Override
    public ShopInfoResponse getShopByShopId(int shopId) {
        validator.validateShopId(shopId);
        ShopInfoResponse res = shopMapper.findShopById(shopId);
        if (res == null) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        return res;
    }

    @Override
    public ShopDetailsResponse getShopDetailsByShopId(int shopId) {
        validator.validateShopId(shopId);
        ShopDetailsResponse res = shopMapper.findShopDetailsById(shopId);
        if(res == null) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        return res;
    }

    @Override
    public String createBookmark(ShopBookmarkRequest request) {
        // bookmark 관련 로직 구현
        return "";
    }


}