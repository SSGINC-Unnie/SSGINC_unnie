package com.ssginc.unnie.common.util.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * board-content 에 있는 html 요소를 파싱하기 위한 파서 클래스
 */
public class BoardParser {

    private final Document document;

    // 생성자에서 HTML을 한 번만 파싱
    public BoardParser(String html) {
        this.document = Jsoup.parse(html);
    }

    /**
     * `<p>` 태그 내부의 텍스트 길이 반환 (`<img>` 태그 제외)
     */
    public int getContentTextLength() {
        Elements paragraphs = document.select("p"); // 모든 <p> 태그 가져오기
        StringBuilder textContent = new StringBuilder();

        for (Element paragraph : paragraphs) {
            // 🚀 <img> 태그 제거 후 텍스트만 추가
            paragraph.select("img").remove();
            String paragraphText = paragraph.text().trim();
            textContent.append(paragraphText);
        }

        return textContent.length(); // 내부 텍스트 길이 반환
    }

    /**
     * 첫 번째 <img> 태그의 src 값을 반환
     */
    public String extractFirstImage() {
        Elements images = document.select("img[src]"); // 모든 <img> 태그 중 src가 있는 것 선택

        if (!images.isEmpty()) {
            return images.first().attr("src"); // 첫 번째 이미지의 src 속성 반환
        }

        return null;
    }
}
