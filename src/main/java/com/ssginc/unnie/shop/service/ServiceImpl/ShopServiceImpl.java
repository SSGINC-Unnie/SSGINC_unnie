package com.ssginc.unnie.shop.service.ServiceImpl;

import com.ssginc.unnie.shop.dto.ShopInfoResponse;
import com.ssginc.unnie.shop.dto.ShopResponse;
import com.ssginc.unnie.shop.mapper.ShopMapper;
import com.ssginc.unnie.shop.service.ShopService;
import com.ssginc.unnie.shop.vo.Designer;
import com.ssginc.unnie.shop.vo.Procedure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopMapper shopMapper;

    @Override
    public List<ShopResponse> selectShopByCategory(String category) {
        return shopMapper.selectShopByCategory(category);
    }


    @Override
    public List<Designer> getDesignersByShopId(long shopId) {
        return shopMapper.findDesignersByShopId(shopId);
    }

    @Override
    public List<Procedure> getProceduresByShopId(long shopId) {
        return shopMapper.findProceduresByShopId(shopId);
    } // 수정은 하는데 수정일이 없음

    @Override
    public ShopInfoResponse getShopByShopId(long shopId) {
        return shopMapper.findShopById(shopId);
    }
}
