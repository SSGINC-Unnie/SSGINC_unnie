package com.ssginc.unnie.community.controller;


import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.common.config.MemberPrincipal;
import com.ssginc.unnie.community.dto.board.*;
import com.ssginc.unnie.community.service.BoardService;
import com.ssginc.unnie.common.util.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 게시글 기능 관련 REST API 처리하는 컨트롤러 클래스
 */
@Slf4j
@RestController
@RequestMapping("/api/community/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 작성 메서드
     *
     * @param boardRequest DB 서버 insert 할 내용(카테고리, 제목, 내용)
     * @return insert 한 데이터의 id
     */
    @PostMapping("")
    public ResponseEntity<ResponseDto<Map<String, String>>> createBoard(BoardCreateRequest boardRequest,
                                                                        @AuthenticationPrincipal UserDetails userDetails) {
        log.info("userDetails: {}", userDetails);
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
    public ResponseEntity<ResponseDto<Map<String, Object>>> updateBoard(BoardUpdateRequest boardUpdateRequest,
                                                                        @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "게시글 수정 성공", Map.of("boardId", boardService.updateBoard(boardUpdateRequest, memberId)))
        );
    }

    /**
     * 게시글 삭제(soft delete) 메서드
     */
    @PatchMapping("/{boardId}")
    public ResponseEntity<ResponseDto<Map<String, Object>>> softDeleteBoard(@PathVariable String boardId,
                                                                            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        long memberId = memberPrincipal.getMemberId();
        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "게시글 삭제 성공", Map.of("boardId", boardService.softDeleteBoard(boardId, memberId)))
        );
    }

    /**
     * 비회원 게시글 목록 조회
     * @param category 게시글 카테고리
     * @param sort 게시글 정렬 방식(최신순/인기순)
     * @param searchType 게시글 검색 방식(제목/내용)
     * @param search 검색어
     * @param page 현재 페이지 수
     * @return 게시글 목록
     */
    @GetMapping("/guest")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getBoardsGuest(
            @RequestParam(defaultValue = "공지 있어!") BoardCategory category,
            @RequestParam(defaultValue = "LATEST") String sort,
            @RequestParam(defaultValue = "TITLE") String searchType,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page) {

        PageInfo<BoardsGuestGetResponse> boards = boardService.getBoardsGuest(category, sort, searchType, search, page);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "게시글 목록 조회 성공", Map.of("boards", boards))
        );
    }

    /**
     * 로그인 시 게시글 목록 조회
     * @param category 게시글 카테고리
     * @param sort 게시글 정렬 방식(최신순/인기순)
     * @param searchType 게시글 검색 방식(제목/내용)
     * @param search 검색어
     * @param page 현재 페이지 수
     * @return 게시글 목록(좋아요 여부 반영)
     */
    @GetMapping("")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getBoards(
            @RequestParam(defaultValue = "공지 있어!") BoardCategory category,
            @RequestParam(defaultValue = "LATEST") String sort,
            @RequestParam(defaultValue = "TITLE") String searchType,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        long memberId = memberPrincipal.getMemberId();

        PageInfo<BoardsGetResponse> boards = boardService.getBoards(category, sort, searchType, search, page, memberId);

        return ResponseEntity.ok(
                new ResponseDto<>(HttpStatus.OK.value(), "게시글 목록 조회 성공", Map.of("boards", boards))
        );
    }

}















