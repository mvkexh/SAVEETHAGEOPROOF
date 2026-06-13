package com.saveethageotag.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

public class PdfReportGenerator {
    private static final Logger logger = LogManager.getLogger(PdfReportGenerator.class);

    private static Font catFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static Font subFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.GRAY);
    private static Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
    private static Font passFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, new BaseColor(27, 94, 32));
    private static Font failFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, new BaseColor(183, 28, 28));

    public static void generatePdf(List<ReportManager.TestResult> results, String filePath) {
        logger.info("Generating PDF Report at: {}", filePath);
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Title Banner
            Paragraph title = new Paragraph("Saveetha GeoTag — Quality Assurance E2E PDF Report", catFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Subtitle / Date
            Paragraph sub = new Paragraph("Report Generated: " + new Date().toString() + "  |  Total Test Cases: " + results.size(), subFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(20);
            document.add(sub);

            // Summary Table (Executive KPI)
            PdfPTable kpiTable = new PdfPTable(2);
            kpiTable.setWidthPercentage(60);
            kpiTable.setSpacingAfter(20);
            
            long passed = results.stream().filter(r -> "PASSED".equalsIgnoreCase(r.status)).count();
            long failed = results.stream().filter(r -> "FAILED".equalsIgnoreCase(r.status)).count();
            long skipped = results.stream().filter(r -> "SKIPPED".equalsIgnoreCase(r.status)).count();
            double passRate = results.size() > 0 ? ((double) passed / results.size()) * 100 : 0.0;

            addKpiCell(kpiTable, "Total Tests", String.valueOf(results.size()));
            addKpiCell(kpiTable, "Passed Tests", String.valueOf(passed));
            addKpiCell(kpiTable, "Failed Tests", String.valueOf(failed));
            addKpiCell(kpiTable, "Skipped Tests", String.valueOf(skipped));
            addKpiCell(kpiTable, "Overall Pass Rate", String.format("%.2f%%", passRate));

            document.add(kpiTable);

            // Details Table Header
            Paragraph detailsHeader = new Paragraph("All Execution Logs Detail", subFont);
            detailsHeader.setSpacingAfter(10);
            document.add(detailsHeader);

            // Detailed Test Cases Table
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            // Widths: TC ID, Suite Name, Test Name, Category, Screen, Status
            table.setWidths(new float[]{1.0f, 2.0f, 2.5f, 2.0f, 1.8f, 1.0f});

            String[] headers = {"TC ID", "Suite", "Test Case Description", "Category", "Screen", "Status"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new BaseColor(26, 35, 126)); // Navy header
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
            }

            for (ReportManager.TestResult r : results) {
                table.addCell(new PdfPCell(new Phrase(r.tcId, normalFont)));
                table.addCell(new PdfPCell(new Phrase(r.suiteName, normalFont)));
                table.addCell(new PdfPCell(new Phrase(r.testName, normalFont)));
                table.addCell(new PdfPCell(new Phrase(r.category, normalFont)));
                table.addCell(new PdfPCell(new Phrase(r.screen, normalFont)));

                PdfPCell statusCell = new PdfPCell();
                if ("PASSED".equalsIgnoreCase(r.status)) {
                    statusCell.addElement(new Phrase("PASS", passFont));
                    statusCell.setBackgroundColor(new BaseColor(232, 245, 233)); // Light green bg
                } else {
                    statusCell.addElement(new Phrase("FAIL", failFont));
                    statusCell.setBackgroundColor(new BaseColor(255, 235, 235)); // Light red bg
                }
                statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(statusCell);
            }

            document.add(table);
            logger.info("PDF Report generated successfully.");
        } catch (Exception e) {
            logger.error("Failed to generate PDF document: {}", e.getMessage(), e);
        } finally {
            document.close();
        }
    }

    private static void addKpiCell(PdfPTable table, String label, String val) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, normalFont));
        cellLabel.setPadding(6);
        cellLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);
        
        PdfPCell cellVal = new PdfPCell(new Phrase(val, normalFont));
        cellVal.setPadding(6);
        cellVal.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(cellLabel);
        table.addCell(cellVal);
    }
}
