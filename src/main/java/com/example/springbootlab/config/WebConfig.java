package com.example.springbootlab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 設定類別。
 *
 * <p>
 * 配置靜態資源處理。
 * </p>
 *
 * @author Spring Boot Lab
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 將所有請求對應到專案根目錄下的 docs 資料夾
        registry.addResourceHandler("/**")
                .addResourceLocations("file:docs/");
    }
}
