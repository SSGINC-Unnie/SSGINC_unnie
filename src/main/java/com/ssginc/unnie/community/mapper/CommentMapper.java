package com.ssginc.unnie.community.mapper;

import com.ssginc.unnie.community.dto.board.BoardResponseForEvent;
import com.ssginc.unnie.community.dto.comment.CommentRequest;
import com.ssginc.unnie.community.dto.comment.CommentGetResponse;
import com.ssginc.unnie.community.dto.comment.CommentGuestGetResponse;
import com.ssginc.unnie.mypage.dto.community.MyPageCommentsResponse;
import com.ssginc.unnie.notification.dto.NotificationResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {
    int checkBoardId(long commentBoardId);

    int checkCommentId(Map<String, Long> commentBoardId);

    int createComment(CommentRequest request);

    int createReplyComment(CommentRequest request);

    List<CommentGuestGetResponse> getAllCommentsGuest(long boardId);

    List<CommentGetResponse> getAllComments(Map<String, Object> memberId);

    int updateComment(CommentRequest comment);

    Integer checkCommentAndAuthor(Map<String, Object> commentId);

    int deleteComment(Map<String, Object> comment);

    List<MyPageCommentsResponse> getMyComments(long memberId);

    NotificationResponse getBoardAuthorIdByCommentId(long commentId);

    BoardResponseForEvent getBoardTitleAndBoardAuthorIdByBoardId(long commentBoardId);

    int getMemberIdByCommentId(long commentParentId);

    int countRootCommentsByBoardId(long boardId);

}
