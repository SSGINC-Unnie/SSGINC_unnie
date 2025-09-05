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
import com.ssginc.unnie.media.dto.MediaRequest;
import com.ssginc.unnie.media.mapper.MediaMapper;
import com.ssginc.unnie.media.vo.MediaTargetType;
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
    private final MediaMapper mediaMapper;

    /**
     * 게시글 작성 메서드
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createBoard(BoardCreateRequest boardRequest) {
        BoardParser parser = new BoardParser(boardRequest.getBoardContents());
        String thumbnail = parser.extractFirstImage();

        // 2. 전달받은 boardRequest 객체에 썸네일을 직접 설정 (객체 재생성 X)
        boardRequest.setBoardThumbnail(thumbnail);

        // 3. 유효성 검증 (작성자, 썸네일이 모두 포함된 상태로 검증)
        boardValidator.validate(boardRequest);

        // 4. board 테이블에 게시글 정보 저장
        int res = boardMapper.insertBoard(boardRequest);
        if (res == 0) {
            throw new UnnieBoardException(ErrorCode.BOARD_CREATE_FAILED);
        }

        // 5. 새로 생성된 boardId를 가져옴
        Long newBoardId = boardRequest.getBoardId();

        // 6. 게시글 본문에 포함된 이미지들을 media 테이블과 연결
        linkImagesToPost(boardRequest.getBoardContents(), newBoardId);

        return String.valueOf(newBoardId);
    }

    /**
     * 게시글 상세 조회 메서드
     */
    @Override
    @Transactional(readOnly = true)
    public BoardDetailGetResponse getBoard(String boardId,Long memberId) {

        if (boardId == null){
            throw new UnnieBoardException(ErrorCode.BOARD_NOT_FOUND);
        }

        BoardDetailGetResponse res = boardMapper.selectBoard(boardId, memberId);

        if (res == null){
            throw new UnnieBoardException(ErrorCode.BOARD_NOT_FOUND);
        }

        log.info("---[게시글 소유권 확인]---");
        log.info("게시글 ID: {}", boardId);
        log.info("게시글 작성자 ID (DB): {}", res.getBoardAuthor());
        log.info("현재 로그인 사용자 ID (JWT): {}", memberId);
        log.info("isOwner 계산 결과 (SQL): {}", res.isOwner());
        log.info("-------------------------");

        return res;
    }

    /**
     * 게시글 수정 메서드
     */
    @Override
    public long updateBoard(BoardUpdateRequest boardUpdateRequest, long memberId) {
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

        mediaMapper.deleteMediaByTarget("BOARD", boardUpdateRequest.getBoardId());

        int res = boardMapper.updateBoard(boardUpdateRequest);
        if (res == 0){
            throw new UnnieBoardException(ErrorCode.BOARD_UPDATE_FAILED);
        }

        linkImagesToPost(boardUpdateRequest.getBoardContents(), boardUpdateRequest.getBoardId());

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
    public PageInfo<BoardsGuestGetResponse> getBoardsGuest(String category, String sort, String searchType, String search, int page) {
        BoardCategory boardCategory = BoardCategory.fromDescription(category);
        List<BoardsGuestGetResponse> boards = boardMapper.getGuestBoards((BoardsGuestGetRequest) this.buildRequest(boardCategory, sort, searchType, search, page, 0));
        return new PageInfo<>(boards);
    }

    @Override
    @Transactional(readOnly = true)
    public PageInfo<BoardsGetResponse> getBoards(String category, String sort, String searchType, String search, int page, long memberId) {
        BoardCategory boardCategory = BoardCategory.fromDescription(category);
        List<BoardsGetResponse> boards = boardMapper.getBoards((BoardsGetRequest) this.buildRequest(boardCategory, sort, searchType, search, page, memberId));
        return new PageInfo<>(boards);
    }

    /**
     * 요청 정보 유효성 검증
     */
    private BoardsGetRequestBase buildRequest(BoardCategory category, String sort, String searchType, String search, int page, long memberId){
        int pageSize = 9;

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


    /**
     * 게시글 본문(HTML)을 파싱하여 img 태그들을 찾고,
     * media 테이블에 현재 게시글 ID와 이미지 정보를 연결(insert)하는 메소드 (신규 구현)
     * @param contents 게시글 본문 HTML
     * @param boardId 새로 생성된 게시글의 ID
     */

    @Override
    public void linkImagesToPost(String contents, long boardId) {
        BoardParser parser = new BoardParser(contents);
        List<String> images = parser.extractAllImages();

        for (String imageUrn : images) {
            if (imageUrn != null && imageUrn.startsWith("/upload/")) {
                String newFileName = imageUrn.substring("/upload/".length());

                MediaRequest mediaRequest = MediaRequest.builder()
                        .targetType(MediaTargetType.BOARD.name()) // "BOARD"
                        .targetId(boardId)
                        .fileUrn(imageUrn)
                        .newFileName(newFileName)
                        .fileOriginalName(newFileName)
                        .build();

                // DB의 media 테이블에 정보 저장
                mediaMapper.insert(mediaRequest);
            }
        }
    }


}
