package com.ssginc.unnie.board.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 비회원 전용 게시글 목록 조회 DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardsGuestGetResponse {
    private long boardAuthor; // 작성자 식별 번호
    private String memberNickname; // 작성자 닉네임
    private String memberProfile; // 작성자 프로필 사진
    private long boardId; // 게시글 식별 번호
    private String boardTitle; // 게시글 제목
    private String boardCreatedAt; // 작성일
    private long boardViews; // 조회 수
    private int likeCount; // 좋아요 수
    private int commentCount; // 댓글 수
}
