package com.ssginc.unnie.review.dto;

import lombok.Data;

@Data
public class KeywordResponse {
    private int keywordId;
    private String keyword; // 예: "친절해요", "깔끔해요", "가격이 착해요" 등
}