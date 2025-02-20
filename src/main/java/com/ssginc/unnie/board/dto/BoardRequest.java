package com.ssginc.unnie.board.dto;

import com.ssginc.unnie.board.vo.BoardCategory;
import lombok.Builder;
import lombok.Data;

/**
 * 게시글 DB 서버 insert 전용 dto 클래스입니다.
 */
@Data
@Builder
public class BoardRequest {
    private BoardCategory boardCategory;
    private String boardTitle;
    private String boardContents;
    private String boardThumbnail;
    private int boardAuthor;
}
