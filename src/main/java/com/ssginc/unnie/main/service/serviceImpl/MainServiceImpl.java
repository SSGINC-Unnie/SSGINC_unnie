package com.ssginc.unnie.main.service.serviceImpl;

import com.ssginc.unnie.main.service.MainService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j // Logback 로거 추가
@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${youtube.api.key}")
    private String apiKey;

    private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/search";
    private static final String YOUTUBE_CACHE_KEY = "youtube:weekly_tips";

    /**
     * 사용자의 요청에 대해 캐시된 YouTube 영상 목록을 반환합니다.
     */


    @PostConstruct
    public void initYoutubeCache() {
        this.refreshYoutubeCache();
    }


    @Override
    public String getYouTubeVideos() {
        String cachedResult = redisTemplate.opsForValue().get(YOUTUBE_CACHE_KEY);

        if (cachedResult != null && !cachedResult.isEmpty()) {
            return cachedResult;
        } else {
            return "{\"items\":[]}";
        }
    }



    /**
     * (스케줄러가 호출) 실제 YouTube API를 호출하여 결과를 Redis에 캐시합니다.
     */

    @Override
    public void refreshYoutubeCache() {
        log.info("YouTube API를 직접 호출하여 캐시를 갱신합니다.");

        List<String> queries;
        int month = LocalDate.now().getMonthValue();

        switch (month) {
            case 3, 4, 5: // 봄
                queries = List.of("봄 웜톤 메이크업 -#shorts", "새학기 헤어스타일 -#shorts", "벚꽃 네일 -#shorts", "봄나들이 코디 -#shorts");
                log.info("계절: 봄, 검색어 목록을 설정합니다.");
                break;
            case 6, 7, 8: // 여름
                queries = List.of("여름 쿨톤 메이크업 -#shorts", "여름휴가 네일 -#shorts", "워터프루프 화장품 -#shorts", "페스티벌 헤어 -#shorts");
                log.info("계절: 여름, 검색어 목록을 설정합니다.");
                break;
            case 9, 10, 11: // 가을
                queries = List.of("가을 뮤트 메이크업 -#shorts", "환절기 피부관리 -#shorts", "가을네일 -#shorts", "트렌치코트 코디 -#shorts");
                log.info("계절: 가을, 검색어 목록을 설정합니다.");
                break;
            case 12, 1, 2: // 겨울
            default:
                queries = List.of("겨울 쿨톤 메이크업 -#shorts", "연말 파티 헤어 -#shorts", "건조한 피부 관리 -#shorts", "겨울 데일리룩 -#shorts");
                log.info("계절: 겨울, 검색어 목록을 설정합니다.");
                break;
        }

        JSONArray combinedItems = new JSONArray();

        for (String query : queries) {
            String singleVideoResultJson = callYoutubeApi(query);

            if (singleVideoResultJson != null) {
                JSONObject resultObj = new JSONObject(singleVideoResultJson);
                if (resultObj.has("items") && resultObj.getJSONArray("items").length() > 0) {
                    combinedItems.put(resultObj.getJSONArray("items").get(0));
                }
            }
        }

        JSONObject finalResult = new JSONObject();
        finalResult.put("items", combinedItems);
        String finalResultJsonString = finalResult.toString();

        redisTemplate.opsForValue().set(YOUTUBE_CACHE_KEY, finalResultJsonString, 8, TimeUnit.DAYS);
    }


    /**
     * (내부 호출용) 단일 검색어로 YouTube API를 호출하는 헬퍼 메소드
     */
    @Override
    public String callYoutubeApi(String query) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(YOUTUBE_API_URL)
                    .queryParam("part", "snippet")
                    .queryParam("q", "{query}")
                    .queryParam("type", "video")
                    .queryParam("maxResults", "1")
                    .queryParam("key", apiKey)
                    .queryParam("relevanceLanguage", "ko")
                    .queryParam("regionCode", "KR")
                    .buildAndExpand(query).toUri();

            return restTemplate.getForObject(uri, String.class);
        } catch (Exception e) {
            log.error("YouTube API 호출 중 오류 발생. 검색어: {}", query, e);
            return null;
        }
    }
}