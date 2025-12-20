# PDF 生成功能規劃與測試計畫

## 1. 功能規劃

### 1.1 目標
- 實作將行政機關辦公日曆表資料轉換為 PDF 文件的功能。
- 前端 (詳細版頁面) 提供「下載 PDF」按鈕。
- 支援中文顯示。

### 1.2 技術選型
- **Library**: `openhtmltopdf` (基於 PDFBox，支援 CSS 2.1 與部分 CSS 3)。
- **Template**: 使用 Java Text Blocks (Java 21) 產生 XHTML 字串 (簡單、無額外樣板引擎依賴)。
- **Font**: Noto Sans CJK TC (解決中文顯示問題)。

### 1.3 架構設計
- **Controller**: `HolidayController`
  - 新增 `GET /api/holidays/{year}/pdf` 端點。
  - 回傳 `application/pdf`，並設定 `Content-Disposition: attachment`。
- **Service**: `PdfService`
  - `generateHolidayPdf(String year, List<Holiday> holidays)`: 主要進入點。
  - `generateHtml(...)`: 產生符合 XHTML 規範的 HTML 字串。
  - 字型載入機制：優先讀取 `src/main/resources/fonts/` 下的字型檔。
- **Frontend**: `detail.html`
  - 新增下載按鈕，點擊後開啟新視窗呼叫 API。

## 2. 測試計畫

### 2.1 單元測試 (Unit Test)
- **測試類別**: `PdfServiceTest`
- **測試案例**: `testGenerateHolidayPdf`
  - **輸入**: 模擬的 2024 年假日資料 (包含中文名稱)。
  - **預期輸出**: 
    - 回傳非空的 `byte[]`。
    - 不拋出例外。
    - (人工驗證) PDF 可開啟且中文顯示正常。

### 2.2 整合測試 (Integration Test / Manual Verification)
- **步驟**:
  1. 啟動應用程式 (`mvn spring-boot:run`)。
  2. 瀏覽器開啟 `http://localhost:8080/detail.html`。
  3. 選擇年份 (例如 2024)。
  4. 點擊「下載 PDF」按鈕。
- **預期結果**:
  - 瀏覽器下載 `holiday-2024.pdf`。
  - 檔案可正常開啟。
  - 內容包含該年度假日列表表格。
  - 中文不出現亂碼或方框 (需備妥 TTF 字型，目前使用 OTF 可能有相容性警告)。

## 3. 實作紀錄
- [x] 修改 `pom.xml` 加入 `openhtmltopdf` 依賴。
- [x] 下載 `NotoSansCJKtc-Regular.otf` 至 `src/main/resources/fonts/`。
- [x] 建立 `PdfService` 實作 PDF 轉換與字型載入。
- [x] 修改 `HolidayController` 新增下載端點。
- [x] 修改 `detail.html` 新增下載按鈕與對應 JavaScript。
- [x] 建立 `PdfServiceTest` 並通過基本測試 (XHTML 格式驗證)。

## 4. 待辦事項 / 已知限制
- **字型相容性**: 目前使用的 OTF 字型在 OpenHTMLToPDF 中可能會有相容性警告 (`cmap table not supported`)。若發生中文無法顯示問題，建議替換為 TTF 格式的 Noto Sans TC。
