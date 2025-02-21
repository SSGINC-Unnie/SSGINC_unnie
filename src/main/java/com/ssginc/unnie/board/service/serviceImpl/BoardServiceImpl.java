package com.ssginc.unnie.board.service.serviceImpl;

import com.ssginc.unnie.board.dto.BoardCreateRequest;
import com.ssginc.unnie.board.dto.BoardDetailGetResponse;
import com.ssginc.unnie.board.dto.BoardRequestBase;
import com.ssginc.unnie.board.dto.BoardUpdateRequest;
import com.ssginc.unnie.board.mapper.BoardMapper;
import com.ssginc.unnie.board.service.BoardService;
import com.ssginc.unnie.common.exception.UnnieBoardException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.common.util.parser.BoardParser;
import com.ssginc.unnie.common.util.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return String.valueOf(res);
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
    public int updateBoard(BoardUpdateRequest boardUpdateRequest, String memberId) {

        // => 미리 파싱해서 boardRequest 에 넣음
        log.info("boardUpdateRequest: {}", boardUpdateRequest);

        // BoardParser 사용하여 HTML 파싱 후 검증
        BoardParser parser = new BoardParser(boardUpdateRequest.getBoardContents());

        boardUpdateRequest.setBoardThumbnail(parser.extractFirstImage());

        boardValidator.validate(boardUpdateRequest);

        validateBoardOwnership(boardUpdateRequest.getBoardId(), memberId);

        int res = executeUpdateTransaction(boardUpdateRequest);

        if (res == 0){
            throw new UnnieBoardException(ErrorCode.BOARD_UPDATE_FAILED);
        }

        return res;
    }

    /**
     * Update 실시하는 메서드(트랜잭션 처리를 별도로 하기 위해 분리)
     */
    @Transactional(rollbackFor = Exception.class)
    public int executeUpdateTransaction(BoardUpdateRequest boardUpdateRequest) {
        return boardMapper.updateBoard(boardUpdateRequest);
    }

    /**
     * 게시글 식별 번호 존재 여부, 게시글 작성자 번호와 로그인 유저 번호 일치 여부 확인하는 메서드
     */
    private void validateBoardOwnership(Long boardId, String memberId) {
        int checkResult = boardMapper.checkBoardAndAuthor(boardId, memberId);

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
    public int softDeleteBoard(String boardId, String memberId) {

        if (boardId == null || memberId == null){
            throw new UnnieBoardException(ErrorCode.BOARD_NOT_INVALID);
        }

        validateBoardOwnership(Long.parseLong(boardId), memberId);

        int res = boardMapper.softDeleteBoard(boardId);

        if (res == 0){
            throw new UnnieBoardException(ErrorCode.BOARD_DELETE_FAILED);
        }

        return res;
    }
}
