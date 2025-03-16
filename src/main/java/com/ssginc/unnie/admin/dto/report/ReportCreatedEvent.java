package com.ssginc.unnie.admin.dto.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportCreatedEvent {
    private long receiverId;
    private String title;
}
