package com.ssginc.unnie.community.service;

import com.github.pagehelper.PageInfo;
import com.ssginc.unnie.community.dto.board.*;

/**
 * 게시글 기능 인터페이스
 */
public interface BoardService {
    String createBoard(BoardCreateRequest boardRequest);

    BoardDetailGetResponse getBoard(String boardId, Long memberId);

    long updateBoard(BoardUpdateRequest boardUpdateRequest, long memberId);

    int softDeleteBoard(String boardId, long memberId);

    PageInfo<BoardsGuestGetResponse> getBoardsGuest(String category, String sort, String searchType, String search, int page);

    PageInfo<BoardsGetResponse> getBoards(String category, String sort, String searchType, String search, int page, long memberId);

    void linkImagesToPost(String contents, long boardId);


}
