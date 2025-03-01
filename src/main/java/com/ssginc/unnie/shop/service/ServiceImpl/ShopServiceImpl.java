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
import org.springframework.transaction.annotation.Transactional;

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
    public Integer createBookmark(ShopBookmarkRequest request) {
        shopMapper.insertBookmark(request);
        return request.getBookmarkShopId();
    }

//    @Transactional
//    @Override
//    public Integer deleteBookmark(ShopBookmarkRequest request, long currentMemberId) {
//        // 1. 찜할 업체 ID 유효성 검사
//        if (request.getBookmarkShopId() <= 0) {
//            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
//        }
//
//        // 2. 찜 정보 조회(삭제 전)
//        // (아래 메서드는 bookmarkMemberId와 bookmarkShopId를 기준으로 찜 정보를 조회하는 것으로 가정합니다)
//        ShopBookmarkRequest bookmarkInfo = shopMapper.findBookmarkByShopIdAndMemberId(
//                request.getBookmarkShopId(), currentMemberId);
//        if (bookmarkInfo == null) {
//            throw new UnnieShopException(ErrorCode.BOOKMARK_NOT_FOUND);
//        }
//
//        // 3. 소유자 검증: 조회한 찜 정보의 소유자와 현재 로그인한 회원이 일치하는지 확인
//        if (bookmarkInfo.getBookmarkMemberId() != currentMemberId) {
//            throw new UnnieShopException(ErrorCode.FORBIDDEN);
//        }
//
//        // 4. 찜 삭제 실행
//        int res = shopMapper.deleteBookmark(request);
//        if (res == 0) {
//            throw new UnnieShopException(ErrorCode.BOOKMARK_DELETE_FAILED);
//        }
//        return request.getBookmarkShopId();
//    }



}