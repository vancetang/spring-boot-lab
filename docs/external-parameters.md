# Spring Boot 外部參數傳遞方式

本文件說明如何在不使用 `.env` 檔案的情況下，從外部傳入參數至 Spring Boot 應用程式。

## 傳遞方式

### 1. 命令列參數 (`--`)

最高優先級的傳遞方式，適合臨時覆蓋設定。

**開發模式 (Maven):**
```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--RESEND_API_KEY=re_xxx --RESEND_TO_EMAIL=test@example.com"
```

**執行 JAR:**
```powershell
java -jar app.jar --RESEND_API_KEY=re_xxx --RESEND_TO_EMAIL=test@example.com
```

---

### 2. JVM 系統屬性 (`-D`)

透過 JVM 系統屬性傳入，適合需要在 JVM 層級設定的場景。

**開發模式 (Maven):**
```powershell
mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-DRESEND_API_KEY=re_xxx -DRESEND_TO_EMAIL=test@example.com"
```

**執行 JAR:**
```powershell
java -DRESEND_API_KEY=re_xxx -DRESEND_TO_EMAIL=test@example.com -jar app.jar
```

---

### 3. 作業系統環境變數

適合 CI/CD 環境或容器化部署。

**PowerShell (僅當前 session):**
```powershell
$env:RESEND_API_KEY="re_xxx"
$env:RESEND_TO_EMAIL="test@example.com"

mvn spring-boot:run
```

**Linux/macOS:**
```bash
export RESEND_API_KEY=re_xxx
export RESEND_TO_EMAIL=test@example.com

mvn spring-boot:run
```

---

### 4. 執行測試時傳入

```powershell
mvn test -Dtest=ResendApiTest "-DRESEND_API_KEY=re_xxx" "-DRESEND_TO_EMAIL=test@example.com"
```

---

## 優先順序

Spring Boot 會依照以下順序載入設定，較高優先級會覆蓋較低優先級：

| 優先級 | 來源 | 說明 |
|:------:|------|------|
| 1 (最高) | 命令列參數 (`--`) | 適合臨時覆蓋 |
| 2 | JVM 系統屬性 (`-D`) | JVM 層級設定 |
| 3 | 作業系統環境變數 | CI/CD、容器化環境 |
| 4 | `.env` 檔案 | 本地開發 (透過 spring-dotenv) |
| 5 (最低) | `application.yml` | 預設值 |

---

## 使用建議

| 環境 | 建議方式 |
|------|----------|
| 本地開發 | `.env` 檔案 |
| CI/CD | 作業系統環境變數或 Secrets |
| Docker/K8s | 環境變數或 ConfigMap |
| 臨時測試 | 命令列參數 |
