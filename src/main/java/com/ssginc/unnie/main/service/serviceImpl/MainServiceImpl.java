package com.ssginc.unnie.main.service.serviceImpl;

import com.ssginc.unnie.main.service.MainService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

@Service
public class MainServiceImpl implements MainService {

    @Value("${youtube.api.key}")
    private String apiKey;


    private final String youtubeApiUrl = "https://www.googleapis.com/youtube/v3/search";

    private final RestTemplate restTemplate = new RestTemplate();

    public String getYouTubeVideos(String query) {
        String encodedQuery = UriUtils.encode(query, "UTF-8");  // URL 인코딩 처리
        String url = UriComponentsBuilder.fromHttpUrl(youtubeApiUrl)
                .queryParam("part", "snippet")
                .queryParam("q", encodedQuery)
                .queryParam("type", "video")
                .queryParam("maxResults", "4")
                .queryParam("key", apiKey)
                .toUriString();
        // API 호출
        String response = restTemplate.getForObject(url, String.class);
        return response;
    }
}