package com.ssginc.unnie.board.mapper;

import com.ssginc.unnie.board.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    int insertBoard(BoardCreateRequest boardRequest);

    BoardDetailGetResponse selectBoard(String boardId);

    int updateBoard(BoardUpdateRequest boardUpdateRequest);

    int checkBoardId(long boardId);

    int checkBoardAndAuthor(long boardId, String memberId);

    int softDeleteBoard(String boardId);

    List<BoardsGuestGetResponse> getGuestBoards(BoardsGuestGetRequest request);

    List<BoardsGetResponse> getBoards(BoardsGetRequest request);
}
