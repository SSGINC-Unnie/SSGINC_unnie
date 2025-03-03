package com.ssginc.unnie.admin.service.serviceImpl;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.admin.dto.shop.AdminShopResponse;
import com.ssginc.unnie.admin.dto.shop.AdminShopUpdateRequest;
import com.ssginc.unnie.admin.dto.shop.GeocodingCoordinate;
import com.ssginc.unnie.admin.dto.shop.GeocodingResponse;
import com.ssginc.unnie.admin.mapper.AdminShopMapper;
import com.ssginc.unnie.admin.service.AdminShopService;
import com.ssginc.unnie.common.exception.UnnieShopException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.mypage.dto.shop.MyDesignerDetailResponse;
import com.ssginc.unnie.mypage.dto.shop.MyProcedureDetailResponse;
import com.ssginc.unnie.mypage.dto.shop.MyShopDetailResponse;
import com.ssginc.unnie.mypage.dto.shop.ShopResponse;
import com.ssginc.unnie.mypage.mapper.MyPageShopMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminShopServiceImpl implements AdminShopService {

    private final AdminShopMapper adminShopMapper;
    private final RestTemplate restTemplate;
    private final MyPageShopMapper myPageShopMapper;

    @Value("${naver.geocoding.clientId}")
    private String clientId;

    @Value("${naver.geocoding.clientSecret}")
    private String clientSecret;

    @Value("${naver.geocoding.url}")
    private String geocodingUrl;

    /**
     * 승인 요청 업체 조회
     */

    @Transactional(readOnly = true)
    @Override
    public List<AdminShopResponse> findAllshop() {
        List<AdminShopResponse> res = adminShopMapper.findAllShops();
        if (res == null) {
            throw new UnnieShopException(ErrorCode.SHOP_LIST_NOT_FOUND);
        }
        return res;
    }

    /**
     *
     * 승입 요청 업체 상세 조회
     */

    @Transactional(readOnly = true)
    @Override
    public MyShopDetailResponse getShopsDetail(int shopId) {
        MyShopDetailResponse shopdetail = adminShopMapper.findShopApproveDetail(shopId);
        if(shopdetail == null)
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        List<MyDesignerDetailResponse> designers = myPageShopMapper.findDesignersByShopId(shopId);
        if (designers != null) {
            for (MyDesignerDetailResponse designer : designers) {
                List<MyProcedureDetailResponse> procedures = myPageShopMapper.findProceduresByDesignerId(designer.getDesignerId());
                designer.setProcedures(procedures);
            }
        }
        shopdetail.setDesigners(designers);
        return shopdetail;
    }


    /**
     * 업체 승인 (shop_status를 0에서 1로 변경)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer approveShop(AdminShopUpdateRequest request) {
        // 1. 업체 승인: shop_status를 0에서 1로 업데이트
        int updatedRows = adminShopMapper.approveShop(request.getShopId());
        if (updatedRows == 0) {
            log.error("업체 승인 실패 - shopId: {}", request.getShopId());
            throw new UnnieShopException(ErrorCode.ALREADY_APPROVED);
        }

        // 2. 업체 주소 조회 (예: shopMapper.findShopAddressById를 통해)
        String address = adminShopMapper.findShopAddressById(request.getShopId());
        if (address != null && !address.isEmpty()) {
            // 3. 지오코딩 API를 호출하여 위도, 경도 변환
            GeocodingCoordinate coord = getCoordinates(address);
            // 4. 변환된 좌표로 DB 업데이트
            adminShopMapper.updateShopCoordinates(request.getShopId(), coord.getLatitude(), coord.getLongitude());
        }

        return request.getShopId();
    }

    /**
     * 업체 거절 처리: 아직 승인되지 않은 업체(shop_status = 0)를 삭제한다.
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer refuseShop(int shopId) {

        int deletedRows = adminShopMapper.refuseShop(shopId);
        if (deletedRows == 0) {
            log.error("업체 거절 처리 실패 - shopId: {}", shopId);
            throw new UnnieShopException(ErrorCode.SHOP_REFUSE_FAILED);
        }
        log.info("업체 거절 처리 성공 - shopId: {}", shopId);
        return shopId;
    }

    /**
     * 모든 업체 조회
     */

    @Transactional(readOnly = true)
    @Override
    public PageInfo<ShopResponse> findShops(int page, int pageSize) {
        // 페이지네이션을 위한 OFFSET 계산
        int offset = (page - 1) * pageSize;

        // 페이지네이션을 고려하여 업체 목록 조회
        List<ShopResponse> res = adminShopMapper.findShops(offset, pageSize);

        // 전체 업체 개수 조회 (전체 페이지네이션을 위한 사용)
        int totalCount = adminShopMapper.getTotalShopCount();

        if (res == null || res.isEmpty()) {
            throw new UnnieShopException(ErrorCode.SHOP_LIST_NOT_FOUND);
        }

        // PageInfo 객체 생성
        PageInfo<ShopResponse> pageInfo = new PageInfo<>(res);
        pageInfo.setTotal(totalCount); // 전체 데이터 개수 설정
        pageInfo.setPageSize(pageSize); // 페이지 크기 설정

        return pageInfo;
    }


    /**
     *
     * 모든 업체 상세 조회
     */

    @Transactional(readOnly = true)
    @Override
    public MyShopDetailResponse findShopsDetail(int shopId) {
        MyShopDetailResponse shopdetail = adminShopMapper.findShopDetail(shopId);
        if(shopdetail == null)
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        List<MyDesignerDetailResponse> designers = myPageShopMapper.findDesignersByShopId(shopId);
        if (designers != null) {
            for (MyDesignerDetailResponse designer : designers) {
                List<MyProcedureDetailResponse> procedures = myPageShopMapper.findProceduresByDesignerId(designer.getDesignerId());
                designer.setProcedures(procedures);
            }
        }
        shopdetail.setDesigners(designers);
        return shopdetail;
    }

    /**
     * 네이버 Geocoding API를 이용하여 주소를 위도/경도로 변환
     */

    @Override
    public GeocodingCoordinate getCoordinates(String address) {
        // 입력된 address 확인
        log.info("getCoordinates() called with address = {}", address);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-NCP-APIGW-API-KEY-ID", clientId);
            headers.add("X-NCP-APIGW-API-KEY", clientSecret);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            log.info("Calling Naver Geocoding API with URL={} and address={}", geocodingUrl, address);

            ResponseEntity<GeocodingResponse> response = restTemplate.exchange(
                    geocodingUrl,
                    HttpMethod.GET,
                    entity,
                    GeocodingResponse.class,
                    address // URI 변수 {address}에 바인딩
            );

            log.info("Naver Geocoding API response: status={}, body={}",
                    response.getStatusCode(), response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                GeocodingResponse geocodeResponse = response.getBody();
                if (geocodeResponse.getAddresses() != null && !geocodeResponse.getAddresses().isEmpty()) {
                    GeocodingResponse.Address addr = geocodeResponse.getAddresses().get(0);
                    double latitude = Double.parseDouble(addr.getY());
                    double longitude = Double.parseDouble(addr.getX());
                    log.info("Parsed coordinates => latitude={}, longitude={}", latitude, longitude);
                    return new GeocodingCoordinate(latitude, longitude);
                } else {
                    log.warn("GeocodeResponse is empty or addresses list is null. address={}", address);
                }
            } else {
                log.warn("Geocoding API call failed or returned empty body. status={}, body={}",
                        response.getStatusCode(), response.getBody());
            }
            throw new UnnieShopException(ErrorCode.GEOCODING_FAILED);
        } catch (Exception ex) {
            log.error("Error while geocoding address={}", address, ex);
            throw new UnnieShopException(ErrorCode.GEOCODING_FAILED, ex);
        }
    }


}
