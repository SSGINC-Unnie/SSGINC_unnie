package com.ssginc.unnie.community.service;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.community.dto.board.*;

/**
 * 게시글 기능 인터페이스
 */
public interface BoardService {
    String createBoard(BoardCreateRequest boardRequest);

    BoardDetailGetResponse getBoard(String boardId);

    long updateBoard(BoardUpdateRequest boardUpdateRequest, String memberId);

    int softDeleteBoard(String boardId, String memberId);

    PageInfo<BoardsGuestGetResponse> getBoardsGuest(BoardCategory category, String sort, String searchType, String search, int page);

    PageInfo<BoardsGetResponse> getBoards(BoardCategory category, String sort, String searchType, String search, int page, int memberId);
}
