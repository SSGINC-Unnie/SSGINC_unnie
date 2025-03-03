package com.ssginc.unnie.admin.dto.shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingResponse {
    private List<Address> addresses;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        private String x; // 경도
        private String y; // 위도
        private String roadAddress;
        private String jibunAddress;
    }
}
