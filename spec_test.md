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
- **Web UI**:
    - **月曆版 (`index.html`)**: 預設首頁，類似 Google Calendar 的月曆介面，支援年月切換，顯示周休/補班/假日等資訊。
        - **社畜儀表板 (Office Worker Dashboard)**: 
            - **連假倒數**: 自動計算距離下一個非週末假日的剩餘天數。
            - **年度進度**: 顯示當前年份已過的時間百分比與趣味文案。
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
    - `com.example.springbootlab.service`: 業務邏輯 (FetchDataService)
    - `com.example.springbootlab.model`: 資料物件 (Holiday)
- **資料流**: CSV URL -> Temp File -> CSVParser -> List<Holiday> -> Grouping -> ObjectMapper -> JSON Files (and years.json)
- **DevOps**:
    - `auto-update.ps1`: 本地端腳本，整合 Maven 執行與 Git Push。
    - **GitHub Actions**: 透過 `.github/workflows/update-holiday-data.yml` 設定排程 (每月 15 號) 或手動觸發，自動執行 Task Mode 更新資料並 Commit 回 Repository。

## 4. 未來擴充
- 增加 OpenAPI/Swagger API 文件
- 加強單元測試覆蓋率