package com.ssginc.unnie.review.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatCompletionRequestDTO {
    private String model;
    private List<ChatMessageDTO> messages;
    private double temperature;
}