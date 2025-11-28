# Spring Boot Lab - OpenData Holiday Calendar

這是一個基於 Spring Boot 3.5.8 與 Java 21 建置的後端實驗室專案。目前的核心功能為自動化抓取政府行政機關辦公日曆表 OpenData，將其轉換為易於使用的 JSON 格式，並提供 RESTful API 與 Web 檢視介面。

## 🛠 技術棧 (Tech Stack)

- **Language**: Java 21
- **Framework**: Spring Boot 3.5.8
- **Build Tool**: Maven
- **Libraries**:
  - `commons-csv`: CSV 解析
  - `commons-io`: 檔案與串流處理 (包含 BOM 處理)
  - `spring-boot-starter-web`: RESTful API 與 Web 服務
  - `lombok`: 簡化程式碼 (Data Class, Logging)

## 🚀 功能特色 (Features)

1.  **自動化資料抓取 (Auto Fetching)**:
    - 啟動時自動從 [政府資料開放平臺](https://data.ntpc.gov.tw/datasets/308dcd75-6434-45bc-a95f-584da4fed251) 下載 CSV。
    - 自動解析並依年份分組產生 JSON 檔案 (`src/main/resources/static/opendata/holiday/{year}.json`)。
    - 自動生成年份索引 (`src/main/resources/static/opendata/holiday/years.json`)。
2.  **RESTful API**:
    - `GET /api/holidays/{year}`: 取得指定年份的假日資料。
3.  **Web Visualization**:
    - 內建靜態網頁 (`src/main/resources/static/index.html`)，可直接瀏覽解析後的假日資料。
    - 支援 GitHub Pages 部署 (透過 GitHub Actions 自動發布 `static/` 目錄)。
4.  **雙重執行模式 (Dual Execution Modes)**:
    - 支援 Server 模式 (Web API) 與 Task 模式 (純資料處理)，方便整合 CI/CD。

## 🏃‍♂️ 如何執行 (How to Run)

確保您的環境已安裝 Java 21 與 Maven。

### 模式 A: 啟動 Web Server (開發用)
此模式會啟動 Web Server (Port 8080) 並執行一次資料抓取。
```bash
mvn spring-boot:run
```
- 瀏覽器訪問: `http://localhost:8080/index.html`

### 模式 B: 執行資料抓取任務 (自動化用)
此模式**不會**啟動 Web Server (不佔用 Port)，僅執行資料抓取與 JSON 產出，完成後自動結束程式。
```bash
# PowerShell
mvn spring-boot:run "-Dspring-boot.run.arguments=--job=fetch"
```

### 自動化腳本 (Optional)
專案包含一個 PowerShell 腳本 `auto-update.ps1`，可自動執行任務並 Commit 到 Git。
```powershell
.\auto-update.ps1
```

## 📂 專案結構 (Project Structure)

- `src/main/java/.../service/FetchDataService.java`: 核心邏輯 (下載、解析、轉檔)。
- `src/main/java/.../controller/HolidayController.java`: API 介面。
- `src/main/resources/application.yml`: 設定檔 (Server Port, OpenData URL)。
- `src/main/resources/static/`: 靜態資源目錄 (包含 HTML 與生成的 JSON)，Spring Boot 自動提供服務，並透過 GitHub Actions 發布至 GitHub Pages。

## 📝 文件 (Docs)

- [spec.md](spec.md): 詳細系統規格與架構設計。
- [todolist.md](todolist.md): 開發進度與待辦事項。
