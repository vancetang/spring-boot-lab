package com.example.springbootlab;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

/**
 * Resend API 測試程式
 * 用於驗證 Resend 郵件發送功能
 */
@Slf4j
public class ResendApiTest {

    @Test
    public void testSendEmail() {
        // 載入 .env 檔案
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String apiKey = dotenv.get("RESEND_API_KEY");
        String toEmail = dotenv.get("RESEND_TO_EMAIL");

        // 檢查是否已設定 API Key
        if (StringUtils.isBlank(apiKey)) {
            log.info("==========================================");
            log.info("略過測試: 請在 .env 檔案中設定 RESEND_API_KEY");
            log.info("==========================================");
            return;
        }
        
        if (StringUtils.isBlank(toEmail)) {
             log.info("==========================================");
             log.info("略過測試: 請在 .env 檔案中設定 RESEND_TO_EMAIL");
             log.info("==========================================");
             return;
        }

        log.info("開始發送測試郵件...");

        // 初始化 Resend 客戶端
        Resend resend = new Resend(apiKey);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        // 建構郵件內容
        // 注意: 免費版只能使用 "onboarding@resend.dev" 作為寄件者
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("onboarding@resend.dev")
                .to(toEmail)
                .subject("來自 Spring Boot Lab 的 Resend API 測試郵件 - " + timestamp)
                .html("<h1>測試成功！</h1><p>這是一封透過 <strong>Resend Java SDK</strong> 發送的測試郵件。</p>")
                .build();

        try {
            // 發送郵件
            CreateEmailResponse data = resend.emails().send(params);
            log.info("------------------------------------------");
            log.info("✅ 郵件發送成功！");
            log.info("Email ID: {}", data.getId());
            log.info("------------------------------------------");
        } catch (ResendException e) {
            log.info("------------------------------------------");
            log.info("❌ 郵件發送失敗");
            log.info("錯誤訊息: {}", e);
            log.info("------------------------------------------");
        }
    }
}
