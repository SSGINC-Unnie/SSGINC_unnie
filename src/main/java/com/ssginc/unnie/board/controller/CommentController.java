package com.ssginc.unnie.board.controller;

import com.ssginc.unnie.board.dto.comment.CommentCreateRequest;
import com.ssginc.unnie.board.service.CommentService;
import com.ssginc.unnie.common.util.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * 댓글 관련 기능 REST API 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/api/community/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("")
    public ResponseEntity<ResponseDto<Map<String, Object>>> createComment(CommentCreateRequest request) {

        int memberId = 1;

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "댓글 작성 성공", Map.of("commentId", commentService.createComment(request, memberId)))
        );
    }
}
