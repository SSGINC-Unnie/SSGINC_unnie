package com.ssginc.unnie.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 인터셉터 적용을 위한 웹 설정
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
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:src/main/resources/static/upload/");
    }
}
