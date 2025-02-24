package com.ssginc.unnie.review.service.serviceImpl;

import com.ssginc.unnie.review.dto.ChatCompletionRequestDTO;
import com.ssginc.unnie.review.dto.ChatCompletionResponseDTO;
import com.ssginc.unnie.review.dto.ChatMessageDTO;
import com.ssginc.unnie.review.service.OpenAIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAIServiceImpl implements OpenAIService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String url = "https://api.openai.com/v1/chat/completions";

    public OpenAIServiceImpl(@Value("${openai.api.key}") String apiKey, RestTemplate restTemplate) {
        this.apiKey = apiKey;
        this.restTemplate = restTemplate;
    }

    @Override
    public String summarizeReview(String review) {
        // 메시지 목록 구성
        List<ChatMessageDTO> messages = new ArrayList<>();
        ChatMessageDTO systemMessage = new ChatMessageDTO();
        systemMessage.setRole("system");
        systemMessage.setContent("You are a helpful assistant.");
        messages.add(systemMessage);

        ChatMessageDTO userMessage = new ChatMessageDTO();
        userMessage.setRole("user");
        userMessage.setContent("다음 리뷰를 한 문장으로 요약해줘: " + review);
        messages.add(userMessage);

        // 요청 DTO 구성
        ChatCompletionRequestDTO requestDTO = new ChatCompletionRequestDTO();
        requestDTO.setModel("gpt-3.5-turbo");
        requestDTO.setMessages(messages);
        requestDTO.setTemperature(0.7);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        HttpEntity<ChatCompletionRequestDTO> requestEntity = new HttpEntity<>(requestDTO, headers);

        // API 호출 (exchange 메소드 활용)
        ResponseEntity<ChatCompletionResponseDTO> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                ChatCompletionResponseDTO.class
        );

        ChatCompletionResponseDTO responseDTO = responseEntity.getBody();
        if(responseDTO != null && responseDTO.getChoices() != null && !responseDTO.getChoices().isEmpty()) {
            return responseDTO.getChoices().get(0).getMessage().getContent();
        }
        return "요약을 생성하지 못했습니다.";
    }
}