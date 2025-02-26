package com.ssginc.unnie.mypage.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssginc.unnie.common.exception.UnnieShopException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.validation.ShopValidator;
import com.ssginc.unnie.mypage.dto.shop.*;
import com.ssginc.unnie.mypage.mapper.MyPageShopMapper;
import com.ssginc.unnie.mypage.service.MyPageShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageShopServiceImpl implements MyPageShopService {

    @Value("${public-api.business.base-url}")
    private String baseUrl;

    @Value("${public-api.business.service-key}")
    private String serviceKey;

    private final MyPageShopMapper myPageShopMapper;
    private final ShopValidator shopValidator;



    /**
     *
     * ===================== 업체 등록 메서드 ========================
     *
     */

    @Override
    public String createShop(ShopInsertRequest request) throws URISyntaxException {

        BusinessVerificationRequest bizRequest = convertFromShopRequest(
                request.getShopRepresentationName(),
                request.getShopBusinessNumber(),
                String.valueOf(request.getShopCreatedAt())
        );

        boolean verified = isValidBusinessLicense(bizRequest);
        if(!verified) {
            log.error("Business license is not valid");
            return "null";
        }


        shopValidator.validate(request);

        boolean isDuplicateName = (myPageShopMapper.existsByShopName(request.getShopName()) > 0);
        if (isDuplicateName) {
            throw new UnnieShopException(ErrorCode.SHOP_ALREADY_EXISTS);
        }

        boolean isDuplicatePhone = (myPageShopMapper.existsByShopTel(request.getShopTel()) > 0);
        if (isDuplicatePhone) {
            throw new UnnieShopException(ErrorCode.DUPLICATE_SHOP_TEL);
        }

        int res = myPageShopMapper.insertShop(request);
        if(res == 0)
        {
            throw new UnnieShopException(ErrorCode.SHOP_INSERT_FAILED);
        }
        return String.valueOf(res);

    }

    /**
     *
     * ===================== 디자이너 등록 메서드 ======================
     *
     */

    @Override
    public String createDesigner(DesignerRequest request) {

        shopValidator.validateDesigner(request);

        boolean shopExists = (myPageShopMapper.existsByShopId(request.getDesignerShopId()) > 0);
        if(!shopExists) {
            throw new UnnieShopException(ErrorCode.SHOP_ALREADY_EXISTS);
        }

        boolean isDuplicateDesigner = (myPageShopMapper.existsByDesignerName(request.getDesignerName()) > 0);
        if(isDuplicateDesigner) {
            throw new UnnieShopException(ErrorCode.DESIGNER_ALREADY_EXISTS);
        }

        int res = myPageShopMapper.insertDesigner(request);
        if(res == 0)
        {
            throw new UnnieShopException(ErrorCode.DESIGNER_INSERT_FAILED);
        }

        return String.valueOf(res);
    }

    /**
     *
     * ======================= 시술 등록 메서드 ==========================
     *
     */


    public String createProcedure(ProcedureRequest request) {


        shopValidator.validateProcedure(request);

        boolean isDuplicateProcedure = (myPageShopMapper.existsByProcedureName(request.getProcedureName()) > 0);
        if(isDuplicateProcedure) {
            throw new UnnieShopException(ErrorCode.PROCEDURE_ALREADY_EXISTS);
        }
        boolean designerExists = (myPageShopMapper.existsByDesignerId(request.getProcedureDesignerId()) > 0);
        if (!designerExists) {
            throw new UnnieShopException(ErrorCode.DESIGNER_NOT_FOUND);
        }

        int res = myPageShopMapper.insertProcedure(request);
        if(res == 0)
        {
            throw new UnnieShopException(ErrorCode.PROCEDURE_INSERT_FAILED);
        }
        // 트랜젝션 어노테이션 추가(lollback ~)
        return String.valueOf(res);
    }

    /**
     * ======================= 업체 수정 =======================
     */

    @Override
    public String updateShop(ShopUpdateRequest request, long memberId) {

        int ownerCount = myPageShopMapper.checkShopOwnership(request.getShopId(), memberId);
        if (ownerCount == 0) {
            throw new UnnieShopException(ErrorCode.FORBIDDEN);
        }

        shopValidator.validateRequiredFields(request);
        shopValidator.validate(request);


        if (request.getShopId() <= 0) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }

        boolean isDuplicateName = (myPageShopMapper.existsByShopName(request.getShopName()) > 0);
        if (isDuplicateName) {
            throw new UnnieShopException(ErrorCode.SHOP_ALREADY_EXISTS);
        }

        boolean isDuplicatePhone = (myPageShopMapper.existsByShopTel(request.getShopTel()) > 0);
        if (isDuplicatePhone) {
            throw new UnnieShopException(ErrorCode.DUPLICATE_SHOP_TEL);
        }

        int res = myPageShopMapper.updateShop(request);
        if (res == 0) {
            throw new UnnieShopException(ErrorCode.SHOP_UPDATE_FAILED);
        }
        return String.valueOf(res);
    }
    /**
     * ======================= 디자이너 수정 =======================
     */


    @Override
    public String updateDesigner(DesignerRequest request, long memberId) {

        int ownerCount = myPageShopMapper.checkDesignerOwnership(request.getDesignerId(), memberId);
        if (ownerCount == 0) {
            throw new UnnieShopException(ErrorCode.FORBIDDEN);
        }

        shopValidator.validateDesigner(request);
        log.info(String.valueOf(request.getDesignerShopId()));

        int shopId = myPageShopMapper.findShopIdByDesignerId(request.getDesignerId());

        if(shopId<= 0) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        request.setDesignerShopId(shopId);

        boolean shopExists = (myPageShopMapper.existsByShopId(request.getDesignerShopId()) > 0);
        if(!shopExists) {
            throw new UnnieShopException(ErrorCode.SHOP_ALREADY_EXISTS);
        }

        boolean isDuplicateDesigner = (myPageShopMapper.existsByDesignerName(request.getDesignerName()) > 0);
        if(isDuplicateDesigner) {
            throw new UnnieShopException(ErrorCode.DESIGNER_ALREADY_EXISTS);
        }


        int res = myPageShopMapper.updateDesigner(request);
        if(res == 0) {
            throw new UnnieShopException(ErrorCode.DESIGNER_UPDATE_FAILED);
        }

        return String.valueOf(res);
    }

    /**
     * ======================= 시술 수정 =======================
     */

    @Override
    public String updateProcedure(ProcedureRequest request, long memberId) {
        log.info(String.valueOf(request.getProcedureId()));
        log.info(String.valueOf(request.getProcedureDesignerId()));

        int ownerCount = myPageShopMapper.checkProcedureOwnership(
                request.getProcedureId(),
                request.getProcedureDesignerId(), // 추가된 designerId 전달
                memberId);
        if (ownerCount == 0) {
            throw new UnnieShopException(ErrorCode.FORBIDDEN);
        }

        shopValidator.validateProcedure(request);

        if(request.getProcedureDesignerId()<=0) {
            throw new UnnieShopException(ErrorCode.DESIGNER_NOT_FOUND);
        }

        boolean isDuplicateProcedure = (myPageShopMapper.existsByProcedureName(request.getProcedureName()) > 0);
        if(isDuplicateProcedure) {
            throw new UnnieShopException(ErrorCode.PROCEDURE_ALREADY_EXISTS);
        }
        boolean designerExists = (myPageShopMapper.existsByDesignerId(request.getProcedureDesignerId()) > 0);
        if (!designerExists) {
            throw new UnnieShopException(ErrorCode.DESIGNER_NOT_FOUND);
        }

        int res = myPageShopMapper.updateProcedure(request);
        if(res ==0) {
            throw new UnnieShopException(ErrorCode.PROCEDURE_UPDATE_FAILED);
        }

        return String.valueOf(res);

    }

    /**
     * ======================= 업체 삭제 =======================
     */

    @Override
    @Transactional
    public String deleteShop(int shopId, long currentMemberId) {
        // 1. shopId 유효성 검증
        if (shopId <= 0) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        // 2. 업체 조회
        ShopDeleteRequest shopInfo = myPageShopMapper.findShopById(shopId);
        if (shopInfo == null) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        // 3. 소유자 검증
        log.info(String.valueOf(shopInfo.getShopMemberId()));
        log.info(String.valueOf(currentMemberId));
        if (shopInfo.getShopMemberId() != currentMemberId) {
            throw new UnnieShopException(ErrorCode.FORBIDDEN);
        }

        // 4. 스토어드 프로시저 호출
        int res = myPageShopMapper.deleteShopCascade(shopId);
        if (res == 0) {
            throw new UnnieShopException(ErrorCode.SHOP_DELETE_FAILED);
        }
        return String.valueOf(res);
    }


    /**
     * ======================= 디자이너 삭제 =======================
     */

    @Transactional
    @Override
    public String deleteDesigner(int designerId, long currentMemberId) {
        if (designerId <= 0) {
            throw new UnnieShopException(ErrorCode.DESIGNER_NOT_FOUND);
        }

        // 디자이너와 관련된 정보를 조회하여 소유자 검증
        DesignerDeleteRequest designerInfo = myPageShopMapper.findDesignerById(designerId);
        if (designerInfo == null) {
            throw new UnnieShopException(ErrorCode.DESIGNER_NOT_FOUND);
        }
        log.info(String.valueOf(designerInfo.getDesignerShopId()));
        log.info(String.valueOf(currentMemberId));


        // 조회된 샵 소유자와 현재 로그인한 사용자가 일치하는지 확인
        if (designerInfo.getShopMemberId() != currentMemberId) {
            throw new UnnieShopException(ErrorCode.FORBIDDEN);
        }

        int res = myPageShopMapper.deleteDesignerCascade(designerId);
        if (res == 0) {
            throw new UnnieShopException(ErrorCode.DESIGNER_DELETE_FAILED);
        }

        return String.valueOf(res);
    }

    /**
     * ======================= 시술 삭제 =======================
     */

    @Transactional
    @Override
    public String deleteProcedure(int procedureId, long currentMemberId) {
        if (procedureId <= 0) {
            throw new UnnieShopException(ErrorCode.PROCEDURE_NOT_FOUND);
        }

        // 1. 시술 정보 조회(삭제 전)
        ProcedureDeleteRequest procInfo = myPageShopMapper.findProcedureById(procedureId);
        if (procInfo == null) {
            throw new UnnieShopException(ErrorCode.PROCEDURE_NOT_FOUND);
        }

        // 2. 소유자 검증: 시술이 속한 업체의 소유자와 현재 사용자가 일치하는지 확인
        if (procInfo.getShopMemberId() != currentMemberId) {
            throw new UnnieShopException(ErrorCode.FORBIDDEN);
        }

        // 3. 시술 삭제 실행
        int res = myPageShopMapper.deleteProcedure(procedureId);
        if (res == 0) {
            throw new UnnieShopException(ErrorCode.PROCEDURE_DELETE_FAILED);
        }
        return String.valueOf(res);
    }

    /**
     * 내 업체 조회
     */

    public List<MyShopResponse> getMyShops(long memberId) {
        List<MyShopResponse> res = myPageShopMapper.findShopsByMemberId(memberId);
        if(res==null) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        if(memberId <= 0) {
            throw new UnnieShopException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return res;
    }

    /**
     * 업체 상세 조회
     */


    /**
     * ======================= 사업자 진위여부 확인 =======================
     */

    public boolean isValidBusinessLicense(BusinessVerificationRequest request) throws URISyntaxException {
        // 1. URI 생성
        URI uri = new URI(baseUrl + serviceKey);

        // 2. RestTemplate 및 헤더 설정
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. 요청 본문 구성: 요청 DTO를 리스트에 담아 "businesses" 키로 감쌈
        List<BusinessVerificationRequest> businesses = new ArrayList<>();
        businesses.add(request);
        Map<String, List<BusinessVerificationRequest>> requestBody = Collections.singletonMap("businesses", businesses);

        HttpEntity<Map<String, List<BusinessVerificationRequest>>> entity = new HttpEntity<>(requestBody, headers);

        // 4. POST 요청 전송
        ResponseEntity<Map> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, entity, Map.class);

        // 5. 응답 본문을 ObjectMapper를 사용해 BusinessValidationResponse로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        Map responseMap = responseEntity.getBody();

        // 응답 JSON의 "data" 필드가 List 형태라고 가정
        List<Map<String, Object>> data = (List<Map<String, Object>>) responseMap.get("data");
        if (data == null || data.isEmpty()) {
            log.error("응답 데이터가 비어 있습니다.");
            return false;
        }
        BusinessValidationResponse validationResponse = objectMapper.convertValue(data.get(0), BusinessValidationResponse.class);
        String valid = validationResponse.getValid();
        log.info("사업자 진위 확인 결과 valid: {}", valid);



        // "02"이면 유효하지 않은 것으로 처리, 그 외에는 유효한 것으로 간주
        return !"02".equals(valid);
    }



    /**
     * ======================= shop 데이터로 컨버트 =======================
     */

    public BusinessVerificationRequest convertFromShopRequest(String shopRepresentationName,
                                                              String shopBusinessNumber,
                                                              String shopCreatedAt) {
        return BusinessVerificationRequest.builder()
                .b_no(shopBusinessNumber)
                .start_dt(shopCreatedAt)
                .p_nm(shopRepresentationName)
                .build();
    }

}