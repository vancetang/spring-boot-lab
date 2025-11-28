# Todo List

## Pending
- [ ] 設定 GitHub Actions 自動執行抓取任務 (deploy-pages.yml 已移除，待重新規劃)
- [ ] 撰寫單元測試 (FetchDataService、HolidayController)
- [ ] 改進 API 錯誤處理 (回傳適當 HTTP 狀態碼)
- [ ] 新增 OpenAPI/Swagger API 文件

## In Progress

## Completed
- [x] 新增月曆版視圖 (calendar.html) - 類似 Google Calendar 的月曆介面
- [x] 移除 Server Mode 啟動時自動下載功能 (改為僅 Task Mode 可更新資料)
- [x] 修正 years.json 產生邏輯，確保包含所有現有年份 (增量更新機制)
- [x] 將靜態資源從 docs/ 移至 src/main/resources/static/
- [x] 建立 GitHub Actions 工作流程 (deploy-pages.yml) - 已移除待重新規劃
- [x] 重新設計前端 UI (精簡版 + 詳細版 + 月曆版)
- [x] 將程式註解調整為繁體中文
- [x] 同步文檔版本號至 Spring Boot 3.5.8
- [x] 實作 CLI Task Mode (支援 --job=fetch 參數且不佔用 Port)
- [x] 建立自動化更新腳本 (auto-update.ps1)
- [x] 製作 README.md (專案說明與執行指南)
- [x] 實作 RESTful API 範例 (HolidayController)
- [x] 建立前端測試 UI (static/index.html, static/detail.html)
- [x] 實作 OpenData 自動化抓取功能 (FetchDataService - CSV 下載與解析)
- [x] 定義 OpenData JSON 產出格式 (Holiday Model)
- [x] 實作年份分組與 JSON 檔案輸出 (static/opendata/holiday)
- [x] 建置基礎 Spring Boot 環境
- [x] 初始化專案結構 (pom.xml, main class)
- [x] 建立 spec.md 與 todolist.md
