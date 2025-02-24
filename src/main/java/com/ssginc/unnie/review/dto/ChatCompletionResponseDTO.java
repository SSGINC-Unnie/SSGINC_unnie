package com.ssginc.unnie.review.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatCompletionResponseDTO {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private ChatMessageDTO message;
    }
}
