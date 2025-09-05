package com.ssginc.unnie.community.dto.comment;

import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 시 댓글 조회 DTO 클래스
 */
@Getter
@Setter
public class CommentGetResponse {
    private long commentId; // 댓글 식별 번호
    private long commentParentId; // 원댓글 식별 번호
    private long commentMemberId; // 작성자 식별 번호
    private String memberNickname; // 작성자 닉네임
    private String memberProfile; // 작성자 프로필
    private String commentCreatedAt; // 댓글 작성일
    private String commentUpdatedAt; // 댓글 수정일
    private String commentContents; // 댓글 내용
    private int likeCount; // 좋아요 수
    private boolean liked; // 좋아요 여부
    private long likeId;
    private boolean isOwner;

}
