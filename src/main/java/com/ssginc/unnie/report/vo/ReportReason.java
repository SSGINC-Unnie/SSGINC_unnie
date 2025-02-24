package com.ssginc.unnie.report.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 신고 사유 ENUM 클래스
 */
@Getter
@AllArgsConstructor
public enum ReportReason {
    INAPPROPRIATE_CONTENT("부적절한 컨텐츠(욕설/비속어/음란물/ 성희롱/폭력적/정치적)"),
    ADVERTISEMENT("광고 및 홍보(상업적 광고 또는 스팸성 게시물)"),
    PERSONAL_INFORMATION("개인정보 유출(본인/타인의 전화번호,주소,계좌번호 등 노출)"),
    COPYRIGHT_VIOLATION("저작권 및 지적 재산권 침해(타인의 저작물을 무단 도용 및 게시)"),
    COMMUNITY_RULE_VIOLATION("커뮤니티 규칙 위반(주제와 맞지 않는 게시물, 도배 및 반복 게시, 타인을 저격하는 글 또는 분쟁 유도 글)"),
    ETC("기타");

    private final String description;
}
