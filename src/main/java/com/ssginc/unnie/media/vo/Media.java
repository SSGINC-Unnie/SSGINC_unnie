package com.ssginc.unnie.media.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media {
    private long mediaId; // 파일 식별 번호
    private MediaTargetType mediaTargetType; // 파일 도메인 타입
    private long mediaTargetId; // 파일이 포함된 도메인의 식별 번호
    private String mediaUrn; //  미디어 서버 저장 urn
    private LocalDateTime mediaCreatedAt; // 업로드 일시
    private LocalDateTime mediaUpdatedAt; // 수정 일시
    private String mediaOriginName; // 원본명
    private String mediaChangedName; // 수정명
    private boolean mediaIsActive; // 활성화 여부
}
