package com.ssginc.unnie.mypage.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer createShop(ShopInsertRequest request) {

        String cleanedDate = request.getShopCreatedAt().replace("-", "");
        request.setShopCreatedAt(cleanedDate);

        BusinessVerificationRequest bizRequest = convertFromShopRequest(
                request.getShopRepresentationName(),
                request.getShopBusinessNumber(),
                String.valueOf(request.getShopCreatedAt())
        );

            boolean verified = isValidBusinessLicense(bizRequest);

        if(!verified) {
            log.error("Business license is not valid");
            throw new UnnieShopException(ErrorCode.INVALID_BUSINESS_NUMBER);
        }


        shopValidator.validate(request);

        boolean isDuplicateName = (myPageShopMapper.existsByShopName(request.getShopName(),request.getShopId()) > 0);
        if (isDuplicateName) {
            throw new UnnieShopException(ErrorCode.SHOP_ALREADY_EXISTS);
        }

        boolean isDuplicatePhone = (myPageShopMapper.existsByShopTel(request.getShopTel(),request.getShopId()) > 0);
        if (isDuplicatePhone) {
            throw new UnnieShopException(ErrorCode.DUPLICATE_SHOP_TEL);
        }

        int res = myPageShopMapper.insertShop(request);
        if(res == 0)
        {
            throw new UnnieShopException(ErrorCode.SHOP_INSERT_FAILED);
        }
        return request.getShopId();

    }

    /**
     *
     * ===================== 디자이너 등록 메서드 ======================
     *
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createDesigners(List<DesignerRequest> requests, List<MultipartFile> files) {
        if(requests.size() != files.size()) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        for (int i = 0; i < requests.size(); i++) {
            DesignerRequest request = requests.get(i);
            MultipartFile file = files.get(i);
            String fileUrl = saveFile(file);
            request.setDesignerThumbnail(fileUrl);

            shopValidator.validateDesigner(request);

            if(myPageShopMapper.existsByShopId(request.getDesignerShopId()) == 0) {
                throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
            }
            if(myPageShopMapper.existsByDesignerName(request.getDesignerName()) > 0) {
                throw new UnnieShopException(ErrorCode.DESIGNER_ALREADY_EXISTS);
            }

            int res = myPageShopMapper.insertDesigner(request);
            if (res == 0) {
                throw new UnnieShopException(ErrorCode.DESIGNER_INSERT_FAILED);
            }
        }
    }



    /**
     *
     * ======================= 시술 등록 메서드 ==========================
     *
     */


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createProcedures(List<ProcedureRequest> requests, List<MultipartFile> files) {


        for (int i = 0; i < requests.size(); i++) {
            ProcedureRequest request = requests.get(i);
            MultipartFile file = files.get(i);

            // 파일 저장 (임시: 로컬 저장소)
            String fileUrl = saveFile(file);
            request.setProcedureThumbnail(fileUrl);

            // 시술 유효성 검사
            shopValidator.validateProcedure(request);

            // 중복 시술명 체크
            boolean isDuplicateProcedure = (myPageShopMapper.existsByProcedureName(request.getProcedureName(), request.getProcedureShopId()) > 0);
            if (isDuplicateProcedure) {
                throw new UnnieShopException(ErrorCode.PROCEDURE_ALREADY_EXISTS);
            }

            // shop 존재 여부 체크
            boolean shopExists = (myPageShopMapper.existsByShopId(request.getProcedureShopId()) > 0);
            if (!shopExists) {
                throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
            }

            // 시술 등록
            int res = myPageShopMapper.insertProcedure(request);
            if (res == 0) {
                throw new UnnieShopException(ErrorCode.PROCEDURE_INSERT_FAILED);
            }
        }
    }


    public String saveFile(MultipartFile file) {
        // 외부에 생성할 폴더 경로 (운영 환경에 맞게 수정하세요)
        String uploadDir = "C:/upload/shop/";
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            folder.mkdirs();  // 폴더가 없으면 생성
        }

        // 원본 파일명과 안전한 파일명 생성 (UUID를 이용하여 중복 및 한글, 특수문자 처리)
        String originalFileName = file.getOriginalFilename();
        String safeFileName = UUID.randomUUID().toString() + "_"
                + originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");

        // 최종 파일 객체 생성
        File destination = new File(folder, safeFileName);
        try {
            file.transferTo(destination);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }

        // DB에는 웹 접근 가능한 경로를 저장 (WebConfig에서 매핑한 경로와 동일하게)
        return "/upload/" + safeFileName;
    }

    /**
     * ======================= 업체 수정 =======================
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateShop(ShopUpdateRequest request, long memberId) {

        int ownerCount = myPageShopMapper.checkShopOwnership(request.getShopId(), memberId);
        if (ownerCount == 0) {
            throw new UnnieShopException(ErrorCode.FORBIDDEN);
        }

        shopValidator.validateRequiredFields(request);
        shopValidator.validate(request);


        if (request.getShopId() <= 0) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }

        boolean isDuplicateName = (myPageShopMapper.existsByShopName(request.getShopName(), request.getShopId()) > 0);
        if (isDuplicateName) {
            throw new UnnieShopException(ErrorCode.SHOP_ALREADY_EXISTS);
        }

        boolean isDuplicatePhone = (myPageShopMapper.existsByShopTel(request.getShopTel(),request.getShopId()) > 0);
        if (isDuplicatePhone) {
            throw new UnnieShopException(ErrorCode.DUPLICATE_SHOP_TEL);
        }

        int res = myPageShopMapper.updateShop(request);
        if (res == 0) {
            throw new UnnieShopException(ErrorCode.SHOP_UPDATE_FAILED);
        }
        return request.getShopId();
    }
    /**
     * ======================= 디자이너 수정 =======================
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateDesigner(DesignerRequest request, long memberId) {

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

        return request.getDesignerId();
    }

    /**
     * ======================= 시술 수정 =======================
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer updateProcedure(ProcedureRequest request, long memberId) {
        log.info(String.valueOf(request.getProcedureId()));

        int ownerCount = myPageShopMapper.checkProcedureOwnership(
                request.getProcedureId(),
                request.getProcedureShopId(), // 추가된 designerId 전달
                memberId);

        if (ownerCount == 0) {
            throw new UnnieShopException(ErrorCode.FORBIDDEN);
        }

        shopValidator.validateProcedure(request);

        if(request.getProcedureShopId()<=0) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }

        boolean isDuplicateProcedure = (myPageShopMapper.existsByProcedureName(request.getProcedureName(),request.getProcedureShopId()) > 0);
        if(isDuplicateProcedure) {
            throw new UnnieShopException(ErrorCode.PROCEDURE_ALREADY_EXISTS);
        }
        boolean shopExists = (myPageShopMapper.existsByShopId(request.getProcedureShopId()) > 0);
        if(!shopExists) {
            throw new UnnieShopException(ErrorCode.SHOP_ALREADY_EXISTS);
        }

        int res = myPageShopMapper.updateProcedure(request);
        if(res ==0) {
            throw new UnnieShopException(ErrorCode.PROCEDURE_UPDATE_FAILED);
        }

        return request.getProcedureId();

    }

    /**
     * ======================= 업체 삭제 =======================
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer deleteShop(int shopId, long currentMemberId) {
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
        return shopId;
    }


    /**
     * ======================= 디자이너 삭제 =======================
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer deleteDesigner(int designerId, long currentMemberId) {
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

        int res = myPageShopMapper.deleteDesigner(designerId);
        if (res == 0) {
            throw new UnnieShopException(ErrorCode.DESIGNER_DELETE_FAILED);
        }



        return designerId;
    }

    /**
     * ======================= 시술 삭제 =======================
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer deleteProcedure(int procedureId, long currentMemberId) {
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
        return procedureId;
    }

    /**
     * 내 업체 조회
     */

    @Transactional(readOnly = true)
    @Override
    public PageInfo<ShopResponse> getMyShops(long memberId, int page, int pageSize) {
        if (memberId <= 0) {
            throw new UnnieShopException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 페이지네이션을 위한 OFFSET 계산
        int offset = (page - 1) * pageSize;

        // 페이지네이션을 고려하여 업체 목록 조회
        List<ShopResponse> res = myPageShopMapper.findShopsByMemberId(memberId, offset, pageSize);
        log.info(String.valueOf(res));

        // 전체 업체 개수 조회 (전체 페이지네이션을 위한 사용)
        int totalCount = myPageShopMapper.getTotalShopCountByMemberId(memberId);

        if (res == null || res.isEmpty()) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }

        // PageInfo 객체 생성
        PageInfo<ShopResponse> pageInfo = new PageInfo<>(res);
        pageInfo.setTotal(totalCount); // 전체 데이터 개수 설정
        pageInfo.setPageSize(pageSize); // 페이지 크기 설정

        return pageInfo;
    }

    /**
     * 업체 상세 조회
     */

    @Transactional(readOnly = true)
    @Override
    public MyShopDetailResponse getMyShopsDetail(int shopId) {
        MyShopDetailResponse shopDetail = myPageShopMapper.findShopNameById(shopId);
        if (shopDetail == null) {
            throw new UnnieShopException(ErrorCode.SHOP_NOT_FOUND);
        }
        // 디자이너 조회 (업체-디자이너 관계는 그대로)
        List<MyDesignerDetailResponse> designers = myPageShopMapper.findDesignersByShopId(shopId);
        shopDetail.setDesigners(designers);

        // 변경: 디자이너별 시술 조회 대신 업체 단위로 시술 조회
        List<MyProcedureDetailResponse> procedures = myPageShopMapper.findProceduresByShopId(shopId);
        shopDetail.setProcedures(procedures); // MyShopDetailResponse에 시술 목록 추가 (필요하다면 DTO 수정)

        return shopDetail;
    }


    /**
     * ======================= 사업자 진위여부 확인 =======================
     */

    @Override
    public boolean isValidBusinessLicense(BusinessVerificationRequest request) {
        // 1. URI 생성
        URI uri = null;
        try {
            uri = new URI(baseUrl + serviceKey);
        } catch (URISyntaxException e) {
            throw new UnnieShopException(ErrorCode.URI_SYNTAX_ERROR);
        }

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