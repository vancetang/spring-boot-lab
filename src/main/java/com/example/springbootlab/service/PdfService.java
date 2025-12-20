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

        // 測試用多種列表方法
        sb.append("<h2>測試列表功能 (替代方法)</h2>");

        // 大寫中文數字列表 (1-10)
        sb.append("<h3>大寫中文數字列表 (壹、貳、參...)</h3>");
        sb.append("<ol class='chinese-upper'>");
        for (int i = 1; i <= 10; i++) {
            sb.append("<li>項目 ").append(i).append("</li>");
        }
        sb.append("</ol>");

        // 小寫中文數字列表 (1-10)
        sb.append("<h3>小寫中文數字列表 (一、二、三...)</h3>");
        sb.append("<ol class='chinese-lower'>");
        for (int i = 1; i <= 10; i++) {
            sb.append("<li>項目 ").append(i).append("</li>");
        }
        sb.append("</ol>");

        // 阿拉伯數字列表
        sb.append("<h3>阿拉伯數字列表 (1. 2. 3...)</h3>");
        sb.append("<ol class='arabic-number'>");
        for (int i = 1; i <= 10; i++) {
            sb.append("<li>項目 ").append(i).append("</li>");
        }
        sb.append("</ol>");

        // 多層級列表
        sb.append("<h3>多層級列表 (第一層: 壹、貳... 第二層: 一、二... 第三層: 1. 2...)</h3>");
        sb.append("<ul class='multi-level-list'>");
        for (int i = 1; i <= 5; i++) {
            sb.append("<li>第一層項目 ").append(i);
            sb.append("<ul>"); // 第二層
            for (int j = 1; j <= 3; j++) {
                sb.append("<li>第二層項目 ").append(j);
                sb.append("<ul>"); // 第三層
                for (int k = 1; k <= 3; k++) {
                    sb.append("<li>第三層項目 ").append(k).append("</li>");
                }
                sb.append("</ul>");
                sb.append("</li>");
            }
            sb.append("</ul>");
            sb.append("</li>");
        }
        sb.append("</ul>");

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
