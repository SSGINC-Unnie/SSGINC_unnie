package com.ssginc.unnie.board.dto.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BoardRequestBase {
    private BoardCategory boardCategory;
    private String boardTitle;
    private String boardContents;
    private String boardThumbnail;
    private long boardAuthor;

    protected BoardRequestBase(BoardCategory boardCategory, String boardTitle, String boardContents, String boardThumbnail, long boardAuthor) {
        this.boardCategory = boardCategory;
        this.boardTitle = boardTitle;
        this.boardContents = boardContents;
        this.boardThumbnail = boardThumbnail;
        this.boardAuthor = boardAuthor;
    }
}
