package com.example.springbootlab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 設定類別。
 *
 * <p>
 * 配置靜態資源處理，Spring Boot 預設會自動處理 classpath:/static/ 下的資源。
 * 此類別保留以便未來擴充自訂 Web MVC 配置。
 * </p>
 *
 * @author Spring Boot Lab
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Spring Boot 預設已自動處理 classpath:/static/ 靜態資源
    // 此類別保留以便未來擴充 Web MVC 配置（如 CORS、Interceptor 等）
}
