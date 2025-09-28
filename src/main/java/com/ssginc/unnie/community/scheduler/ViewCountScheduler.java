package com.ssginc.unnie.community.scheduler;

import com.ssginc.unnie.community.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final BoardMapper boardMapper;

    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void syncViewCountsToDB() {
        log.info("조회수 DB 동기화 스케줄러 시작");

        Set<String> keys = redisTemplate.keys("board:views:*");
        if (keys == null || keys.isEmpty()) {
            log.info("동기화할 조회수 데이터가 없습니다.");
            return;
        }

        Map<Long, Long> viewCountsMap = new HashMap<>();
        for (String key : keys) {
            String countStr = redisTemplate.opsForValue().getAndSet(key, "0");
            if (countStr != null) {
                try {
                    long count = Long.parseLong(countStr);
                    if (count > 0) {
                        long boardId = Long.parseLong(key.split(":")[2]);
                        viewCountsMap.put(boardId, count);
                    }
                } catch (Exception e) {
                    log.error("Redis 키 처리 중 오류 발생: key={}, value={}", key, countStr, e);
                }
            }
        }

        if (!viewCountsMap.isEmpty()) {
            log.info("DB에 {}개의 게시글 조회수를 동기화합니다.", viewCountsMap.size());
            viewCountsMap.forEach((boardId, count) -> {
                try {
                    boardMapper.updateBoardViews(boardId, count);
                    log.info(" -> [성공] 게시글 ID {}: 조회수 {} 추가", boardId, count);
                } catch (Exception e) {
                    log.error(" -> [실패] 게시글 ID {} DB 업데이트 중 오류 발생", boardId, e);
                }
            });
        }
        log.info("조회수 DB 동기화 스케줄러 종료");
    }
}