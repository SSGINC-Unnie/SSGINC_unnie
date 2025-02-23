package com.ssginc.unnie.board.service;

import com.ssginc.unnie.board.dto.comment.CommentCreateRequest;

/**
 * 댓글 기능 인터페이스
 */
public interface CommentService {
    int createComment(CommentCreateRequest request, int memberId);
}
