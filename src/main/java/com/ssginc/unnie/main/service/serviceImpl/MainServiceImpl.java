package com.ssginc.unnie.main.service.serviceImpl;

import com.ssginc.unnie.main.service.MainService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@Service
public class MainServiceImpl implements MainService {

    @Value("${youtube.api.key}")
    private String apiKey;

    private final String youtubeApiUrl = "https://www.googleapis.com/youtube/v3/search";

    private final RestTemplate restTemplate = new RestTemplate();

    public String getYouTubeVideos(String query) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(youtubeApiUrl)
                .queryParam("part", "snippet")
                .queryParam("q", "{query}")
                .queryParam("type", "video")
                .queryParam("maxResults", "4")
                .queryParam("key", apiKey)
                .queryParam("relevanceLanguage", "ko")
                .queryParam("regionCode", "KR");

        URI uri = builder.buildAndExpand(query).toUri();

        String response = restTemplate.getForObject(uri, String.class);
        return response;
    }
}