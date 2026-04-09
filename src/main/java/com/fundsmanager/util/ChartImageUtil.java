package fundsmanager.util;


import fundsmanager.model.FundAllocation;
import fundsmanager.model.GroupSummary;
import fundsmanager.model.GroupType;
import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ChartImageUtil {

    public static BufferedImage createGroupPieChart(
            List<GroupSummary> summaries,
            int width,
            int height
    ) {

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (GroupSummary gs : summaries) {
            dataset.setValue(
                    gs.getGroupType().getDisplayName(),
                    gs.getTotalAmount()
            );
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Expenses vs Savings",
                dataset,
                true,
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint(
                GroupType.EXPENSE.getDisplayName(),
                new Color(220, 53, 69)
        );
        plot.setSectionPaint(
                GroupType.SAVING.getDisplayName(),
                new Color(40, 167, 69)
        );

        return chart.createBufferedImage(width, height);
    }

    public static BufferedImage combineChartsVertical(
            BufferedImage pie,
            BufferedImage bar) {

        int width = Math.max(pie.getWidth(), bar.getWidth());
        int height = pie.getHeight() + bar.getHeight();

        BufferedImage combined =
                new BufferedImage(
                        width,
                        height,
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D g = combined.createGraphics();

        // White background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.drawImage(pie, 0, 0, null);
        g.drawImage(bar, 0, pie.getHeight(), null);

        g.dispose();

        return combined;
    }

    public static BufferedImage createBarChartImage(
            List<FundAllocation> allocations,
            int width,
            int height) {

        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();

        for (FundAllocation fa : allocations) {
            dataset.addValue(
                    fa.getAmount(),
                    "Amount",
                    fa.getCategory()
            );
        }

        JFreeChart chart =
                ChartFactory.createBarChart(
                        "Category Allocation",
                        "Category",
                        "Amount",
                        dataset
                );

        return chart.createBufferedImage(width, height);
    }
}
