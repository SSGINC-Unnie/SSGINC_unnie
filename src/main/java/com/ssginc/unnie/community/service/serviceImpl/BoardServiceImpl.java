package com.ssginc.unnie.community.service.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.community.dto.board.*;
import com.ssginc.unnie.community.mapper.BoardMapper;
import com.ssginc.unnie.community.service.BoardService;
import com.ssginc.unnie.common.exception.UnnieBoardException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.parser.BoardParser;
import com.ssginc.unnie.common.util.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 게시글 기능 관련 서비스 인터페이스 구현 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final Validator<BoardRequestBase> boardValidator; // 유효성 검증 인터페이스

    private final BoardMapper boardMapper;

    /**
     * 게시글 작성 메서드
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createBoard(BoardCreateRequest boardRequest) {

        // => 미리 파싱해서 boardRequest 에 넣음
        log.info("boardRequest: {}", boardRequest);

        // BoardParser 사용하여 HTML 파싱 후 검증
        BoardParser parser = new BoardParser(boardRequest.getBoardContents());
        
        String thumbnail = parser.extractFirstImage();

        boardRequest = BoardCreateRequest.builder()
                                            .boardTitle(boardRequest.getBoardTitle())    // 기존 제목 유지
                                            .boardCategory(boardRequest.getBoardCategory()) // 기존 카테고리 유지
                                            .boardContents(boardRequest.getBoardContents()) // 기존 본문 유지 (HTML 그대로 저장)
                                            .boardAuthor(boardRequest.getBoardAuthor())  // 기존 작성자 유지
                                            .boardThumbnail(thumbnail) // 새로 추출한 썸네일 저장
                                        .build();

        boardValidator.validate(boardRequest);

        int res = boardMapper.insertBoard(boardRequest);

        if (res == 0){
            throw new UnnieBoardException(ErrorCode.BOARD_CREATE_FAILED);
        }

        return String.valueOf(boardRequest.getBoardId());
    }

    /**
     * 게시글 상세 조회 메서드
     */
    @Override
    @Transactional(readOnly = true)
    public BoardDetailGetResponse getBoard(String boardId) {

        if (boardId == null){
            throw new UnnieBoardException(ErrorCode.BOARD_NOT_FOUND);
        }

        BoardDetailGetResponse res = boardMapper.selectBoard(boardId);

        if (res == null){
            throw new UnnieBoardException(ErrorCode.BOARD_NOT_FOUND);
        }

        return res;
    }

    /**
     * 게시글 수정 메서드
     */
    @Override
    public long updateBoard(BoardUpdateRequest boardUpdateRequest, long memberId) {

        // => 미리 파싱해서 boardRequest 에 넣음
        log.info("boardUpdateRequest: {}", boardUpdateRequest);

        // BoardParser 사용하여 HTML 파싱 후 검증
        BoardParser parser = new BoardParser(boardUpdateRequest.getBoardContents());

        boardUpdateRequest.setBoardThumbnail(parser.extractFirstImage());

        boardValidator.validate(boardUpdateRequest);

        validateBoardOwnership(boardUpdateRequest.getBoardId(), memberId);

        return executeUpdateTransaction(boardUpdateRequest);
    }

    /**
     * Update 실시하는 메서드(트랜잭션 처리를 별도로 하기 위해 분리)
     */
    @Transactional(rollbackFor = Exception.class)
    public long executeUpdateTransaction(BoardUpdateRequest boardUpdateRequest) {
        int res = boardMapper.updateBoard(boardUpdateRequest);

        if (res == 0){
            throw new UnnieBoardException(ErrorCode.BOARD_UPDATE_FAILED);
        }

        return boardUpdateRequest.getBoardId();
    }

    /**
     * 게시글 식별 번호 존재 여부, 게시글 작성자 번호와 로그인 유저 번호 일치 여부 확인하는 메서드
     */
    private void validateBoardOwnership(Long boardId, long memberId) {
        int checkResult = boardMapper.checkBoardAndAuthor(Map.of("boardId", boardId, "memberId", memberId));

        if (checkResult == -1) {
            throw new UnnieBoardException(ErrorCode.BOARD_NOT_FOUND);
        }

        if (checkResult == 0) {
            throw new UnnieBoardException(ErrorCode.FORBIDDEN);
        }
    }

    /**
     * 게시글 삭제 메서드
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int softDeleteBoard(String boardId, long memberId) {

        if (boardId == null || memberId < 1){
            throw new UnnieBoardException(ErrorCode.BOARD_NOT_INVALID);
        }

        validateBoardOwnership(Long.parseLong(boardId), memberId);

        int res = boardMapper.softDeleteBoard(boardId);

        if (res == 0){
            throw new UnnieBoardException(ErrorCode.BOARD_DELETE_FAILED);
        }

        return res;
    }

    /**
     * 비회원 게시글 목록 조회 전용 메서드
     */
    @Override
    @Transactional(readOnly = true)
    public PageInfo<BoardsGuestGetResponse> getBoardsGuest(BoardCategory category, String sort, String searchType, String search, int page) {
        List<BoardsGuestGetResponse> boards = boardMapper.getGuestBoards((BoardsGuestGetRequest) this.buildRequest(category, sort, searchType, search, page, 0));
        return new PageInfo<>(boards);
    }

    @Override
    @Transactional(readOnly = true)
    public PageInfo<BoardsGetResponse> getBoards(BoardCategory category, String sort, String searchType, String search, int page, long memberId) {
        List<BoardsGetResponse> boards = boardMapper.getBoards((BoardsGetRequest) this.buildRequest(category, sort, searchType, search, page, memberId));
        return new PageInfo<>(boards);
    }

    /**
     * 요청 정보 유효성 검증
     */
    private BoardsGetRequestBase buildRequest(BoardCategory category, String sort, String searchType, String search, int page, long memberId){
        int pageSize = 10;

        if (page < 1){
            throw new UnnieBoardException(ErrorCode.PAGE_OUT_OF_RANGE);
        }

        if (category == null ) {
            throw new UnnieBoardException(ErrorCode.BOARD_INVALID_CATEGORY);
        }

        if (searchType != null && !SearchType.isValid(searchType)){
            throw new UnnieBoardException(ErrorCode.BOARD_INVALID_SEARCH_TYPE);
        }

        if (sort != null && !SortType.isValid(sort)){
            throw new UnnieBoardException(ErrorCode.BOARD_INVALID_SORT_TYPE);
        }

        BoardsGetRequestBase request = null;

        if (memberId > 0){
            request = BoardsGetRequest.builder()
                    .category(category)
                    .sort(sort)
                    .searchType(searchType)
                    .search(search)
                    .page(page)
                    .pageSize(pageSize)
                    .memberId(memberId)
                    .build();
        } else {
            request = BoardsGuestGetRequest.builder()
                    .category(category)
                    .sort(sort)
                    .searchType(searchType)
                    .search(search)
                    .page(page)
                    .pageSize(pageSize)
                    .build();
        }

        // PageHelper 사용해 페이징 적용
        PageHelper.startPage(page, pageSize);

        return request;
    }


}
