package com.example.springbootlab.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.springbootlab.model.Holiday;

@SpringBootTest
class PdfServiceTest {

    @Autowired
    private PdfService pdfService;

    @Test
    void testGenerateHolidayPdf() throws Exception {
        // Arrange
        String year = "2024";
        List<Holiday> holidays = Arrays.asList(
                Holiday.builder().date("20240101").name("元旦").isHoliday(true).holidayCategory("放假之紀念日及節日")
                        .description("開國紀念日").build(),
                Holiday.builder().date("20240208").name("春節").isHoliday(true).holidayCategory("民俗節日")
                        .description("農曆除夕前一日").build());

        // Act
        byte[] pdfBytes = pdfService.generateHolidayPdf(year, holidays);

        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        // Optional: Write to a file for manual inspection (in local env)
        // try (FileOutputStream fos = new FileOutputStream("target/test-holiday.pdf"))
        // {
        // fos.write(pdfBytes);
        // }
    }
}
