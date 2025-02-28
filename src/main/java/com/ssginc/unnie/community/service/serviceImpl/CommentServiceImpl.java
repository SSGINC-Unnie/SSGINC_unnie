package com.ssginc.unnie.community.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.community.dto.comment.CommentRequest;
import com.ssginc.unnie.community.dto.comment.CommentGetResponse;
import com.ssginc.unnie.community.dto.comment.CommentGuestGetResponse;
import com.ssginc.unnie.community.mapper.CommentMapper;
import com.ssginc.unnie.community.service.CommentService;
import com.ssginc.unnie.common.exception.UnnieBoardException;
import com.ssginc.unnie.common.exception.UnnieCommentException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.member.vo.Member;
import com.ssginc.unnie.notification.dto.NotificationMessage;
import com.ssginc.unnie.notification.dto.NotificationResponse;
import com.ssginc.unnie.notification.service.ProducerService;
import com.ssginc.unnie.notification.vo.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 댓글 기능 인터페이스의 구현체 클래스
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    /**
     * 댓글 작성 메서드
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createComment(CommentRequest request, long memberId) {

        checkRequestDto(request);

        request.setCommentMemberId(memberId);

        int res = commentMapper.createComment(request);

        if (res == 0) {
            throw new UnnieCommentException(ErrorCode.COMMENT_CREATE_FAILED);
        }

        return request.getCommentId();
    }

    /**
     * 대댓글 작성
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createReplyComment(CommentRequest request, long memberId) {

        checkRequestDto(request);

        // 원댓글 번호 체크
        if (request.getCommentParentId() > 0 && checkCommentId(request.getCommentBoardId(), request.getCommentParentId()) != 1){
            throw new UnnieCommentException(ErrorCode.COMMENT_PARENT_NOT_FOUND);
        }

        request.setCommentMemberId(memberId);

        int res = commentMapper.createReplyComment(request);

        if (res == 0) {
            throw new UnnieCommentException(ErrorCode.COMMENT_CREATE_FAILED);
        }

        return request.getCommentId();
    }
    
    
    /**
     * 게시글 번호 존재 여부 확인
     */
    private int checkBoardId(long commentBoardId) {
        return commentMapper.checkBoardId(commentBoardId);
    }

    /**
     * 게시글 원댓글 존재 여부 확인
     */
    private int checkCommentId(long commentBoardId, long commentParentId) {
        return commentMapper.checkCommentId(Map.of("commentBoardId", commentBoardId, "commentParentId", commentParentId));
    }

    /**
     * 댓글 작성 요청 정보 유효성 검증
     */
    private void checkRequestDto(CommentRequest request){
        // 게시글 식별 번호 체크
        if (request.getCommentBoardId() < 1 ){
            throw new UnnieCommentException(ErrorCode.COMMENT_BOARD_NOT_FOUND);
        }

        // 게시글 식별 존재 여부 체크
        if (checkBoardId(request.getCommentBoardId()) != 1){
            throw new UnnieCommentException(ErrorCode.COMMENT_BOARD_NOT_FOUND);
        }

        // 내용 검증
        if (request.getCommentContents() == null){
            throw new UnnieCommentException(ErrorCode.COMMENT_CONTENT_REQUIRED);
        }

        // 길이 검증
        if (request.getCommentContents().length() > 100) {
            throw new UnnieCommentException(ErrorCode.COMMENT_LENGTH_INVALID);
        }
    }

    /**
     * 댓글 조회 요청 정보 유효성 검증
     */
    private void checkCommentRequest(long boardId, int page){
        int pageSize = 10;

        // 게시글 식별 번호 체크
        if (boardId < 1 ){
            throw new UnnieCommentException(ErrorCode.COMMENT_BOARD_NOT_FOUND);
        }

        // 게시글 식별 존재 여부 체크
        if (checkBoardId(boardId) != 1){
            throw new UnnieCommentException(ErrorCode.COMMENT_BOARD_NOT_FOUND);
        }

        if (page < 1){
            throw new UnnieBoardException(ErrorCode.PAGE_OUT_OF_RANGE);
        }

        PageHelper.startPage(page, pageSize);
    }

    /**
     * 비로그인시 댓글 조회 메서드
     */
    @Override
    @Transactional(readOnly = true)
    public PageInfo<CommentGuestGetResponse> getAllCommentsGuest(long boardId, int page) {

        checkCommentRequest(boardId, page);

        List<CommentGuestGetResponse> comments = commentMapper.getAllCommentsGuest(boardId);

        if (comments == null || comments.isEmpty()){
            throw new UnnieCommentException(ErrorCode.COMMENT_SELECT_FAILED);
        }

        return new PageInfo<>(comments);
    }

    /**
     * 로그인 시 댓글 조회 메서드
     */
    @Override
    @Transactional(readOnly = true)
    public PageInfo<CommentGetResponse> getAllComments(long memberId, long boardId, int page) {

        checkCommentRequest(boardId, page);

        List<CommentGetResponse> comments = commentMapper.getAllComments(Map.of("memberId", memberId, "boardId", boardId));

        if (comments == null || comments.isEmpty()){
            throw new UnnieCommentException(ErrorCode.COMMENT_SELECT_FAILED);
        }

        return new PageInfo<>(comments);
    }

    /**
     * 댓글 수정 API
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long updateComment(CommentRequest comment) {
        
        // 입력 정보 유효성 검증
        checkRequestDto(comment);
        
        // 댓글 작성자와 로그인 유저 일치 여부 검증
        validateCommentOwnership(comment.getCommentId(), comment.getCommentMemberId());

        // 원댓글 번호 체크
        if (comment.getCommentParentId() > 0 && checkCommentId(comment.getCommentBoardId(), comment.getCommentParentId()) != 1){
            throw new UnnieCommentException(ErrorCode.COMMENT_PARENT_NOT_FOUND);
        }

        int res = commentMapper.updateComment(comment);

        if (res == 0) {
            throw new UnnieCommentException(ErrorCode.COMMENT_UPDATE_FAILED);
        }

        return comment.getCommentId();
    }


    /**
     * 댓글 식별 번호 존재 여부, 게시글 작성자 번호와 로그인 유저 번호 일치 여부 확인하는 메서드
     */
    private void validateCommentOwnership(long commentId, long memberId) {
        int checkResult = commentMapper.checkCommentAndAuthor(Map.of("commentId", commentId, "memberId", memberId));

        if (checkResult == -1) {
            throw new UnnieCommentException(ErrorCode.COMMENT_NOT_FOUND);
        }

        if (checkResult == 0) {
            throw new UnnieBoardException(ErrorCode.FORBIDDEN);
        }
    }

    /**
     * 댓글 soft delete 메서드
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public long deleteComment(long commentId, long memberId) {
        // 댓글 작성자와 로그인 유저 일치 여부 검증
        validateCommentOwnership(commentId, memberId);

        int res = commentMapper.deleteComment(Map.of("commentId", commentId, "memberId", memberId));

        if (res == 0) {
            throw new UnnieCommentException(ErrorCode.COMMENT_DELETE_FAILED);
        }

        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getBoardAuthorIdByCommentId(long commentBoardId) {
        return commentMapper.getBoardAuthorIdByCommentId(commentBoardId);
    }
}
