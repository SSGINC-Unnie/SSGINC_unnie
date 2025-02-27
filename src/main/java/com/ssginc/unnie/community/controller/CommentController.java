package com.ssginc.unnie.community.controller;

import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.community.dto.comment.CommentRequest;
import com.ssginc.unnie.community.service.CommentService;
import com.ssginc.unnie.common.util.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 댓글 관련 기능 REST API 처리하는 컨트롤러 클래스
 */
@Slf4j
@RestController
@RequestMapping("/api/community/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성 api
     */
    @PostMapping("")
    public ResponseEntity<ResponseDto<Map<String, Object>>> createComment(CommentRequest request,
                                                                          @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

//        long memberId = memberPrincipal.getMemberId();
        long memberId = 1;

        log.info("CommentRequest = {}", request);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "댓글 작성 성공", Map.of("commentId", commentService.createComment(request, memberId)))
        );
    }

    /**
     * 대댓글 작성 api
     */
    @PostMapping("/reply")
    public ResponseEntity<ResponseDto<Map<String, Object>>> createReplyComment(CommentRequest request,
                                                                               @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "대댓글 작성 성공", Map.of("commentId", commentService.createReplyComment(request, memberId)))
        );
    }

    /**
     * 비로그인 댓글 조회 API
     */
    @GetMapping("/guest")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getAllCommentsGuest(@RequestParam long boardId,
                                                                                @RequestParam int page) {
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "비로그인 댓글 조회 성공", Map.of("comment", commentService.getAllCommentsGuest(boardId, page)))
        );
    }

    /**
     * 로그인 댓글 조회 API
     */
    @GetMapping("")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getAllComments(@RequestParam long boardId,
                                                                           @RequestParam int page,
                                                                           @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "비로그인 댓글 조회 성공", Map.of("comment", commentService.getAllComments(memberId, boardId, page)))
        );
    }

    /**
     * 댓글 수정 API
     */
    @PutMapping("")
    public ResponseEntity<ResponseDto<Map<String, Object>>> updateComment(CommentRequest comment,
                                                                          @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        comment.setCommentMemberId(memberId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "댓글 수정 성공", Map.of("commentId", commentService.updateComment(comment)))
        );
    }

    /**
     * 댓글 삭제(soft delete) API
     */
    @PatchMapping("/{commentId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> deleteComment(@PathVariable long commentId,
                                                                          @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "댓글 삭제 성공", Map.of("commentId", commentService.deleteComment(commentId, memberId)))
        );
    }
}
