package com.ssginc.unnie.review.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatCompletionResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private ChatMessage message;
    }
}
