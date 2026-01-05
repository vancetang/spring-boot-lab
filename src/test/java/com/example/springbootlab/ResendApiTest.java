package com.example.springbootlab;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Resend API 測試程式
 * 用於驗證 Resend 郵件發送功能
 *
 * <p>
 * 使用 spring-dotenv 從 .env 檔案載入環境變數至 Spring Environment，
 * 再透過 @Value 注入的方式讀取設定值。
 * </p>
 */
@Slf4j
@SpringBootTest
public class ResendApiTest {

    /**
     * Spring Environment，用於程式化讀取環境變數
     */
    @Autowired
    private Environment env;

    /**
     * Resend API 金鑰，從環境變數 RESEND_API_KEY 讀取
     */
    @Value("${RESEND_API_KEY:}")
    private String apiKey;

    /**
     * 收件者信箱，從環境變數 RESEND_TO_EMAIL 讀取
     */
    @Value("${RESEND_TO_EMAIL:}")
    private String toEmail;

    /**
     * 測試 @Value 注入與 Environment.getProperty() 兩種讀取方式是否一致
     */
    @Test
    public void testEnvironmentReadingMethods() {
        // 使用 Environment 讀取
        String apiKeyFromEnv = env.getProperty("RESEND_API_KEY", "");
        String toEmailFromEnv = env.getProperty("RESEND_TO_EMAIL", "");

        log.info("==========================================");
        log.info("比較 @Value 與 Environment.getProperty() 讀取結果");
        log.info("------------------------------------------");
        log.info("RESEND_API_KEY:");
        log.info("  @Value      : [{}]", maskApiKey(apiKey));
        log.info("  Environment : [{}]", maskApiKey(apiKeyFromEnv));
        log.info("  相同: {}", apiKey.equals(apiKeyFromEnv));
        log.info("------------------------------------------");
        log.info("RESEND_TO_EMAIL:");
        log.info("  @Value      : [{}]", toEmail);
        log.info("  Environment : [{}]", toEmailFromEnv);
        log.info("  相同: {}", toEmail.equals(toEmailFromEnv));
        log.info("==========================================");

        // 斷言兩種方式取得的值相同
        assertEquals(apiKey, apiKeyFromEnv, "RESEND_API_KEY 值應該相同");
        assertEquals(toEmail, toEmailFromEnv, "RESEND_TO_EMAIL 值應該相同");
    }

    /**
     * 遮罩 API Key，使用 MD5 雜湊
     */
    private String maskApiKey(String key) {
        if (StringUtils.isBlank(key)) {
            return "(空)";
        }
        return DigestUtils.md5Hex(key);
    }

    @Test
    public void testSendEmail() {
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
