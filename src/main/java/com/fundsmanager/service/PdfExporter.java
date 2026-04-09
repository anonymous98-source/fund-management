package fundsmanager.service;

import fundsmanager.model.FundAllocation;
import fundsmanager.model.GroupSummary;
import fundsmanager.util.ChartImageUtil;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

public class PdfExporter {

    public void export(
            File file,
            List<FundAllocation> allocations,
            List<GroupSummary> groupSummaries
    ) throws Exception {

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        document.add(new Paragraph(
                "Funds Management Report",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)
        ));
        document.add(Chunk.NEWLINE);

        document.add(new Paragraph("Category Allocation"));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        table.addCell("Category");
        table.addCell("Percentage");
        table.addCell("Amount");

        for (FundAllocation fa : allocations) {
            table.addCell(fa.getCategory());
            table.addCell(String.valueOf(fa.getPercentage()));
            table.addCell(String.format("%.2f", fa.getAmount()));
        }

        document.add(table);
        document.add(Chunk.NEWLINE);

        document.add(new Paragraph("Group Summary"));
        document.add(Chunk.NEWLINE);

        PdfPTable gTable = new PdfPTable(3);
        gTable.setWidthPercentage(100);

        gTable.addCell("Group");
        gTable.addCell("Total %");
        gTable.addCell("Total Amount");

        for (GroupSummary gs : groupSummaries) {
            gTable.addCell(gs.getGroupType().getDisplayName());
            gTable.addCell(String.valueOf(gs.getTotalPercentage()));
            gTable.addCell(String.format("%.2f", gs.getTotalAmount()));
        }

        document.add(gTable);
        document.add(Chunk.NEWLINE);


        BufferedImage pie = ChartImageUtil.createGroupPieChart(
                groupSummaries, 600, 350);

        BufferedImage bar = ChartImageUtil.createBarChartImage(allocations, 600, 350);

        BufferedImage combined = ChartImageUtil.combineChartsVertical(pie, bar);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(combined, "png", baos);
        Image chartsImg = Image.getInstance(baos.toByteArray());

        chartsImg.setAlignment(Image.ALIGN_CENTER);
        document.add(chartsImg);


        document.addAuthor("rugved.dev");

        document.close();
    }
}
