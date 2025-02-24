package com.ssginc.unnie.mypage.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssginc.unnie.common.exception.UnnieShopException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.validation.ShopValidator;
import com.ssginc.unnie.mypage.dto.*;
import com.ssginc.unnie.mypage.mapper.MyPageShopMapper;
import com.ssginc.unnie.mypage.service.MyPageShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
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
    public String createShop(ShopCreateRequest request) throws URISyntaxException {

        BusinessVerificationRequest bizRequest = convertFromShopRequest(
                request.getShopRepresentationName(),
                request.getShopBusinessNumber(),
                String.valueOf(request.getShopCreatedAt())
        );
        System.out.println(bizRequest);

        boolean verified = isValidBusinessLicense(bizRequest);
        if(!verified) {
            log.error("Business license is not valid");
            return "null";
        }

        request = ShopCreateRequest.builder()
                .shopTel(request.getShopTel())
                .shopLocation(request.getShopLocation())
                .shopCategory(request.getShopCategory())
                .shopName(request.getShopName())
                .shopIntroduction(request.getShopIntroduction())
                .shopBusinessTime(request.getShopBusinessTime())
                .shopClosedDay(request.getShopClosedDay())
                .shopRepresentationName(request.getShopRepresentationName())
                .shopBusinessNumber(request.getShopBusinessNumber())
                .shopCreatedAt(request.getShopCreatedAt())
                .shopMemberId(request.getShopMemberId())
                .build();

        shopValidator.validate(request);

        boolean isDuplicateName = (myPageShopMapper.existsByShopName(request.getShopName()) > 0);
        shopValidator.validateDuplicateShopName(isDuplicateName);

        // 3) 전화번호 중복 검사
        boolean isDuplicatePhone = (myPageShopMapper.existsByShopTel(request.getShopTel()) > 0);
        shopValidator.validateDuplicatePhoneNumber(isDuplicatePhone);

        int res = myPageShopMapper.insertShop(request);
            return String.valueOf(res);

    }

    /**
     *
     * ===================== 디자이너 등록 메서드 ======================
     *
     */

    @Override
    public String createDesigner(DesignerCreateRequest request) {

        request = DesignerCreateRequest.builder()
                .designerShopId(request.getDesignerShopId())
                .designerName(request.getDesignerName())
                .designerIntroduction(request.getDesignerIntroduction())
                .designerThumbnail(request.getDesignerThumbnail())
                .build();

        shopValidator.validateDesigner(request);

        boolean shopExists = (myPageShopMapper.existsByShopId(request.getDesignerShopId()) > 0);
        shopValidator.validateShopReference(shopExists);

        boolean isDuplicateDesigner = (myPageShopMapper.existsByDesignerName(request.getDesignerName()) > 0);
        if(isDuplicateDesigner) {
            throw new UnnieShopException(ErrorCode.DESIGNER_ALREADY_EXISTS);
        }

        int res = myPageShopMapper.insertDesigner(request);

        return String.valueOf(res);
    }

    /**
     *
     * ======================= 시술 등록 메서드 ==========================
     *
     */


    public String createProcedure(ProcedureCreateRequest request) {

        request = ProcedureCreateRequest.builder()
                .procedureDesignerId(request.getProcedureDesignerId())
                .procedureName(request.getProcedureName())
                .procedurePrice(request.getProcedurePrice())
                .build();

        shopValidator.validateProcedure(request);

        boolean isDuplicateProcedure = (myPageShopMapper.existsByProcedureName(request.getProcedureName()) > 0);
        if(isDuplicateProcedure) {
            throw new UnnieShopException(ErrorCode.PROCEDURE_ALREADY_EXISTS);
        }
        boolean designerExists = (myPageShopMapper.existsByDesignerId(request.getProcedureDesignerId()) > 0);
        shopValidator.validateDesignerReference(designerExists);


        int res = myPageShopMapper.insertProcedure(request);

        return String.valueOf(res);
    }

    /**
     * 외부 API를 호출하여 사업자 진위 여부를 확인합니다.
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
