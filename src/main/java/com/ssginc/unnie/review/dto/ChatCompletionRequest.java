package com.ssginc.unnie.review.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionRequest {
    private String model;
    private List<ChatMessage> messages;
    private double temperature;
}