package com.ssginc.unnie.community.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글 상세 조회 전용 DTO 클래스
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDetailGetResponse {
    private long boardId; // 게시글 식별 번호
    private String boardCategory; // 게시글 카테고리
    private long boardAuthor; // 작성자 식별 번호
    private String boardTitle; // 게시글 제목
    private String boardContents; // 내용
    private String boardCreatedAt; // 작성일
    private long boardViews; // 조회 수
    private int likeCount; // 좋아요 수
    private boolean isOwner; // 작성자 확인
    private String authorNickname;
    private String authorProfile;
}
