package com.ssginc.unnie.board.controller;


import com.ssginc.unnie.board.dto.BoardCreateRequest;
import com.ssginc.unnie.board.dto.BoardUpdateRequest;
import com.ssginc.unnie.board.service.BoardService;
import com.ssginc.unnie.common.util.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/community/board")
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;

    /**
     * 게시글 작성 메서드
     *
     * @param boardRequest DB 서버 insert 할 내용(카테고리, 제목, 내용)
     * @return insert 한 데이터의 id
     */
    @PostMapping("")
    public ResponseEntity<ResponseDto<Map<String, String>>> createBoard(BoardCreateRequest boardRequest) {
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "게시글 작성에 성공했습니다.", Map.of("boardId", boardService.createBoard(boardRequest)))
        );
    }

    /**
     * 게시글 상세 조회 메서드
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getBoard(@PathVariable String boardId) {
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "게시글 조회 성공", Map.of("board", boardService.getBoard(boardId)))
        );
    }

    /**
     * 게시글 수정 메서드
     */
    @PutMapping()
    public ResponseEntity<ResponseDto<Map<String, Object>>> updateBoard(BoardUpdateRequest boardUpdateRequest) {
        String memberId = "1";
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "게시글 수정 성공", Map.of("boardId", boardService.updateBoard(boardUpdateRequest, memberId)))
        );
    }

    /**
     * 게시글 삭제(soft delete) 메서드
     */
    @PatchMapping("/{boardId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> softDeleteBoard(@PathVariable String boardId) {
        String memberId = "1";
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "게시글 삭제 성공", Map.of("boardId", boardService.softDeleteBoard(boardId, memberId)))
        );
    }
}
















