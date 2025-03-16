package com.ssginc.unnie.community.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponseForEvent {
    private long boardAuthor;
    private String boardTitle;
}
