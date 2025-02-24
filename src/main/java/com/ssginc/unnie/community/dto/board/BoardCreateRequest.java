package com.ssginc.unnie.community.dto.board;

import lombok.*;

/**
 * 게시글 DB 서버 insert 전용 dto 클래스입니다.
 */
@Getter
@Setter
public class BoardCreateRequest extends BoardRequestBase {
    @Builder
    public BoardCreateRequest(long boardId, String boardTitle, String boardContents, BoardCategory boardCategory, String boardThumbnail, long boardAuthor) {
        super(boardId, boardCategory, boardTitle, boardContents, boardThumbnail, boardAuthor);
    }
}
