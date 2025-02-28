package com.ssginc.unnie.common.util;

/**
 * 시스템에서 발생할 수 있는 다양한 에러 코드와 메시지를 정의하는 Enum 클래스
 * <p>
 * 각 에러 코드와 대응되는 메시지 관리
 * 에러 식별을 위한 고유 코드 제공
 */
public enum ErrorCode {

    // 회원 관련 에러
    ACCOUNT_ALREADY_EXISTS(400, "ME001", "이미 가입된 계정입니다."),
    EMAIL_ALREADY_EXISTS(400, "ME002", "이미 존재하는 이메일입니다."),
    INVALID_EMAIL_FORMAT(400, "ME003", "유효하지 않은 이메일 형식입니다."),
    EMAIL_VERIFICATION_FAILED(400, "ME004", "이메일 인증에 실패했습니다."),
    DUPLICATE_NICKNAME(400, "ME005", "이미 존재하는 닉네임입니다."),
    INVALID_PHONE_FORMAT(400, "ME006", "유효하지 않은 전화번호 형식입니다."),
    PHONE_VERIFICATION_FAILED(400, "ME007", "전화번호 인증에 실패했습니다."),
    MEMBER_REGISTRATION_FAILED(500, "ME008", "회원가입 중 오류가 발생했습니다."),
    VERIFICATION_CODE_MISMATCH(400, "ME009", "인증번호가 일치하지 않습니다."),
    MEMBER_NOT_FOUND(401, "ME010", "존재하지 않는 회원입니다."),
    INVALID_PASSWORD(401, "ME011", "비밀번호가 일치하지 않습니다."),
    ID_NOT_FOUND(404, "ME012", "해당 아이디를 찾을 수 없습니다."),
    EMAIL_SEND_FAILED(500, "ME013", "이메일 전송에 실패했습니다."),
    SMS_SEND_FAILED(500, "ME014", "SMS 전송에 실패했습니다."),
    MEMBER_UPDATE_FAILED(500, "ME015", "회원 정보 수정 중 오류가 발생했습니다."),
    MEMBER_DELETION_FAILED(500, "ME016", "회원 탈퇴 중 오류가 발생했습니다."),
    INVALID_PASSWORD_FORMAT(400, "ME017", "비밀번호 형식이 올바르지 않습니다."),
    INVALID_NAME_FORMAT(400, "ME018", "이름 형식이 올바르지 않습니다."),
    INVALID_NICKNAME_FORMAT(400, "ME019", "닉네임 형식이 올바르지 않습니다."),
    INVALID_BIRTH_FORMAT(400, "ME020", "생년월일 형식이 올바르지 않습니다."),
    LOGIN_FAILED(401, "ME021", "로그인에 실패했습니다."),
    EMAIL_INPUT_EMPTY(400, "ME022", "이메일 입력값이 누락되었습니다."),
    PHONE_INPUT_EMPTY(400, "ME023", "전화번호 입력값이 누락되었습니다."),
    PASSWORD_UPDATE_FAILED(500, "ME024", "비밀번호 변경에 실패했습니다." ),
    //JWT 관련 에러
    INVALID_ACCESS_TOKEN(401, "JWT001", "유효하지 않은 Access Token입니다."),
    EXPIRED_ACCESS_TOKEN(401, "JWT002", "Access Token이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(401, "JWT003", "유효하지 않은 Refresh Token입니다."),
    EXPIRED_REFRESH_TOKEN(401, "JWT004", "Refresh Token이 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(400, "JWT005", "Refresh Token이 존재하지 않습니다."),

    // =================================== 뷰티샵 관련 에러 =====================================================

    // 뷰티샵 조회
    NO_SHOP_DATA_IN_RANGE(404, "SH022", "해당 반경 내에 뷰티샵이 없습니다."),
    INVALID_ADDRESS(400, "SH023", "유효하지 않은 주소입니다."),
    LOCATION_PERMISSION_DENIED(403, "SH024", "위치 접근 권한이 거부되었습니다."),
    GEOCODING_FAILED(400, "SH025", "주소를 좌표로 변환하는 중 오류가 발생했습니다."),
    NO_BOOKMARK_FOUND(404, "SH026", "찜한 매장이 없습니다."),
    PROCEDURE_ALREADY_EXISTS(409, "SH027", "시술이 이미 등록되어 있습니다."),
//    INVALID_NAME_FORMAT(400,"SH028","이름 형식이 올바르지 않습니다."),
    INVALID_TEL_FORMAT(400,"SH029","업체 전화번호 형식이 올바르지 않습니다."),
    INVALID_INTRODUCTION_FORMAT(400,"SH030","소개 형식이 올바르지 않습니다"),
    SHOP_CATEGORY_NOT_FOUND(404, "SH031", "카테고리가 옳지 않습니다."),

    // 마이페이지(업체)
    SHOP_NOT_FOUND(404, "SH001", "해당 업체 정보를 찾을 수 없습니다."),
    SHOP_ALREADY_EXISTS(409, "SH002", "이미 존재하는 업체입니다."),
    DUPLICATE_BUSINESS_NUMBER(409, "SH003", "이미 등록된 사업자등록번호입니다."),
    DUPLICATE_SHOP_TEL(409, "SH004", "이미 사용 중인 전화번호입니다."),
    SHOP_VERIFICATION_FAILED(500, "SH005", "사업자 진위여부 확인 중 오류가 발생했습니다."),
    INVALID_BUSINESS_NUMBER(400, "SH006", "사업자 등록번호가 일치하지 않습니다."),
    SHOP_MISSING_REQUIRED_FIELD(400, "SH007", "등록 중 필수 입력값이 누락되었습니다."),
    INVALID_SHOP_DATA(400, "SH008", "업체 정보가 유효하지 않습니다."),
    SHOP_INSERT_FAILED(500, "SH009", "업체 등록 중 오류가 발생했습니다."),
    SHOP_UPDATE_FAILED(500, "SH010", "업체 수정 중 오류가 발생했습니다."),
    SHOP_DELETE_FAILED(500, "SH011", "업체 삭제 중 오류가 발생했습니다."),
    DESIGNER_ALREADY_EXISTS(409, "SH012", "이미 등록된 디자이너입니다."),
    DESIGNER_INSERT_FAILED(500, "SH013", "디자이너 등록 중 오류가 발생했습니다."),
    DESIGNER_UPDATE_FAILED(500, "SH014", "디자이너 수정 중 오류가 발생했습니다."),
    DESIGNER_DELETE_FAILED(500, "SH015", "디자이너 삭제 중 오류가 발생했습니다."),
    PROCEDURE_INSERT_FAILED(500, "SH016", "시술 등록 중 오류가 발생했습니다."),
    PROCEDURE_UPDATE_FAILED(500, "SH017", "시술 수정 중 오류가 발생했습니다."),
    PROCEDURE_DELETE_FAILED(500, "SH018", "시술 삭제 중 오류가 발생했습니다."),
    INVALID_DESIGNER_NAME_FORMAT(400,"SH032","디자이너 이름 형식이 올바르지 않습니다."),
    INVALID_PROCEDURE_PRICE(400, "SH033", "시술 가격이 유효하지 않습니다."),
    DESIGNER_NOT_FOUND(404, "SH034","해당 디자이너를 찾을 수 없습니다."),
    PROCEDURE_NOT_FOUND(404,"SH035","해당 시술을 찾을 수 없습니다,"),

    // 관리자(업체)
    ALREADY_APPROVED(409, "SH019", "이미 승인(또는 거부) 처리된 업체입니다."),
    SHOP_LIST_NOT_FOUND(404, "SH020", "업체 목록을 찾을 수 없습니다."),
    NO_MATCHING_DATA(404, "SH021", "필터 조건에 해당하는 업체가 없습니다."),

    // =================================== 리뷰 관련 에러 =====================================================
    REVIEW_CATEGORY_REQUIRED(400, "RV001", "리뷰 카테고리는 필수 입력 항목입니다."),
    REVIEW_CONTENT_REQUIRED(400, "RV002", "리뷰 내용은 필수 입력 항목입니다."),
    REVIEW_FILE_REQUIRED(400, "RV003", "최소 1장의 이미지를 첨부해야 합니다."),
    INVALID_REVIEW_CONTENT(400, "RV004", "리뷰 내용이 형식에 맞지 않습니다."),
    REVIEW_LENGTH_EXCEEDED(400, "RV005", "리뷰 길이가 제한을 초과했습니다."),
    DUPLICATE_REVIEW(400, "RV006", "동일한 리뷰를 중복 작성할 수 없습니다."),
    EXPIRED_RECEIPT(400, "RV007", "영수증 사용 기한이 만료되었습니다."),
    INVALID_RECEIPT(400, "RV008", "유효하지 않은 영수증입니다."),
    OCR_PROCESSING_FAILED(400, "RV009", "OCR 분석 중 오류가 발생했습니다."),
    INVALID_AI_ANALYSIS(400, "RV010", "AI 감정 분석 처리 중 오류가 발생했습니다."),
    REVIEW_NOT_FOUND(404, "RV011", "요청한 리뷰를 찾을 수 없습니다."),
    USER_NOT_FOUND(404, "RV012", "사용자를 찾을 수 없습니다."),
    RECEIPT_NOT_FOUND(404, "RV013", "영수증을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(404, "RV014", "요청한 카테고리를 찾을 수 없습니다."),
    DUPLICATE_REVIEW_EDIT(409, "RV015", "동일한 내용으로 리뷰를 수정할 수 없습니다."),
    DUPLICATE_REVIEW_DELETE(409, "RV016", "이미 삭제된 리뷰입니다."),
    AI_ANALYSIS_FAILED(422, "RV017", "AI 감정 분석에 실패했습니다."),
    TEXT_EXTRACTION_FAILED(422, "RV018", "리뷰 텍스트 추출 중 오류가 발생했습니다."),
    REVIEW_DELETE_FAILED(500, "RV019", "리뷰 삭제 중 오류가 발생했습니다."),
    REVIEW_UPDATE_FAILED(500, "RV020", "리뷰 수정 중 오류가 발생했습니다."),
    REVIEW_SEARCH_FAILED(500, "RV021", "리뷰 정보를 가져오는 중 오류가 발생했습니다."),

    // =================================== 게시글 관련 에러 =====================================================
    BOARD_CATEGORY_REQUIRED(400, "BO001", "게시글 카테고리는 필수 입력 항목입니다."),
    BOARD_TITLE_REQUIRED(400, "BO002", "게시글 제목은 필수 입력 항목입니다."),
    BOARD_CONTENT_REQUIRED(400, "BO003", "게시글 내용은 필수 입력 항목입니다."),
    BOARD_FILE_REQUIRED(400, "BO004", "최소 1장의 이미지를 첨부해야 합니다."),
    BOARD_NOT_FOUND(404, "BO005", "요청한 게시글을 찾을 수 없습니다."),
    BOARD_CONTENT_LENGTH_INVALID(400, "BO006", "게시글 내용은 10자 이상 800자 이하여야 합니다."),
    BOARD_CREATE_FAILED(500, "BO007", "게시글 작성에 실패했습니다."),
    BOARD_SEARCH_NOT_FOUND(400, "BO008", "검색 결과가 존재하지 않습니다."),
    BOARD_SEARCH_QUERY_REQUIRED(404, "BO009", "검색어가 존재하지 않습니다."),
    BOARD_DELETE_FAILED(500, "BO010", "게시글 삭제 중 오류가 발생했습니다."),
    BOARD_UPDATE_FAILED(500, "BO011", "게시글 수정 중 오류가 발생했습니다."),
    BOARD_SEARCH_FAILED(500, "BO012", "게시글 정보를 가져오는 중 오류가 발생했습니다."),
    BOARD_NOT_INVALID(400, "BO013", "게시글 작성 형식이 올바르지 않습니다."),
    BOARD_INVALID_SEARCH_TYPE(400, "BO013", "요청한 검색 타입이 유효하지 않습니다."),
    BOARD_INVALID_SORT_TYPE(400, "BO014", "요청한 정렬 방식이 유효하지 않습니다."),
    BOARD_INVALID_CATEGORY(400, "BO015", "요청한 카테고리가 유효하지 않습니다."),

    // =================================== 댓글 관련 에러 =====================================================
    COMMENT_CREATE_FAILED(500, "CO001", "댓글 작성에 실패했습니다."),
    COMMENT_UPDATE_FAILED(500, "CO002", "댓글 수정에 실패했습니다."),
    COMMENT_DELETE_FAILED(500, "CO003", "댓글 삭제에 실패했습니다."),
    COMMENT_CONTENT_REQUIRED(400, "CO004", "댓글 내용은 필수 입력 항목입니다."),
    COMMENT_LENGTH_INVALID(400, "CO005", "댓글 내용은 100자 이하여야 합니다."),
    COMMENT_NOT_FOUND(404, "CO006", "요청한 댓글을 찾을 수 없습니다."),
    COMMENT_SELECT_FAILED(500, "CO007", "댓글을 가져오는 데 실패했습니다."),
    COMMENT_INVALID_WORD(400, "CO008", "금칙어가 포함되어 있습니다."),
    COMMENT_PARENT_NOT_FOUND(404, "CO009", "원댓글을 찾을 수 없습니다."),
    COMMENT_BOARD_NOT_FOUND(404, "CO010", "댓글 게시글을 찾을 수 없습니다."),

    // =================================== 좋아요 관련 에러 =====================================================
    LIKE_CREATE_FAILED(500, "LI001", "좋아요 중 오류가 발생했습니다"),
    LIKE_NOT_FOUND(404, "LI002", "좋아요 대상을 찾을 수 없습니다."),
    LIKE_INVALID_TARGET_TYPE(400, "LI003", "좋아요 타입이 적절하지 않습니다."),
    LIKE_DELETE_FAILED(500, "LI004", "좋아요 취소 중 오류가 발생했습니다."),

    // =================================== 신고 관련 에러 =====================================================
    REPORT_CREATE_FAILED(500, "RE001", "신고 접수에 실패했습니다."),
    REPORT_REASON_REQUIRED(400, "RE002", "신고 사유는 필수 입력 항목입니다."),
    REPORT_TYPE_INVALID(400, "RE003", "잘못된 신고 유형입니다."),
    REPORT_SELF_FORBIDDEN(403, "RE004", "자신의 컨텐츠는 신고할 수 없습니다."),
    REPORT_NOT_FOUND(404, "RE005", "신고 대상이 존재하지 않습니다."),
    REPORT_DUPLICATE(409, "RE006", "동일한 컨텐츠를 중복 신고할 수 없습니다."),
    REPORT_LENGTH_INVALID(400, "RE007", "신고 상세내용은 300자 이하여야 합니다."),
    REPORT_SELECT_FAILED(500, "RE008", "신고 내용을 가져오는 중 오류가 발생했습니다."),
    REPORT_UPDATE_FAILED(500, "RE009", "신고 처리 중 오류가 발생했습니다."),
    REPORT_INVALID_STATUS(400, "RE010", "요청하신 신고 상태 값이 적절하지 않습니다."),
    REPORT_INVALID_DATE(400, "RE011", "요청하신  날짜값이 적절하지 않습니다."),
    REPORT_INVALID_TARGET_TYPE(400, "RE012", "요청하신 신고 타입이 적절하지 않습니다."),
    ADMIN_REPORT_NOT_FOUND(404, "RE013", "요청하신 신고가 존재하지 않습니다."),
    REPORTED_CONTENT_DELETE_FAILED(500, "RE014", "신고 컨텐츠 삭제 중 오류가 발생했습니다."),
    // =================================== 알림 관련 에러 =====================================================
    INVALID_NOTIFICATION_REQUEST(400, "NT001", "잘못된 알림 요청입니다."),
    NOTIFICATION_NOT_FOUND(404, "NT002", "해당 알림을 찾을 수 없습니다."),
    NOTIFICATION_SERVICE_ERROR(500, "NT003", "알림 서비스 내부 오류가 발생했습니다."),

    // =================================== 파일 관련 에러 =====================================================
    INVALID_FILE_TYPE(400, "IM001", "지원하지 않는 파일 형식입니다."),
    FILE_TOO_LARGE(400, "IM002", "파일 용량이 30MB를 초과했습니다."),
    FILE_UPLOAD_FAILED(500, "IM003", "파일 업로드에 실패했습니다."),
    FILE_NOT_FOUND(404, "IM004", "요청한 파일을 찾을 수 없습니다."),
    FILE_DUPLICATE(409, "IM005", "동일한 파일이 이미 존재합니다."),
    FILE_INTERNAL_SERVER_ERROR(500, "IM006", "파일 정보를 가져오는 중 오류가 발생했습니다."),
    INVALID_FILE_TARGET_TYPE(400, "IM007", "파일 대상 타입이 올바르지 않습니다"),

    // =================================== 공통 에러 =====================================================
    UNKNOWN_ERROR(500, "CM001", "알 수 없는 오류가 발생했습니다."),
    FORBIDDEN(403, "CM002", "권한이 없습니다."),
    DB_CONNECTION_ERROR(500, "CM003", "DB 연결 오류가 발생했습니다."),
    INVALID_PAGINATION_PARAM(400, "CM004", "페이지네이션 파라미터가 유효하지 않습니다."),
    PAGE_OUT_OF_RANGE(400, "CM005", "요청한 페이지가 범위를 벗어났습니다."),
    NULL_POINTER_ERROR(500, "CM006", "값이 존재하지 않습니다."),
    INVALID_CATEGORY(400, "CM007", "유효하지 않은 카테고리 입니다.");

    private final int status;
    private final String code;
    private final String msg;

    /**
     * 에러 코드 및 메시지를 초기화합니다.
     *
     * @param status 상태 코드
     * @param code 에러 코드. 예외 원인 식별화
     * @param msg  에러 메시지
     */
    ErrorCode(int status, String code, String msg) {
        this.status = status;
        this.code = code;
        this.msg = msg;
    }

    /**
     * 상태 코드를 반환합니다.
     *
     * @return 상태 코드
     */
    public int getStatus() {
        return status;
    }

    /**
     * 에러 코드를 반환합니다.
     *
     * @return 에러 코드
     */
    public String getCode() {
        return code;
    }

    /**
     * 에러 메시지를 반환합니다.
     *
     * @return 에러 메시지
     */
    public String getMsg() {
        return msg;
    }

}
