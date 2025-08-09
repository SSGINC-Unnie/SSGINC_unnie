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
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopMapper shopMapper;
    private final ShopValidator validator;


    @Transactional(readOnly = true)
    @Override
    public List<ShopResponse> selectShopByCategory(String category) {
        if (category == null || category.isEmpty() ||
                Arrays.stream(ShopCategory.values())
                        .noneMatch(enumCategory -> enumCategory.getDescription().equals(category))) {
            throw new UnnieShopException(ErrorCode.SHOP_CATEGORY_NOT_FOUND);
        }
        List<ShopResponse> res = shopMapper.selectShopByCategory(category);

        return res;
    }

    @Override
    public List<ShopAllResponse> getNearbyShops(double userLat, double userLon) {
        List<ShopAllResponse> allShops = shopMapper.selectAllShops();

        return allShops.stream()
                .filter(shop -> {
                    double distance = calculateDistance(userLat, userLon, shop.getShopLatitude(), shop.getShopLongitude());
                    return distance <= 10;
                })
                .collect(Collectors.toList());
    }

    // Haversine 공식
    @Override
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public List<ShopAllResponse> getAllActiveShops() {
        List<ShopAllResponse> res = shopMapper.getAllActiveShops();
        if(res.isEmpty()) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        return res;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShopDesignerResponse> getDesignersByShopId(int shopId) {
        validator.validateShopId(shopId);
        List<ShopDesignerResponse> res = shopMapper.findDesignersByShopId(shopId);
        if(res.isEmpty()) {
            throw new UnnieShopException(ErrorCode.DESIGNER_NOT_FOUND);
        }
        return res;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShopProcedureResponse> getProceduresByShopId(int shopId) {
        validator.validateShopId(shopId);
        List<ShopProcedureResponse> res = shopMapper.findProceduresByShopId(shopId);
        if(res.isEmpty()) {
            throw new UnnieShopException(ErrorCode.PROCEDURE_NOT_FOUND);
        }
        return res;
    }

    @Transactional(readOnly = true)
    @Override
    public ShopInfoResponse getShopByShopId(int shopId) {
        validator.validateShopId(shopId);
        ShopInfoResponse res = shopMapper.findShopById(shopId);
        if (res == null) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        return res;
    }

    @Transactional(readOnly = true)
    @Override
    public ShopDetailsResponse getShopDetailsByShopId(int shopId) {
        validator.validateShopId(shopId);
        ShopDetailsResponse res = shopMapper.findShopDetailsById(shopId);
        if(res == null) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        return res;
    }

//    /**
//     * 찜 등록
//     */
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public Integer createBookmark(ShopBookmarkRequest request) {
//        if (request.getBookmarkShopId() <= 0) {
//            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
//        }
//        int res = shopMapper.insertBookmark(request);
//
//        if(res == 0) {
//            throw new UnnieShopException(ErrorCode.BOOKMARK_INSERT_FAILED);
//        }
//
//        return request.getBookmarkShopId();
//    }
//
//
//    /**
//     * 찜 삭제
//     */
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public Integer deleteBookmark(ShopBookmarkRequest request, long currentMemberId) {
//        // 1. 찜할 업체 ID 유효성 검사
//        if (request.getBookmarkShopId() <= 0) {
//            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
//        }
//
//        // 2. 찜 정보 조회(삭제 전)
//        ShopBookmarkRequest bookmarkInfo = shopMapper.findBookmarkByShopIdAndMemberId(
//                request.getBookmarkShopId(), currentMemberId);
//
//        // 3. 소유자 검증: 조회된 찜 정보의 소유자와 현재 로그인한 사용자가 일치하는지 확인
//        if (bookmarkInfo.getBookmarkMemberId() != currentMemberId) {
//            throw new UnnieShopException(ErrorCode.FORBIDDEN);
//        }
//
//        // 4. 찜 삭제 실행
//        int res = shopMapper.deleteBookmark(request.getBookmarkShopId());
//        if (res == 0) {
//            throw new UnnieShopException(ErrorCode.BOOKMARK_DELETE_FAILED);
//        }
//
//        return request.getBookmarkShopId();
//    }
}