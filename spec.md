# System Specification: Spring Boot Lab

## 1. 系統概述
本專案 `spring-boot-lab` 旨在作為後端技術的實驗室。目前首要目標為建立一個純 Java 環境，支援透過 Console 執行特定任務 (如 OpenData 抓取)。未來將擴充為具備 RESTful API 與簡易 UI 的 AP Server。

## 2. 當前功能需求
- **執行模式 (Execution Modes)**:
    - **Server Mode (Default)**: 啟動 Web Server (Port 8080)，提供 RESTful API 與 Web UI。**不會自動抓取資料**，需透過 Task Mode 手動更新。
    - **Task Mode (`--job=fetch`)**: 僅執行 OpenData 抓取與 JSON 產出任務，不啟動 Web Server，任務完成後自動結束程式 (適合 CI/CD 自動化)。
- **OpenData 處理**:
    - 自動下載政府行政機關辦公日曆表 CSV。
    - 轉換資料格式為 JSON。
    - 依年份分組，產出獨立 JSON 檔案 (e.g., `2024.json`) 及年份索引檔 `years.json`。
    - **增量更新機制**: 僅覆寫當次下載包含的年份 JSON，不影響其他年份的現有資料。
    - 輸出路徑: `docs/opendata/holiday/`。
- **RESTful API**:
    - 提供 `/api/holidays/{year}` 介面，回傳指定年份的 JSON 資料。
    - **(New) 即時颱風假查詢**:
        - 提供 `/api/holidays/realtime` 介面，回傳當前是否有即時停班停課資訊。
        - **資料來源**: 國家災害防救科技中心 (NCDR) JSON Atom Feed (`https://alerts.ncdr.nat.gov.tw/JSONAtomFeed.ashx?AlertType=33`)。
        - **判斷邏輯**:
            - 解析 JSON Feed 中的 `entry` -> `summary` -> `#text`。
            - 鎖定目標城市：**台北市** 或 **臺北市**。
            - **全區判斷**: 檢查內容是否符合 `[停班停課通知]臺北市:` 或 `[停班停課通知]台北市:` (即城市名稱後緊接冒號，無其他行政區名)，以確保為全區停班停課。
        - **回傳格式**: 回傳包含日期、狀態 (停止上班/上課)、發布時間與原始描述的 JSON 物件。
- **Web UI**:
    - **月曆版 (`index.html`)**: 預設首頁，類似 Google Calendar 的月曆介面，支援年月切換，顯示周休/補班/假日等資訊。
        - **社畜儀表板 (Office Worker Dashboard)**:
            - **連假倒數**: 自動計算距離下一個非週末假日的剩餘天數。
            - **年度進度**: 顯示當前年份已過的時間百分比與趣味文案。
            - **即時停班停課**: 整合 NCDR API，顯示台北市即時停班停課狀態。
    - **精簡版 (`simple.html`)**: 表格式列表，僅顯示假日資訊，並提供 **CSV 下載** 功能 (包含 BOM 以支援 Excel)。
    - **詳細版 (`detail.html`)**: 完整資訊表格，包含所有欄位，並提供 **CSV 下載** 功能。
    - 三種視圖可透過導覽連結相互切換，並支援 URL 參數 (`?year=YYYY`) 傳遞年份。

## 3. 架構設計
- **技術堆疊 (Tech Stack)**:
    - **Language**: Java 21
    - **Framework**: Spring Boot 3.5.8
- **Package Structure**:
    - `com.example.springbootlab`: Main Application (實作 `ApplicationRunner` 處理參數)
    - `com.example.springbootlab.config`: Web 設定 (靜態資源映射)
    - `com.example.springbootlab.controller`: API 控制器 (HolidayController)
    - `com.example.springbootlab.service`: 業務邏輯 (FetchDataService, RealTimeHolidayService)
    - `com.example.springbootlab.model`: 資料物件 (Holiday, NcdrHolidayResponse 等)
- **資料流**: CSV URL -> Temp File -> CSVParser -> List<Holiday> -> Grouping -> ObjectMapper -> JSON Files (and years.json)
- **DevOps**:
    - `auto-update.ps1`: 本地端腳本，整合 Maven 執行與 Git Push。
    - **GitHub Actions**: 透過 `.github/workflows/update-holiday-data.yml` 設定排程 (每月 15 號) 或手動觸發，自動執行 Task Mode 更新資料並 Commit 回 Repository。

## 4. 未來擴充
- 增加 OpenAPI/Swagger API 文件
- 加強單元測試覆蓋率