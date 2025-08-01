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

    // RestTemplate을 서비스에서 직접 생성
    private final RestTemplate restTemplate = new RestTemplate();

    // YouTube API를 호출하여 결과 가져오기
    public String getYouTubeVideos(String query) {
        // URL 인코딩을 고려한 URL 구성
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