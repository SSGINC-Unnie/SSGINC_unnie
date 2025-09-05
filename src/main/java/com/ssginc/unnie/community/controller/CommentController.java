package com.ssginc.unnie.community.controller;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.community.dto.comment.CommentGetResponse;
import com.ssginc.unnie.community.dto.comment.CommentGuestGetResponse;
import com.ssginc.unnie.community.dto.comment.CommentRequest;
import com.ssginc.unnie.community.dto.member.CommunityMemberDto;
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

        CommunityMemberDto communityMemberDto = CommunityMemberDto.builder()
                .memberId(memberPrincipal.getMemberId())
                .memberNickname(memberPrincipal.getMemberNickname())
                .build();

//        // 카프카 메세지 생성
//        NotificationMessage msg = NotificationMessage.builder()
//                .notificationMemberId(notificationResponse.getReceiverId())
//                .notificationType(NotificationType.COMMENT)
//                .notificationContents(String.format("[%s]님이 [%s]글에 댓글을 남겼어요", notificationResponse.getReceiverNickname(), notificationResponse.getTargetTitle()))
//                .notificationUrn(String.format("/community/board/%d", request.getCommentBoardId()))
//                .build();
//
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "댓글 작성 성공", Map.of("commentId", commentService.createComment(request, communityMemberDto)))
        );
    }

    /**
     * 대댓글 작성 api
     */
    @PostMapping("/reply")
    public ResponseEntity<ResponseDto<Map<String, Object>>> createReplyComment(CommentRequest request,
                                                                               @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        CommunityMemberDto communityMemberDto = CommunityMemberDto.builder()
                .memberId(memberPrincipal.getMemberId())
                .memberNickname(memberPrincipal.getMemberNickname())
                .build();


        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "대댓글 작성 성공", Map.of("commentId", commentService.createReplyComment(request, communityMemberDto)))
        );
    }

    /**
     * 비로그인 댓글 조회 API
     */
    @GetMapping("/guest")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getAllCommentsGuest(@RequestParam long boardId,
                                                                                @RequestParam int page) {

        PageInfo<CommentGuestGetResponse> pageInfo = commentService.getAllCommentsGuest(boardId, page);
        int rootCommentCount = commentService.countRootComments(boardId);
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "비로그인 댓글 조회 성공",
                        Map.of("pageInfo", pageInfo, "rootCommentCount", rootCommentCount))
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
        PageInfo<CommentGetResponse> pageInfo = commentService.getAllComments(memberId, boardId, page);
        int rootCommentCount = commentService.countRootComments(boardId);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "로그인 댓글 조회 성공",
                        Map.of("pageInfo", pageInfo, "rootCommentCount", rootCommentCount))
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
