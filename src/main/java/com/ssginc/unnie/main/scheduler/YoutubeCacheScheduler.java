package com.ssginc.unnie.main.scheduler;

import com.ssginc.unnie.main.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class YoutubeCacheScheduler {

    private final MainService mainService;

    @Scheduled(cron = "0 0 4 ? * MON")
    public void refreshYoutubeWeeklyTips() {
        log.info("이번 주 꿀팁 YouTube 영상 캐시 갱신 작업을 시작합니다.");
        try {
            mainService.refreshYoutubeCache(); // 서비스의 캐시 갱신 메소드 호출
            log.info("YouTube 영상 캐시 갱신 작업이 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            log.error("YouTube 영상 캐시 갱신 중 오류가 발생했습니다.", e);
        }
    }
}