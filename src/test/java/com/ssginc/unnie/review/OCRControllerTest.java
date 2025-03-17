package com.ssginc.unnie.review;

import com.ssginc.unnie.common.util.JwtUtil;
import com.ssginc.unnie.review.controller.OCRController;
import com.ssginc.unnie.review.service.OCRService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(OCRController.class)
class OCRControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OCRService ocrService;

    @MockBean
    private JwtUtil jwtUtil;

    private final String URL = "/api/ocr/upload";

    @Test
    @WithMockUser
    void testUploadReceipt_Success() throws Exception {
        log.info("testUploadReceipt_Start");
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "Receipt.jpg", "image/jpeg", "dummy content".getBytes());

        // 실제 서비스와 동일한 구조로 OCR API 응답 mocking
        JSONObject dummyField1 = new JSONObject().put("inferText", "텍스트1");
        JSONObject dummyField2 = new JSONObject().put("inferText", "텍스트2");
        JSONArray fieldsArray = new JSONArray().put(dummyField1).put(dummyField2);

        JSONObject imageObject = new JSONObject().put("fields", fieldsArray);
        JSONArray imagesArray = new JSONArray().put(imageObject);

        JSONObject dummyOcrResponse = new JSONObject().put("images", imagesArray);

        // OCRService가 mocking된 OCR API 응답을 반환하도록 설정
        given(ocrService.processOCR(any(MultipartFile.class))).willReturn(dummyOcrResponse);

        // when & then
        mockMvc.perform(multipart(URL)
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OCR 분석 성공"))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));
        log.info("testUploadReceipt_Success");
        log.info("testUploadReceipt_End");
    }
}
