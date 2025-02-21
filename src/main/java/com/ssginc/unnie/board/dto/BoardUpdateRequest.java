package com.ssginc.unnie.board.dto;

import com.ssginc.unnie.board.vo.BoardCategory;
import lombok.*;

/**
 * 게시글 수정 전용 DTO 클래스
 */
@Getter
@Setter
public class BoardUpdateRequest extends BoardRequestBase{
    private long boardId; // 게시글 식별 번호

    @Builder
    public BoardUpdateRequest(Long boardId, String boardTitle, String boardContents, BoardCategory boardCategory, String boardThumbnail, long boardAuthor) {
        super(boardCategory, boardTitle, boardContents, boardThumbnail, boardAuthor);
        this.boardId = boardId;
    }
}
