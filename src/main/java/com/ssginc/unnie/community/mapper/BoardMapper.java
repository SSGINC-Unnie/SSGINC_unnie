package com.ssginc.unnie.community.mapper;

import com.ssginc.unnie.community.dto.board.*;
import com.ssginc.unnie.mypage.dto.community.MyPageBoardsResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

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

    int checkBoardAndAuthor(Map<String, Object> boardId);

    List<MyPageBoardsResponse> getMyBoards(long memberId);
}
