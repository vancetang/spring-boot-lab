# System Specification: Spring Boot Lab

## 1. 系統概述
本專案 `spring-boot-lab` 旨在作為後端技術的實驗室。目前首要目標為建立一個純 Java 環境，支援透過 Console 執行特定任務 (如 OpenData 抓取)。未來將擴充為具備 RESTful API 與簡易 UI 的 AP Server。

## 2. 當前功能需求
- **執行模式 (Execution Modes)**:
    - **Server Mode (Default)**: 啟動 Web Server (Port 8080)，提供 RESTful API 與 Web UI。啟動時亦會執行一次資料抓取。
    - **Task Mode (`--job=fetch`)**: 僅執行 OpenData 抓取與 JSON 產出任務，不啟動 Web Server，任務完成後自動結束程式 (適合 CI/CD 自動化)。
- **OpenData 處理**:
    - 自動下載政府行政機關辦公日曆表 CSV。
    - 轉換資料格式為 JSON。
    - 依年份分組，產出獨立 JSON 檔案 (e.g., `2024.json`) 及年份索引檔 `years.json`。
    - 輸出路徑: `docs/opendata/holiday/` (支援 GitHub Pages)。
- **RESTful API**:
    - 提供 `/api/holidays/{year}` 介面，回傳指定年份的 JSON 資料。
- **Web UI**:
    - 靜態網頁 (`docs/index.html`) 透過 AJAX 讀取生成的 JSON 檔。
    - 支援年份自動載入與切換檢視。
- **Java 版本**: Java 21
- **Framework**: Spring Boot 3.5.8

## 3. 架構設計
- **Package Structure**:
    - `com.example.springbootlab`: Main Application (實作 `ApplicationRunner` 處理參數)
    - `com.example.springbootlab.config`: Web 設定 (靜態資源映射)
    - `com.example.springbootlab.controller`: API 控制器 (HolidayController)
    - `com.example.springbootlab.service`: 業務邏輯 (FetchDataService)
    - `com.example.springbootlab.model`: 資料物件 (Holiday)
- **資料流**: CSV URL -> Temp File -> CSVParser -> List<Holiday> -> Grouping -> ObjectMapper -> JSON Files (and years.json)
- **DevOps**: `auto-update.ps1` 腳本整合 Maven 執行與 Git Push。

## 4. 未來擴充
- 設定 GitHub Actions 自動化排程抓取 (使用 Task Mode)
