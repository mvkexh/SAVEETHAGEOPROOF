package com.saveethageotag.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ExcelReportGenerator {
    private static final Logger logger = LogManager.getLogger(ExcelReportGenerator.class);

    public static void generateExcel(List<ReportManager.TestResult> results, String filePath) {
        logger.info("Generating Excel Report at: {}", filePath);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Creation of colors and styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle passStyle = createStatusStyle(workbook, IndexedColors.LIGHT_GREEN, IndexedColors.GREEN);
            CellStyle failStyle = createStatusStyle(workbook, IndexedColors.LIGHT_TURQUOISE, IndexedColors.RED);
            CellStyle bodyStyle = createBodyStyle(workbook);

            // Sheet 1: Executive Summary
            createSummarySheet(workbook, results);

            // Sheet 2: All Test Results
            createAllResultsSheet(workbook, results, headerStyle, passStyle, failStyle, bodyStyle);

            // Sheet 3: Failed Tests
            createFailedTestsSheet(workbook, results, headerStyle, failStyle, bodyStyle);

            // Sheet 4: Screen Coverage
            createScreenCoverageSheet(workbook, results, headerStyle, bodyStyle);

            // Save Workbook
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            logger.info("Excel Report written successfully.");
        } catch (IOException e) {
            logger.error("Error writing Excel report: {}", e.getMessage(), e);
        }
    }

    private static void createSummarySheet(Workbook workbook, List<ReportManager.TestResult> results) {
        Sheet sheet = workbook.createSheet("📊 Executive Summary");
        sheet.setDisplayGridlines(true);

        // Calculate KPI values
        long total = results.size();
        long passed = results.stream().filter(r -> "PASSED".equalsIgnoreCase(r.status)).count();
        long failed = results.stream().filter(r -> "FAILED".equalsIgnoreCase(r.status)).count();
        long skipped = results.stream().filter(r -> "SKIPPED".equalsIgnoreCase(r.status)).count();
        double passRate = total > 0 ? ((double) passed / total) * 100 : 0.0;
        long duration = results.stream().mapToLong(r -> r.duration).sum();

        // Title Row
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("🎓 SAVEETHA GEOTAG APPLICATION — E2E TEST SUMMARY REPORT");
        titleCell.setCellStyle(createTitleStyle(workbook));
        sheet.setColumnWidth(0, 8000);

        // KPI Rows
        String[][] kpis = {
                {"Total Test Cases", String.valueOf(total)},
                {"Passed Tests", String.valueOf(passed)},
                {"Failed Tests", String.valueOf(failed)},
                {"Skipped Tests", String.valueOf(skipped)},
                {"Pass Rate (%)", String.format("%.2f%%", passRate)},
                {"Total Duration", String.format("%.2fs", (double) duration / 1000.0)}
        };

        int rowNum = 2;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Metric Name");
        headerRow.createCell(1).setCellValue("Value");
        headerRow.getCell(0).setCellStyle(createHeaderStyle(workbook));
        headerRow.getCell(1).setCellStyle(createHeaderStyle(workbook));

        for (String[] kpi : kpis) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(kpi[0]);
            row.createCell(1).setCellValue(kpi[1]);
            row.getCell(0).setCellStyle(createBodyStyle(workbook));
            row.getCell(1).setCellStyle(createBodyStyle(workbook));
        }

        // Category Breakdown Header
        rowNum += 2;
        Row breakdownHeader = sheet.createRow(rowNum++);
        breakdownHeader.createCell(0).setCellValue("Test Category (11 Areas)");
        breakdownHeader.createCell(1).setCellValue("Total Count");
        breakdownHeader.getCell(0).setCellStyle(createHeaderStyle(workbook));
        breakdownHeader.getCell(1).setCellStyle(createHeaderStyle(workbook));

        Map<String, Integer> categoriesMap = new LinkedHashMap<>();
        String[] categories = {
                "Functional Testing", "UI/UX Testing", "Compatibility Testing", "Performance Testing",
                "Security Testing", "API Testing", "Firebase Database Testing", "Accessibility Testing",
                "Mobile Specific Testing", "Regression Testing", "End-to-End Testing"
        };
        for (String cat : categories) {
            categoriesMap.put(cat, 0);
        }
        for (ReportManager.TestResult r : results) {
            categoriesMap.put(r.category, categoriesMap.getOrDefault(r.category, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : categoriesMap.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
            row.getCell(0).setCellStyle(createBodyStyle(workbook));
            row.getCell(1).setCellStyle(createBodyStyle(workbook));
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private static void createAllResultsSheet(Workbook workbook, List<ReportManager.TestResult> results,
                                              CellStyle headerStyle, CellStyle passStyle, CellStyle failStyle, CellStyle bodyStyle) {
        Sheet sheet = workbook.createSheet("📋 All Test Results");
        sheet.setDisplayGridlines(true);

        String[] headers = {"#", "TC ID", "Suite Name", "Test Case Name", "Category", "Screen", "Status", "Duration (ms)", "Error Details"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (int idx = 0; idx < results.size(); idx++) {
            ReportManager.TestResult r = results.get(idx);
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(idx + 1);
            row.createCell(1).setCellValue(r.tcId);
            row.createCell(2).setCellValue(r.suiteName);
            row.createCell(3).setCellValue(r.testName);
            row.createCell(4).setCellValue(r.category);
            row.createCell(5).setCellValue(r.screen);
            
            Cell statusCell = row.createCell(6);
            statusCell.setCellValue(r.status);
            if ("PASSED".equalsIgnoreCase(r.status)) {
                statusCell.setCellStyle(passStyle);
            } else {
                statusCell.setCellStyle(failStyle);
            }

            row.createCell(7).setCellValue(r.duration);
            row.createCell(8).setCellValue(r.error != null ? r.error : "");

            // Apply body style to non-status cells
            for (int i = 0; i < headers.length; i++) {
                if (i != 6) {
                    row.getCell(i).setCellStyle(bodyStyle);
                }
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void createFailedTestsSheet(Workbook workbook, List<ReportManager.TestResult> results,
                                               CellStyle headerStyle, CellStyle failStyle, CellStyle bodyStyle) {
        Sheet sheet = workbook.createSheet("❌ Failed Tests");
        sheet.setDisplayGridlines(true);

        String[] headers = {"TC ID", "Suite Name", "Test Case Name", "Screen", "Duration (ms)", "Error Message", "Screenshot Path"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (ReportManager.TestResult r : results) {
            if ("FAILED".equalsIgnoreCase(r.status)) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.tcId);
                row.createCell(1).setCellValue(r.suiteName);
                row.createCell(2).setCellValue(r.testName);
                row.createCell(3).setCellValue(r.screen);
                row.createCell(4).setCellValue(r.duration);
                row.createCell(5).setCellValue(r.error);
                row.createCell(6).setCellValue(r.screenshotPath != null ? r.screenshotPath : "");

                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(bodyStyle);
                }
            }
        }

        if (rowNum == 1) {
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("🎉 No failures recorded. All tests passed!");
            row.getCell(0).setCellStyle(bodyStyle);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void createScreenCoverageSheet(Workbook workbook, List<ReportManager.TestResult> results, CellStyle headerStyle, CellStyle bodyStyle) {
        Sheet sheet = workbook.createSheet("🗺 Screen Coverage");
        sheet.setDisplayGridlines(true);

        String[] headers = {"Screen / Module Name", "Total Tests", "Passed Tests", "Failed Tests", "Status"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Aggregate by screen
        Map<String, List<ReportManager.TestResult>> screenMap = new LinkedHashMap<>();
        for (ReportManager.TestResult r : results) {
            screenMap.putIfAbsent(r.screen, new ArrayList<>());
            screenMap.get(r.screen).add(r);
        }

        int rowNum = 1;
        for (Map.Entry<String, List<ReportManager.TestResult>> entry : screenMap.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            long tot = entry.getValue().size();
            long pass = entry.getValue().stream().filter(r -> "PASSED".equalsIgnoreCase(r.status)).count();
            long fail = entry.getValue().stream().filter(r -> "FAILED".equalsIgnoreCase(r.status)).count();
            String state = fail > 0 ? "❌ Action Required" : "✅ Complete";

            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(tot);
            row.createCell(2).setCellValue(pass);
            row.createCell(3).setCellValue(fail);
            row.createCell(4).setCellValue(state);

            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(bodyStyle);
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // --- Styling Helpers ---
    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createStatusStyle(Workbook workbook, IndexedColors bg, IndexedColors fg) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        font.setColor(fg.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(bg.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createBodyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        return style;
    }
}
