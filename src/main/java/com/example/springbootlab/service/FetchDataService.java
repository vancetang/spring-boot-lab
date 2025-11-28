package com.example.springbootlab.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Service;

import com.example.springbootlab.config.OpendataProperties;
import com.example.springbootlab.model.Holiday;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OpenData 資料抓取與處理服務。
 *
 * <p>
 * 負責從政府開放資料平台下載 CSV 檔案，解析後依年份分組，
 * 並輸出為 JSON 格式供前端使用。
 * </p>
 *
 * <p>
 * 處理流程：
 * <ol>
 * <li>下載 CSV 檔案至暫存區</li>
 * <li>解析 CSV 內容並轉換為 Holiday 物件</li>
 * <li>依年份分組並輸出 JSON 檔案</li>
 * <li>產生年份索引檔 (years.json)</li>
 * <li>清理暫存檔案</li>
 * </ol>
 * </p>
 *
 * @author Spring Boot Lab
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FetchDataService {

    /** 下載連線逾時時間 (毫秒) */
    private static final int CONNECTION_TIMEOUT_MS = 10000;

    /** 下載讀取逾時時間 (毫秒) */
    private static final int READ_TIMEOUT_MS = 30000;

    /** 表示「是」的字串常數 */
    private static final String YES_STRING = "是";

    /** JSON 序列化器（由 Spring 注入） */
    private final ObjectMapper objectMapper;

    /** 開放資料設定屬性（由 Spring 注入） */
    private final OpendataProperties opendataProperties;

    /**
     * 執行資料抓取與處理的主要方法。
     *
     * <p>
     * 此方法會：
     * <ul>
     * <li>從設定的 URL 下載 CSV 檔案</li>
     * <li>解析 CSV 並建立 Holiday 物件列表</li>
     * <li>依年份分組輸出 JSON 檔案</li>
     * <li>產生年份索引檔供前端讀取</li>
     * </ul>
     * </p>
     */
    public void fetchAndProcess() {
        Path tempFile = null;
        String dataUrl = opendataProperties.holiday().url();
        try {
            log.info("開始從 OpenData 抓取資料: {}", dataUrl);

            // 步驟 1: 下載至暫存檔
            tempFile = downloadToTempFile(dataUrl);

            // 步驟 2: 解析 CSV
            List<Holiday> allHolidays = parseCsvFile(tempFile);
            log.info("成功解析 {} 筆記錄。", allHolidays.size());

            // 步驟 3: 依年份分組並輸出 JSON
            Map<String, List<Holiday>> groupedByYear = groupByYear(allHolidays);
            writeYearlyJsonFiles(groupedByYear);

            // 步驟 4: 產生年份索引檔
            writeYearsIndex(groupedByYear);

        } catch (IOException e) {
            log.error("檔案處理過程發生 I/O 錯誤", e);
        } catch (URISyntaxException e) {
            log.error("資料來源 URL 格式錯誤: {}", dataUrl, e);
        } finally {
            // 步驟 5: 清理暫存檔
            cleanupTempFile(tempFile);
        }
    }

    /**
     * 下載資料至暫存檔。
     *
     * @param dataUrl 資料來源 URL
     * @return 暫存檔路徑
     * @throws IOException        當下載失敗時
     * @throws URISyntaxException 當 URL 格式錯誤時
     */
    private Path downloadToTempFile(String dataUrl) throws IOException, URISyntaxException {
        Path tempFile = Files.createTempFile("holiday_data_", ".csv");
        log.info("下載檔案中...");
        FileUtils.copyURLToFile(
                new URI(dataUrl).toURL(),
                tempFile.toFile(),
                CONNECTION_TIMEOUT_MS,
                READ_TIMEOUT_MS);
        log.info("檔案下載成功: {}", tempFile);
        return tempFile;
    }

    /**
     * 解析 CSV 檔案並轉換為 Holiday 物件列表。
     *
     * @param csvFile CSV 檔案路徑
     * @return Holiday 物件列表
     * @throws IOException 當檔案讀取失敗時
     */
    private List<Holiday> parseCsvFile(Path csvFile) throws IOException {
        List<Holiday> holidays = new ArrayList<>();

        try (BOMInputStream bomIn = BOMInputStream.builder()
                .setInputStream(new FileInputStream(csvFile.toFile()))
                .get();
                Reader reader = new InputStreamReader(bomIn, StandardCharsets.UTF_8);
                CSVParser parser = CSVParser.builder()
                        .setReader(reader)
                        .setFormat(buildCsvFormat())
                        .get()) {

            for (CSVRecord record : parser) {
                holidays.add(mapToHoliday(record));
            }
        }
        return holidays;
    }

    /**
     * 建立 CSV 解析格式設定。
     *
     * @return CSVFormat 實例
     */
    private CSVFormat buildCsvFormat() {
        return CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .get();
    }

    /**
     * 將 CSV 記錄對應到 Holiday 物件。
     *
     * @param record CSV 記錄
     * @return Holiday 物件
     */
    private Holiday mapToHoliday(CSVRecord record) {
        return Holiday.builder()
                .date(record.get("date"))
                .year(record.get("year"))
                .name(record.get("name"))
                .isHoliday(YES_STRING.equals(record.get("isholiday")))
                .holidayCategory(record.get("holidaycategory"))
                .description(record.get("description"))
                .build();
    }

    /**
     * 依年份分組 Holiday 資料。
     *
     * @param holidays Holiday 列表
     * @return 依年份分組的 Map
     */
    private Map<String, List<Holiday>> groupByYear(List<Holiday> holidays) {
        return holidays.stream()
                .collect(Collectors.groupingBy(Holiday::getYear));
    }

    /**
     * 輸出各年份的 JSON 檔案。
     *
     * @param groupedByYear 依年份分組的資料
     * @throws IOException 當檔案寫入失敗時
     */
    private void writeYearlyJsonFiles(Map<String, List<Holiday>> groupedByYear) throws IOException {
        Path outputPath = Paths.get(opendataProperties.holiday().outputDir());
        Files.createDirectories(outputPath);

        for (Map.Entry<String, List<Holiday>> entry : groupedByYear.entrySet()) {
            String year = entry.getKey();
            List<Holiday> holidaysOfYear = entry.getValue();

            Path jsonFile = outputPath.resolve(year + ".json");
            writeJsonWithLf(jsonFile, holidaysOfYear);
            log.info("已產生 {} 年度 JSON: {}", year, jsonFile.toAbsolutePath());
        }
    }

    /**
     * 產生年份索引檔 (years.json)。
     *
     * @param groupedByYear 依年份分組的資料
     * @throws IOException 當檔案寫入失敗時
     */
    private void writeYearsIndex(Map<String, List<Holiday>> groupedByYear) throws IOException {
        List<String> sortedYears = groupedByYear.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        Path yearsFile = Paths.get(opendataProperties.holiday().outputDir(), "years.json");
        writeJsonWithLf(yearsFile, sortedYears);
        log.info("已產生年份索引檔: {}", yearsFile.toAbsolutePath());
    }

    /**
     * 將物件寫入 JSON 檔案，使用 LF 換行符號。
     *
     * @param filePath 檔案路徑
     * @param data     要序列化的資料物件
     * @throws IOException 當檔案寫入失敗時
     */
    private void writeJsonWithLf(Path filePath, Object data) throws IOException {
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8)) {
            String json = objectMapper.writeValueAsString(data);
            // 確保換行符號為 LF（移除可能的 CR）
            json = json.replace("\r\n", "\n").replace("\r", "\n");
            writer.write(json);
            // 確保檔案以 LF 結尾
            if (!json.endsWith("\n")) {
                writer.write("\n");
            }
        }
    }

    /**
     * 清理暫存檔案。
     *
     * @param tempFile 暫存檔路徑
     */
    private void cleanupTempFile(Path tempFile) {
        if (tempFile == null) {
            return;
        }
        try {
            boolean deleted = Files.deleteIfExists(tempFile);
            if (deleted) {
                log.info("暫存檔已刪除: {}", tempFile);
            }
        } catch (IOException e) {
            log.warn("無法刪除暫存檔: {}", tempFile, e);
        }
    }
}
