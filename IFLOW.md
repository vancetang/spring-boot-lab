# Spring Boot Lab 專案指南

## 專案概述

Spring Boot Lab 是一個基於 Spring Boot 3.5.8 與 Java 21 建置的後端實驗室專案。其核心功能為自動化抓取政府行政機關辦公日曆表 OpenData，將其轉換為易於使用的 JSON 格式，並提供 RESTful API 與 Web 檢視介面。

### 主要技術
- **語言**: Java 21
- **框架**: Spring Boot 3.5.8
- **建置工具**: Maven
- **核心庫**: Spring Boot Starter Web, Apache Commons CSV, Lombok

### 核心功能
1. **自動化資料抓取**: 透過 Task Mode 從臺北市資料大平台下載 CSV，自動解析並依年份產生 JSON 檔案
2. **RESTful API**: 提供 `/api/holidays/{year}` 端點查詢指定年份的假日
3. **三種 Web 視圖**: 月曆版 (類似 Google Calendar)、精簡版 (表格)、詳細版 (完整資訊)，支援 CSV 匯出
4. **雙重執行模式**: Server Mode (啟動 Web 服務) 和 Task Mode (資料處理任務)

## 建置與執行

### 環境需求
- Java 21
- Maven

### 執行模式

#### A. 啟動 Web 伺服器 (開發用)
此模式會啟動 Web Server (Port 8080)，不會自動抓取資料，需先執行 Task Mode 更新資料。
```bash
mvn spring-boot:run
```
- 月曆版: `http://localhost:8080/` 或 `http://localhost:8080/index.html` (預設首頁)
- 精簡版: `http://localhost:8080/simple.html`
- 詳細版: `http://localhost:8080/detail.html`

#### B. 執行資料抓取任務 (自動化用)
此模式不會啟動 Web Server (不佔用 Port)，僅執行資料抓取與 JSON 產出，完成後自動結束程式。
```bash
# PowerShell
mvn spring-boot:run "-Dspring-boot.run.arguments=--job=fetch"
```

### 自動化腳本
專案包含一個 PowerShell 腳本 `auto-update.ps1`，可自動執行任務並 Commit 到 Git。

## 專案結構

### 主要程式碼
- `SpringBootLabApplication.java`: 應用程式入口點，支援 Server Mode 和 Task Mode
- `FetchDataService.java`: 核心邏輯 (下載、解析、轉檔)
- `HolidayController.java`: API 介面
- `Holiday.java`: 假日資料模型

### 配置與資源
- `application.yml`: 設定檔 (Server Port, OpenData URL)
- `static/`: 靜態資源目錄 (包含 HTML 與生成的 JSON)
  - `index.html`: 月曆版 UI (預設首頁)
  - `simple.html`: 精簡版 UI
  - `detail.html`: 詳細版 UI
  - `opendata/holiday/`: 生成的 JSON 資料目錄

## 開發慣例

### 編碼慣例
- 程式碼與文件採用繁體中文註解
- 使用 Lombok 簡化程式碼 (Data Class, Logging)
- 使用 Apache Commons 庫處理 CSV 解析和檔案操作

### 執行模式設計
- Server Mode: 啟動 Web 服務，不自動抓取資料
- Task Mode: 僅執行資料處理，適合 CI/CD 自動化
  - `--job=fetch`: 執行完整的 OpenData 抓取流程
  - `--job=process`: 僅處理現有 JSON 檔案，重新執行關聯分析邏輯

### 資料處理流程
1. 下載 CSV 檔案至暫存區
2. 解析 CSV 內容並轉換為 Holiday 物件
3. 依年份分組並輸出 JSON 檔案
4. 產生年份索引檔 (years.json)
5. 清理暫存檔案

## 進階功能

### 即時停班停課查詢
提供 `/api/holidays/realtime` 介面，整合國家災害防救科技中心 (NCDR) API，查詢台北市即時停班停課資訊。

### PDF 產生
支援下載指定年份的假日資料 PDF 檔案。

### 社畜儀表板
月曆版首頁包含儀表板功能：
- 連假倒數計算
- 年度時間進度條
- 即時停班停課狀態顯示