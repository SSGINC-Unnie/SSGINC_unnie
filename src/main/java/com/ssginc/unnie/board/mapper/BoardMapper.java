package com.ssginc.unnie.board.mapper;

import com.ssginc.unnie.board.dto.BoardRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {
    int insertBoard(BoardRequest boardRequest);
}
