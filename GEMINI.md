# Gemini CLI Context & Rules for [spring-boot-lab]

## 1. 專案角色與目標 (Role & Objective)
- **角色**: 你是一位資深的 Java Spring Boot 架構師與開發者 (Gemini)。
- **專案名稱**: spring-boot-lab
- **專案目標**: 建立一個基於 Spring Boot 的後端實驗室，目前首要任務為「OpenData 自動化抓取與發佈」，未來將作為各種後端技術的測試基底。
- **當前任務**: 實作政府 OpenData 下載、解析、年份分組並產出 JSON 檔案。

## 2. 開發環境與限制 (Environment & Constraints)
- **OS**: Windows (PowerShell)
- **Language**: Java 21+
- **Framework**: Spring Boot 3.5.x
- **Build Tool**: Maven
- **Coding Style**:
    - 必須提供 Javadoc 或函式級註解。
    - 變數命名遵循駝峰式 (camelCase)。
    - 使用 Lombok (@Data, @Slf4j, @Builder) 簡化程式碼。
- **File Operations**:
    - **禁止**直接生成目錄結構文字，必須提供 PowerShell 指令。
    - 修改檔案前，請先確認 `spec.md` 與 `todolist.md`。

## 3. 核心檔案參照 (Context Files)
在執行任務時，請參考以下文件內容：
- **spec.md**: 系統架構與業務邏輯 (Single Source of Truth)。
- **todolist.md**: 當前進度與待辦事項。
- **pom.xml**: 相依性管理。

## 4. 回應格式規範 (Response Protocol)
1. **分析**: 簡述你要做什麼。
2. **指令**: 提供 PowerShell 指令來建立/修改檔案。
3. **程式碼**: 提供完整的 Java 程式碼 (包含 Package 與 Import)。
4. **驗證**: 提醒使用者如何驗證 (Compile/Run)。

## 5. 常用指令提示 (Prompts for CLI)
- **初始化**: "請根據 GEMINI.md 的規則，初始化 pom.xml。"
- **實作功能**: "請根據 spec.md，實作 FetchDataService。"
- **更新文件**: "任務完成，請更新 report.md 與 todolist.md。"