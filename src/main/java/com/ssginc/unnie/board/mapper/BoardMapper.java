package com.ssginc.unnie.board.mapper;

import com.ssginc.unnie.board.dto.BoardCreateRequest;
import com.ssginc.unnie.board.dto.BoardDetailGetResponse;
import com.ssginc.unnie.board.dto.BoardUpdateRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {
    int insertBoard(BoardCreateRequest boardRequest);

    BoardDetailGetResponse selectBoard(String boardId);

    int updateBoard(BoardUpdateRequest boardUpdateRequest);

    int checkBoardId(long boardId);

    int checkBoardAndAuthor(long boardId, String memberId);

    int softDeleteBoard(String boardId);
}
