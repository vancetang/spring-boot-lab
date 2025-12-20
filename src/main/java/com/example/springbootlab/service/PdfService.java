package com.example.springbootlab.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.example.springbootlab.model.Holiday;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PdfService {

    public byte[] generateHolidayPdf(String year, List<Holiday> holidays) throws IOException {
        String htmlContent = generateHtmlWithAlternativeList(year, holidays);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            // 嘗試載入字型
            try {
                ClassPathResource fontResource = new ClassPathResource("fonts/NotoSansCJKtc-Regular.ttf");
                if (fontResource.exists()) {
                    // OpenHTMLToPDF 需要字型檔案的 File 物件或 InputStream (需配合 supplier)
                    // 這裡使用 InputStream Supplier
                    builder.useFont(() -> {
                        try {
                            return fontResource.getInputStream();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, "Noto Sans CJK TC");
                    log.info("已載入 Noto Sans CJK TC 字型");
                } else {
                    log.warn("找不到 Noto Sans CJK TC 字型，中文顯示可能會異常");
                }
            } catch (Exception e) {
                log.error("載入字型時發生錯誤", e);
            }

            builder.withHtmlContent(htmlContent, null);
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        }
    }

    @SuppressWarnings("unused")
    private String generateHtml(String year, List<Holiday> holidays) {

        StringBuilder sb = new StringBuilder();

        // CSS 樣式

        String style = """

                    <style>

                        @page {

                            size: A4;

                            margin: 15mm;

                            @bottom-center {

                                content: "Page " counter(page) " of " counter(pages);

                                font-family: 'Noto Sans CJK TC', sans-serif;

                                font-size: 10pt;

                                color: #777;

                            }

                        }

                        body {

                            font-family: 'Noto Sans CJK TC', sans-serif;

                            font-size: 11pt;

                            line-height: 1.4;

                            color: #333;

                        }

                        h1 {

                            text-align: center;

                            color: #1e3c72; /* Dark Blue */

                            margin-bottom: 20px;

                            font-size: 20pt;

                        }

                        table {

                            width: 100%;

                            border-collapse: collapse;

                            border-spacing: 0;

                            margin-top: 10px;

                            -fs-table-paginate: paginate; /* 優化分頁 */

                        }

                        thead {

                            display: table-header-group; /* 關鍵：每一頁重複顯示標題 */

                        }

                        tr {

                            page-break-inside: avoid; /* 避免列內容被切斷 */

                        }

                        th, td {

                            border: 1px solid #dee2e6;

                            padding: 10px;

                            vertical-align: middle;

                        }

                        th {

                            background-color: #2a5298; /* Header Blue */

                            color: #ffffff;

                            font-weight: bold;

                            text-align: center;

                            font-size: 11pt;

                        }

                        tr:nth-child(even) {

                            background-color: #f8f9fa;

                        }

                        .text-center {

                            text-align: center;

                        }

                        .badge {

                            display: inline-block;

                            padding: 4px 10px;

                            border-radius: 12px;

                            color: #fff;

                            font-size: 10pt;

                            font-weight: bold;

                            text-align: center;

                            white-space: nowrap;

                        }

                        .badge-yes {

                            background-color: #e03131; /* Red */

                        }

                        .badge-no {

                            background-color: #2f9e44; /* Green */

                        }

                        .note {

                            font-size: 0.9em;

                            color: #666;

                            margin-top: 4px;

                            display: block;

                        }

                        /* 自定義國字編號樣式 */
                        .chinese-numbers {
                            list-style: none;
                            padding-left: 0;
                            margin-left: 0;
                        }

                        .chinese-numbers > li:nth-child(1)::before {
                            content: "一、";
                            font-weight: bold;
                        }

                        .chinese-numbers > li:nth-child(2)::before {
                            content: "二、";
                            font-weight: bold;
                        }

                        .chinese-numbers > li:nth-child(3)::before {
                            content: "三、";
                            font-weight: bold;
                        }

                        .chinese-numbers > li {
                            position: relative;
                            padding-left: 30px;
                            margin-bottom: 8px;
                        }

                    </style>

                """;

        // HTML 標頭

        sb.append("<!DOCTYPE html>");

        sb.append("<html><head><meta charset='UTF-8'/>"); // 必須閉合 meta

        sb.append(style);

        sb.append("</head><body>");

        // 標題

        sb.append("<h1>").append(year).append(" 年中華民國政府行政機關辦公日曆表</h1>");

        // 表格

        sb.append("<table>");

        sb.append("<thead><tr>");

        sb.append("<th style='width: 18%'>日期</th>");

        sb.append("<th style='width: 12%'>放假</th>");

        sb.append("<th style='width: 25%'>節日名稱</th>");

        sb.append("<th style='width: 15%'>類別</th>");

        sb.append("<th style='width: 30%'>說明</th>");

        sb.append("</tr></thead>");

        sb.append("<tbody>");

        for (Holiday holiday : holidays) {

            sb.append("<tr>");

            // 日期 (格式化為 YYYY/MM/DD)

            String date = holiday.getDate();

            String formattedDate = date;

            if (date != null && date.length() == 8) {

                formattedDate = date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);

            }

            sb.append("<td class='text-center'>").append(formattedDate).append("</td>");

            // 是否放假 (使用 Badge 樣式)

            boolean isHoliday = holiday.isHoliday();

            String badgeClass = isHoliday ? "badge-yes" : "badge-no";

            String badgeText = isHoliday ? "放假" : "上班";

            sb.append("<td class='text-center'>");

            sb.append("<span class='badge ").append(badgeClass).append("'>").append(badgeText).append("</span>");

            sb.append("</td>");

            // 名稱

            sb.append("<td>").append(escapeHtml(holiday.getName())).append("</td>");

            // 類別

            sb.append("<td class='text-center'>").append(escapeHtml(holiday.getHolidayCategory())).append("</td>");

            // 說明 (加上 Note)

            sb.append("<td>");

            sb.append(escapeHtml(holiday.getDescription()));

            if (holiday.getNote() != null && !holiday.getNote().isEmpty()) {

                sb.append("<br/><span class='note'>(").append(escapeHtml(holiday.getNote())).append(")</span>");

            }

            sb.append("</td>");

            sb.append("</tr>");

        }

        sb.append("</tbody></table>");

        // 測試用有序列表

        sb.append("<h2>測試列表功能</h2>");

        sb.append("<ol class='chinese-numbers'>");

        sb.append("<li>item1</li>");

        sb.append("<li>item2</li>");

        sb.append("<li>item3</li>");

        sb.append("</ol>");

        sb.append("</body></html>");

        return sb.toString();

    }

    private String generateHtmlWithAlternativeList(String year, List<Holiday> holidays) {

        StringBuilder sb = new StringBuilder();

        // 基本樣式

        String basicStyle = """



                    <style>

                        @page {

                            size: A4;

                            margin: 15mm;

                            @bottom-center {

                                content: "Page " counter(page) " of " counter(pages);

                                font-family: 'Noto Sans CJK TC', sans-serif;

                                font-size: 10pt;

                                color: #777;

                            }

                        }

                        body {

                            font-family: 'Noto Sans CJK TC', sans-serif;

                            font-size: 11pt;

                            line-height: 1.4;

                            color: #333;

                        }

                        h1 {

                            text-align: center;

                            color: #1e3c72; /* Dark Blue */

                            margin-bottom: 20px;

                            font-size: 20pt;

                        }

                        table {

                            width: 100%;

                            border-collapse: collapse;

                            border-spacing: 0;

                            margin-top: 10px;

                            -fs-table-paginate: paginate; /* 優化分頁 */

                        }

                        thead {

                            display: table-header-group; /* 關鍵：每一頁重複顯示標題 */

                        }

                        tr {

                            page-break-inside: avoid; /* 避免列內容被切斷 */

                        }

                        th, td {

                            border: 1px solid #dee2e6;

                            padding: 10px;

                            vertical-align: middle;

                        }

                        th {

                            background-color: #2a5298; /* Header Blue */

                            color: #ffffff;

                            font-weight: bold;

                            text-align: center;

                            font-size: 11pt;

                        }

                        tr:nth-child(even) {

                            background-color: #f8f9fa;

                        }

                        .text-center {

                            text-align: center;

                        }

                        .badge {

                            display: inline-block;

                            padding: 4px 10px;

                            border-radius: 12px;

                            color: #fff;

                            font-size: 10pt;

                            font-weight: bold;

                            text-align: center;

                            white-space: nowrap;

                        }

                        .badge-yes {

                            background-color: #e03131; /* Red */

                        }

                        .badge-no {

                            background-color: #2f9e44; /* Green */

                        }

                        .note {

                            font-size: 0.9em;

                            color: #666;

                            margin-top: 4px;

                            display: block;

                        }

                        /* 自定義國字編號樣式 (原始方法) */

                        .chinese-numbers {

                            list-style: none;

                            padding-left: 0;

                            margin-left: 0;

                        }



                        .chinese-numbers > li:nth-child(1)::before {

                            content: "一、";

                            font-weight: bold;

                        }



                        .chinese-numbers > li:nth-child(2)::before {

                            content: "二、";

                            font-weight: bold;

                        }



                        .chinese-numbers > li:nth-child(3)::before {

                            content: "三、";

                            font-weight: bold;

                        }



                        .chinese-numbers > li {

                            position: relative;

                            padding-left: 30px;

                            margin-bottom: 8px;

                        }

                """;

        // 讀取外部CSS檔案內容

        String listStyles = loadListStylesFromResource();

        // 合併基本樣式和列表樣式

        String style = basicStyle + listStyles + "</style>";

        // HTML 標頭

        sb.append("<!DOCTYPE html>");

        sb.append("<html><head><meta charset='UTF-8'/>");

        sb.append(style);

        sb.append("</head><body>"); // 標題
        sb.append("<h1>").append(year).append(" 年中華民國政府行政機關辦公日曆表</h1>");

        // 表格
        sb.append("<table>");
        sb.append("<thead><tr>");
        sb.append("<th style='width: 18%'>日期</th>");
        sb.append("<th style='width: 12%'>放假</th>");
        sb.append("<th style='width: 25%'>節日名稱</th>");
        sb.append("<th style='width: 15%'>類別</th>");
        sb.append("<th style='width: 30%'>說明</th>");
        sb.append("</tr></thead>");
        sb.append("<tbody>");

        for (Holiday holiday : holidays) {
            sb.append("<tr>");

            // 日期 (格式化為 YYYY/MM/DD)
            String date = holiday.getDate();
            String formattedDate = date;
            if (date != null && date.length() == 8) {
                formattedDate = date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
            }
            sb.append("<td class='text-center'>").append(formattedDate).append("</td>");

            // 是否放假 (使用 Badge 樣式)
            boolean isHoliday = holiday.isHoliday();
            String badgeClass = isHoliday ? "badge-yes" : "badge-no";
            String badgeText = isHoliday ? "放假" : "上班";

            sb.append("<td class='text-center'>");
            sb.append("<span class='badge ").append(badgeClass).append("'>").append(badgeText).append("</span>");
            sb.append("</td>");

            // 名稱
            sb.append("<td>").append(escapeHtml(holiday.getName())).append("</td>");

            // 類別
            sb.append("<td class='text-center'>").append(escapeHtml(holiday.getHolidayCategory())).append("</td>");

            // 說明 (加上 Note)
            sb.append("<td>");
            sb.append(escapeHtml(holiday.getDescription()));
            if (holiday.getNote() != null && !holiday.getNote().isEmpty()) {
                sb.append("<br/><span class='note'>(").append(escapeHtml(holiday.getNote())).append(")</span>");
            }
            sb.append("</td>");

            sb.append("</tr>");
        }

        sb.append("</tbody></table>");

        // --- 測試自定義列表樣式 ---
        sb.append("<div style='margin-top: 30px; page-break-inside: avoid;'>");
        sb.append("<h2>附件一：自定義列表樣式測試 (OpenHTMLtoPDF 相容版)</h2>");

        // 1. 標準中文數字 (一、二、三...)
        sb.append("<h3>1. 標準中文數字列表 (Class: chinese-safe)</h3>");
        sb.append("<p style='color: #666; font-size: 0.9em; margin-bottom: 10px;'>此樣式使用 CSS 窮舉法定義，確保在任何 PDF 渲染引擎中都能正確顯示中文編號。</p>");
        sb.append("<ol class='chinese-safe'>");
        for (int i = 1; i <= 12; i++) {
            sb.append("<li>這是第 ").append(i).append(" 點說明事項，測試長度與對齊效果。若文字較長會自動折行，第二行應對齊文字起始處，不會跑版到編號下方。</li>");
        }
        sb.append("</ol>");

        // 2. 公文式階層
        sb.append("<h3 style='margin-top: 20px;'>2. 公文式多層級列表 (Class: official-doc-list)</h3>");
        sb.append("<p style='color: #666; font-size: 0.9em; margin-bottom: 10px;'>此樣式模擬正式公文結構：一、 -> (一) -> 1. -> (1)</p>");
        
        sb.append("<ol class='official-doc-list'>");
        
        // 第一層 Item 1
        sb.append("<li><strong>計畫目標與範疇</strong>");
        sb.append("<ol>"); // 第二層
            sb.append("<li>短期目標：完成系統基礎建設。");
            sb.append("<ol>"); // 第三層
                sb.append("<li>建立開發環境 (Development Environment)。</li>");
                sb.append("<li>完成資料庫 Schema 設計與正規化。</li>");
                sb.append("<li>建立 CI/CD 自動化部署流程。");
                    sb.append("<ol>"); // 第四層
                        sb.append("<li>設定 GitHub Actions Workflow。</li>");
                        sb.append("<li>配置 Docker 容器化環境。</li>");
                    sb.append("</ol>");
                sb.append("</li>");
            sb.append("</ol>");
            sb.append("</li>");
            
            sb.append("<li>中期目標：導入自動化測試與監控。");
            sb.append("<ol>");
                sb.append("<li>單元測試覆蓋率達 80% 以上。</li>");
                sb.append("<li>整合 Prometheus 與 Grafana 監控儀表板。</li>");
            sb.append("</ol>");
            sb.append("</li>");
        sb.append("</ol>");
        sb.append("</li>");

        // 第一層 Item 2
        sb.append("<li><strong>執行策略與方法</strong>");
        sb.append("<ol>");
            sb.append("<li>採用敏捷開發 (Agile) 模式，每兩週進行一次 Sprint。</li>");
            sb.append("<li>每週召開進度檢核會議，確認開發狀況與風險。</li>");
            sb.append("<li>定期進行程式碼審查 (Code Review)，確保品質。</li>");
        sb.append("</ol>");
        sb.append("</li>");
        
        // 第一層 Item 3
        sb.append("<li><strong>預期效益</strong>");
        sb.append("<ol>");
            sb.append("<li>提升開發效率 30%。</li>");
            sb.append("<li>降低系統錯誤率，提升使用者滿意度。</li>");
        sb.append("</ol>");
        sb.append("</li>");

        sb.append("</ol>"); // End official-doc-list
        sb.append("</div>");

        sb.append("</body></html>");

        return sb.toString();
    }

    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * 從資源檔案中載入列表樣式
     */
    private String loadListStylesFromResource() {
        try {
            // 使用 ClassPathResource 讀取 CSS 檔案
            ClassPathResource resource = new ClassPathResource("static/list-styles.css");
            if (resource.exists()) {
                String cssContent = org.springframework.util.StreamUtils.copyToString(
                        resource.getInputStream(),
                        java.nio.charset.StandardCharsets.UTF_8);
                // 確保 CSS 內容被正確包含在 style 標籤中
                return cssContent;
            }
        } catch (Exception e) {
            log.error("載入列表樣式時發生錯誤", e);
            // 如果載入失敗，返回空字串，使用內聯的基本樣式
        }

        // 如果無法載入外部 CSS，返回預設的列表樣式
        return StringUtils.EMPTY;
    }
}
