package com.ssginc.unnie.media.dto;

import lombok.*;

@Data
@Builder
public class MediaRequest {
    private String targetType;
    private long targetId;
    private String fileUrn;
    private String fileOriginalName;
    private String newFileName;
}
