package fundsmanager.ui;


import fundsmanager.model.GroupSummary;
import fundsmanager.model.GroupType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class PieChartPanel extends JPanel {

    private final JPanel chartHolder;

    public PieChartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Expenses vs Savings"));

        chartHolder = new JPanel(new BorderLayout());
        chartHolder.setPreferredSize(new Dimension(420, 280));

        JScrollPane scrollPane = new JScrollPane(chartHolder);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateChart(List<GroupSummary> summaries) {

        chartHolder.removeAll();

        if (summaries == null || summaries.isEmpty()) {
            repaint();
            return;
        }


        DefaultPieDataset dataset = new DefaultPieDataset();
        for (GroupSummary gs : summaries) {
            dataset.setValue(
                    gs.getGroupType().getDisplayName(),
                    gs.getTotalAmount()
            );
        }


        JFreeChart chart = ChartFactory.createPieChart(
                "Overall Distribution",
                dataset,
                true,   // legend
                true,   // tooltips
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();

        // -------- COLORS --------
        plot.setSectionPaint(
                GroupType.EXPENSE.getDisplayName(),
                new Color(220, 53, 69)   // Red
        );
        plot.setSectionPaint(
                GroupType.SAVING.getDisplayName(),
                new Color(40, 167, 69)   // Green
        );

        // -------- LABELS WITH PERCENTAGE --------
        plot.setLabelGenerator(
                new StandardPieSectionLabelGenerator(
                        "{0} – {2}",
                        new DecimalFormat("0.00"),
                        new DecimalFormat("0.00%")
                )
        );

        plot.setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        plot.setLabelBackgroundPaint(Color.WHITE);
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);

        // -------- PANEL --------
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(420, 260));
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
