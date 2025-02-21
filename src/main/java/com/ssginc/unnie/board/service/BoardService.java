package com.ssginc.unnie.board.service;

import com.ssginc.unnie.board.dto.BoardCreateRequest;
import com.ssginc.unnie.board.dto.BoardDetailGetResponse;
import com.ssginc.unnie.board.dto.BoardUpdateRequest;

public interface BoardService {
    String createBoard(BoardCreateRequest boardRequest);

    BoardDetailGetResponse getBoard(String boardId);

    int updateBoard(BoardUpdateRequest boardUpdateRequest, String memberId);

    int softDeleteBoard(String boardId, String memberId);
}
