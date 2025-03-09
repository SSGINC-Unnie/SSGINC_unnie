package com.ssginc.unnie.common.config;

import com.ssginc.unnie.common.converter.EnumDescriptionConverterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 설정
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginCheckInterceptor())
//                .order(1) // 첫 번째로 적용될 인터셉터
//                .addPathPatterns("/**") // 하위에 전부 적용
//                .excludePathPatterns("/login/**", "/logout/**", "/registration/**", "/js/**",  "/img/**", "/css/**", "/*.ico", "/error"); // 예외
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 정적 리소스 (CSS, JS, 이미지 등) 제공
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // 업로드된 파일 제공
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:src/main/resources/static/upload/");
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**")
//                .addResourceLocations("classpath:/static/");
//
//        registry.addResourceHandler("/upload/**")
//                .addResourceLocations("file:///C:/upload/shop/");
//    }



    /**
     * 카테고리 형식의 ENUM 을 자동으로 변환시켜주는 컨버터 등록하기
     *
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new EnumDescriptionConverterFactory());
    }
}