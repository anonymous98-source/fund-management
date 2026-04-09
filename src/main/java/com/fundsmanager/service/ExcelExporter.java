package fundsmanager.service;


import fundsmanager.model.FundAllocation;
import fundsmanager.model.GroupSummary;
import fundsmanager.util.ChartImageUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.List;

public class ExcelExporter {

    public void export(
            File file,
            List<FundAllocation> allocations,
            List<GroupSummary> groupSummaries
    ) throws Exception {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Funds Report");

        int rowIdx = 0;

        // ---------- TITLE ----------
        Row title = sheet.createRow(rowIdx++);
        title.createCell(0).setCellValue("Funds Management Report");

        rowIdx++;

        // ---------- CATEGORY TABLE ----------
        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Category");
        header.createCell(1).setCellValue("Percentage");
        header.createCell(2).setCellValue("Amount");

        for (FundAllocation fa : allocations) {
            Row r = sheet.createRow(rowIdx++);
            r.createCell(0).setCellValue(fa.getCategory());
            r.createCell(1).setCellValue(fa.getPercentage());
            r.createCell(2).setCellValue(fa.getAmount());
        }

        rowIdx += 2;

        // ---------- GROUP SUMMARY ----------
        Row gHeader = sheet.createRow(rowIdx++);
        gHeader.createCell(0).setCellValue("Group");
        gHeader.createCell(1).setCellValue("Total %");
        gHeader.createCell(2).setCellValue("Total Amount");

        for (GroupSummary gs : groupSummaries) {
            Row r = sheet.createRow(rowIdx++);
            r.createCell(0).setCellValue(gs.getGroupType().getDisplayName());
            r.createCell(1).setCellValue(gs.getTotalPercentage());
            r.createCell(2).setCellValue(gs.getTotalAmount());
        }

        rowIdx += 2;

        // ---------- PIE CHART IMAGE ----------
        var image = ChartImageUtil.createGroupPieChart(
                groupSummaries, 500, 350
        );

        ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
        ImageIO.write(image, "png", imgBytes);

        int pictureIdx = workbook.addPicture(
                imgBytes.toByteArray(),
                Workbook.PICTURE_TYPE_PNG
        );

        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
        anchor.setCol1(0);
        anchor.setRow1(rowIdx);

        drawing.createPicture(anchor, pictureIdx);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
    }
}


