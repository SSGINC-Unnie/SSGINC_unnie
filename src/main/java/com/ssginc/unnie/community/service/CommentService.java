package com.ssginc.unnie.community.service;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.community.dto.comment.CommentRequest;
import com.ssginc.unnie.community.dto.comment.CommentGetResponse;
import com.ssginc.unnie.community.dto.comment.CommentGuestGetResponse;
import com.ssginc.unnie.community.dto.member.CommunityMemberDto;
import com.ssginc.unnie.notification.dto.NotificationResponse;

/**
 * 댓글 기능 인터페이스
 */
public interface CommentService {
    long createComment(CommentRequest request, CommunityMemberDto memberId);

    long createReplyComment(CommentRequest request, CommunityMemberDto memberId);

    PageInfo<CommentGuestGetResponse> getAllCommentsGuest(long boardId, int page);

    PageInfo<CommentGetResponse> getAllComments(long memberId, long boardId, int page);

    long updateComment(CommentRequest comment);

    long deleteComment(long commentId, long memberId);

    NotificationResponse getBoardAuthorIdByCommentId(long commentBoardId);
}
