package com.ssginc.unnie.mypage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessVerificationRequest {
    // 사업자등록번호: 필수, 하이픈 제거된 10자리 숫자 문자열 (API 문서 기준: b_no)
    private String b_no;
    // 개업일자: 필수, YYYYMMDD 형식의 문자열 (API 문서 기준: start_dt)
    private String start_dt;
    // 대표자성명: 필수 (API 문서 기준: p_nm)
    private String p_nm;
}
