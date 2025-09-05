package com.ssginc.unnie.community.dto.board;

import lombok.*;

/**
 * 게시글 수정 전용 DTO 클래스
 */
@Getter
@Setter
public class BoardUpdateRequest extends BoardRequestBase{
    @Builder
    public BoardUpdateRequest(Long boardId, String boardTitle, String boardContents, BoardCategory boardCategory, String boardThumbnail, Long boardAuthor) {
        super(boardId, boardCategory, boardTitle, boardContents, boardThumbnail, boardAuthor);
    }
}
