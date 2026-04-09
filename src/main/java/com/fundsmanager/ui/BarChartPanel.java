package fundsmanager.ui;

import fundsmanager.model.FundAllocation;
import fundsmanager.util.CurrencyContext;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;

public class BarChartPanel extends JPanel {

    private final JPanel chartHolder;
    private boolean horizontal = false;
    private boolean showPercentage = false;

    public BarChartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Category Allocation"));

        chartHolder = new JPanel(new BorderLayout());
        add(chartHolder, BorderLayout.CENTER);
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public void setShowPercentage(boolean showPercentage) {
        this.showPercentage = showPercentage;
    }

    public void updateChart(List<FundAllocation> allocations) {

        chartHolder.removeAll();

        if (allocations == null || allocations.isEmpty()) {
            repaint();
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (FundAllocation fa : allocations) {
            double value = showPercentage
                    ? fa.getPercentage()
                    : fa.getAmount();

            dataset.addValue(value, "Value", fa.getCategory());
        }

        PlotOrientation orientation =
                horizontal
                        ? PlotOrientation.HORIZONTAL
                        : PlotOrientation.VERTICAL;

        JFreeChart chart = ChartFactory.createBarChart(
                "Category Allocation",
                horizontal ? "Value" : "Category",
                horizontal ? "Category" : "Value",
                dataset,
                orientation,
                false,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();


        GradientPaint gp = new GradientPaint(
                0f, 0f, new Color(52, 152, 219),
                0f, 0f, new Color(41, 128, 185)
        );
        renderer.setSeriesPaint(0, gp);

        // -------- VALUE LABELS --------
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator()
        );

        renderer.setDefaultItemLabelFont(
                new Font("Segoe UI", Font.BOLD, 11)
        );


        if (!showPercentage) {
            NumberAxis rangeAxis =
                    (NumberAxis) plot.getRangeAxis();

            NumberFormat currencyFormat =
                    NumberFormat.getCurrencyInstance();
            currencyFormat.setCurrency(
                    CurrencyContext.get().getCurrency()
            );

            rangeAxis.setNumberFormatOverride(currencyFormat);
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);

        chartHolder.add(chartPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void clear() {
        chartHolder.removeAll();
        repaint();
    }
}
