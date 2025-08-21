package com.ssginc.unnie.mypage.dto.reservation;

import lombok.Data;
import java.util.List;

@Data
public class DesignerScheduleDto {
    private Long designerId;
    private String designerName;
    private List<MyPageReservationManagedto> reservations;
}
