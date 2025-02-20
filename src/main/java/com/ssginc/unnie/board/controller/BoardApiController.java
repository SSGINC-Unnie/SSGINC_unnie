package com.ssginc.unnie.board.controller;

import com.ssginc.unnie.board.dto.BoardRequest;
import com.ssginc.unnie.board.service.BoardService;
import com.ssginc.unnie.common.util.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/community/board")
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;

    /**
     * 게시글 작성 메서드
     * @param boardRequest DB 서버 insert 할 내용(카테고리, 제목, 내용)
     * @return insert 한 데이터의 id
     */
    @PostMapping("")
    public ResponseEntity<ResponseDto<Map<String, String>>> createBoard(BoardRequest boardRequest) {

        String boardId = boardService.createBoard(boardRequest);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.CREATED.value(), "게시글 작성에 성공했습니다.", Map.of("boardId", boardId))
        );
    }

}
