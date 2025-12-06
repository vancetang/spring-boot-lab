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

1.  **資料抓取任務 (Data Fetching)**:
    - 透過 Task Mode (`--job=fetch`) 從 [臺北市資料大平臺](https://data.taipei/dataset/detail?id=0dcbcfcf-f7a1-4664-a810-82c01cb524e0) 下載 CSV。
    - 自動解析並依年份分組產生 JSON 檔案 (`src/main/resources/static/opendata/holiday/{year}.json`)。
    - **增量更新**: 僅更新當次下載的年份，不影響其他年份的現有資料。
    - 自動生成年份索引 (`years.json`)，包含所有現有年份。
2.  **RESTful API**:
    - `GET /api/holidays/{year}`: 取得指定年份的假日資料。
3.  **Web Visualization (三種視圖)**:
    - 📅 **月曆版** (`index.html`): 預設首頁，類似 Google Calendar 的月曆介面，支援年月切換。
      - **社畜儀表板 (Dashboard)**: 首頁新增儀表板，顯示「下一個連假倒數」與「年度時間進度條」，讓使用者一眼掌握放假目標。
    - 📋 **精簡版** (`simple.html`): 表格式列表，僅顯示假日資訊，**支援 CSV 匯出**。
    - 📊 **詳細版** (`detail.html`): 完整資訊表格，包含所有欄位，**支援 CSV 匯出**。
4.  **雙重執行模式 (Dual Execution Modes)**:
    - **Server Mode**: 啟動 Web Server 提供 API 與 UI（不自動抓取資料）。
    - **Task Mode**: 純資料處理，適合 CI/CD 自動化。

## 🏃‍♂️ 如何執行 (How to Run)

確保您的環境已安裝 Java 21 與 Maven。

### 模式 A: 啟動 Web Server (開發用)
此模式會啟動 Web Server (Port 8080)，**不會自動抓取資料**，需先執行 Task Mode 更新資料。
```bash
mvn spring-boot:run
```
- 月曆版: `http://localhost:8080/` 或 `http://localhost:8080/index.html` (預設首頁)
- 精簡版: `http://localhost:8080/simple.html`
- 詳細版: `http://localhost:8080/detail.html`

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
- `src/main/resources/static/`: 靜態資源目錄 (包含 HTML 與生成的 JSON)。
  - `index.html`: 月曆版 UI (預設首頁)
  - `simple.html`: 精簡版 UI
  - `detail.html`: 詳細版 UI
  - `opendata/holiday/`: 生成的 JSON 資料目錄

## 📝 文件 (Docs)

- [spec.md](spec.md): 詳細系統規格與架構設計。
- [todolist.md](todolist.md): 開發進度與待辦事項。
