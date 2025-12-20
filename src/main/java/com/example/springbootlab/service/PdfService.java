package com.example.springbootlab.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.example.springbootlab.model.Holiday;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PdfService {

    public byte[] generateHolidayPdf(String year, List<Holiday> holidays) throws IOException {
        String htmlContent = generateHtml(year, holidays);

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
}
