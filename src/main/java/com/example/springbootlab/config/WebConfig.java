package com.example.springbootlab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 設定類別。
 *
 * <p>
 * 配置靜態資源處理，將 docs 目錄映射為 Web 可存取路徑，
 * 以支援 GitHub Pages 靜態網站部署。
 * </p>
 *
 * @author Spring Boot Lab
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置靜態資源處理器。
     *
     * <p>
     * 將所有請求路徑 (/**) 對應到以下資源位置：
     * <ul>
     * <li>classpath:/static/ - 類別路徑下的靜態資源</li>
     * <li>file:docs/ - 專案根目錄下的 docs 資料夾</li>
     * </ul>
     * </p>
     *
     * @param registry 資源處理器註冊表
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "file:docs/");
    }
}
