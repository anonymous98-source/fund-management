package fundsmanager.ui;

import fundsmanager.model.*;
import fundsmanager.service.ExportService;
import fundsmanager.service.FundCalculationService;
import fundsmanager.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {

    private JTextField incomeField;
    private JLabel totalLabel;
    private JComboBox<CurrencyItem> currencyCombo;

    private JTable categoryTable;
    private JTable allocationTable;
    private JTable groupSummaryTable;

    private CategoryTableModel categoryTableModel;
    private ReportTableModel allocationTableModel;
    private GroupSummaryTableModel groupSummaryTableModel;

    private PieChartPanel pieChartPanel;
    private BarChartPanel barChartPanel;

    private final FundCalculationService calculationService =
            new FundCalculationService();
    private final ExportService exportService =
            new ExportService();

    private List<FundAllocation> lastAllocations;
    private List<GroupSummary> lastGroupSummaries;

    private final Deque<List<Category>> undoStack = new ArrayDeque<>();
    private final Deque<List<Category>> redoStack = new ArrayDeque<>();


    public MainFrame() {
        setTitle("Funds Management Dashboard");
        setSize(1350, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createFooter(), BorderLayout.SOUTH);
        initModels();

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        setupKeyboardShortcuts();
        setupCategoryContextMenu();
        updateTotalLabel();

        Image icon = Toolkit.getDefaultToolkit()
                .getImage(getClass().getResource("/money-management.png"));
        setIconImage(icon);

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
        });

    }


    private void initModels() {
        categoryTableModel =
                new CategoryTableModel(deepCopy(DefaultCategories.load()));
        allocationTableModel = new ReportTableModel();
        groupSummaryTableModel = new GroupSummaryTableModel();
    }


    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        Icon icon = new ImageIcon(
                Objects.requireNonNull(CategoryStatusRenderer.class.getResource("/cost-of-living.png"))
        );

        JLabel title = new JLabel("Funds Management Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setIcon(icon);


        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));

        incomeField = new JTextField(10);
        incomeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JToggleButton orientationToggle =
                new JToggleButton("Horizontal");

        JToggleButton modeToggle =
                new JToggleButton("Show %");

        orientationToggle.addActionListener(e -> {
            barChartPanel.setHorizontal(
                    orientationToggle.isSelected()
            );
            barChartPanel.updateChart(lastAllocations);
        });

        modeToggle.addActionListener(e -> {
            barChartPanel.setShowPercentage(
                    modeToggle.isSelected()
            );
            barChartPanel.updateChart(lastAllocations);
        });

        currencyCombo = new JComboBox<>(
                CurrencyProvider.loadAll().toArray(new CurrencyItem[0])
        );
        currencyCombo.setRenderer(new CurrencyComboRenderer());
        currencyCombo.setPreferredSize(new Dimension(200, 28));

        CurrencyItem inr = null;
        for (CurrencyItem item : CurrencyProvider.loadAll()) {
            if ("INR".equals(item.getCode())) {
                inr = item;
                break;
            }
        }
        if (inr != null) {
            currencyCombo.setSelectedItem(inr);
            CurrencyContext.set(inr);
        }

        currencyCombo.addActionListener(_ -> {
            CurrencyContext.set((CurrencyItem) currencyCombo.getSelectedItem());
            allocationTable.repaint();
            groupSummaryTable.repaint();
        });

        JButton generateBtn = new JButton("Generate");
        JButton resetBtn = new JButton("Reset");
        JButton exportExcelBtn = new JButton("Export Excel");
        JButton exportPdfBtn = new JButton("Export PDF");


        totalLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        generateBtn.addActionListener(_ -> generateReport());
        resetBtn.addActionListener(_ -> resetCategories());
        exportExcelBtn.addActionListener(_ -> exportExcel());
        exportPdfBtn.addActionListener(_ -> exportPdf());


        controls.add(new JLabel("Monthly/ Yearly Income:"));
        controls.add(incomeField);
        controls.add(new JLabel("Currency:"));
        controls.add(currencyCombo);
        controls.add(generateBtn);
        controls.add(resetBtn);
        controls.add(exportExcelBtn);
        controls.add(exportPdfBtn);


        header.add(title, BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);

        return header;
    }


    private JSplitPane createMainContent() {

        JPanel leftPanel = getLeftPanel();
        JPanel rightPanel = getRightPanel();

        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel,
                rightPanel
        );
        mainSplit.setDividerLocation(460);
        mainSplit.setResizeWeight(0.35);

        return mainSplit;
    }

    /**
     * Extracted Method for rendering left panel
     *
     */
    private JPanel getLeftPanel() {
        categoryTable = new JTable(categoryTableModel);
        categoryTable.setRowHeight(28);


        CategoryStatusRenderer statusRenderer =
                new CategoryStatusRenderer();

        categoryTable.getColumnModel()
                .getColumn(0)
                .setCellRenderer(statusRenderer);

        /*categoryTable.getColumnModel()
                .getColumn(1)
                .setCellRenderer(statusRenderer);*/

        JScrollPane categoryScroll =
                new JScrollPane(categoryTable);
        categoryScroll.setBorder(
                BorderFactory.createTitledBorder("Categories")
        );

        JButton addBtn = new JButton("+ Add");
        JButton removeBtn = new JButton("Remove");

        addBtn.addActionListener(_ -> addCategory());
        removeBtn.addActionListener(_ -> removeSelectedCategory());

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        leftButtons.add(addBtn);
        leftButtons.add(removeBtn);

        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));

        JLabel legend = new JLabel(
                " Default Category   |    User Category"
        );
        legend.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        legend.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        leftPanel.add(categoryScroll, BorderLayout.CENTER);
        leftPanel.add(leftButtons, BorderLayout.SOUTH);
        leftPanel.add(legend, BorderLayout.NORTH);
        return leftPanel;
    }

    /**
     * Extracted method for rendering right panel
     *
     */
    private JPanel getRightPanel() {
        pieChartPanel = new PieChartPanel();
        barChartPanel = new BarChartPanel();

        JSplitPane chartsSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                pieChartPanel,
                barChartPanel
        );

        chartsSplit.setDividerLocation(450);
        chartsSplit.setResizeWeight(0.5);
        chartsSplit.setOneTouchExpandable(true);

        allocationTable = new JTable(allocationTableModel);
        allocationTable.setRowHeight(26);

        JScrollPane allocationScroll =
                new JScrollPane(allocationTable);
        allocationScroll.setBorder(
                BorderFactory.createTitledBorder("Category Allocation")
        );

        groupSummaryTable = new JTable(groupSummaryTableModel);
        groupSummaryTable.setRowHeight(26);

        JScrollPane groupScroll =
                new JScrollPane(groupSummaryTable);
        groupScroll.setBorder(
                BorderFactory.createTitledBorder("Group Summary")
        );

        return getRightPanelTables(allocationScroll, groupScroll, chartsSplit);
    }


    /**
     * Method for rendering tables on right panel.
     *
     */
    private JPanel getRightPanelTables(JScrollPane allocationScroll, JScrollPane groupScroll, JSplitPane chartsSplit) {
        JSplitPane tablesSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                allocationScroll,
                groupScroll
        );


        tablesSplit.setDividerLocation(220);
        tablesSplit.setResizeWeight(0.45);
        tablesSplit.setOneTouchExpandable(true);

        JSplitPane rightSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                chartsSplit,
                tablesSplit
        );

        rightSplit.setDividerLocation(350);
        rightSplit.setResizeWeight(0.5);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        rightPanel.add(rightSplit, BorderLayout.CENTER);
        return rightPanel;
    }

    /**
     * Method for adding custom category.
     *
     */
    private void addCategory() {
        saveState();
        AddCategoryDialog dialog = new AddCategoryDialog(this);
        dialog.setVisible(true);

        Category c = dialog.getCategory();
        if (c != null) {
            categoryTableModel.addCategory(c);
            updateTotalLabel();
        }
    }

    /**
     * method for removing custom/ default unlocked categories
     */
    private void removeSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a category to remove");
            return;
        }
        if (categoryTableModel.getCategories().get(row).isLocked()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Default categories cannot be removed"
            );
            return;
        }
        saveState();
        categoryTableModel.removeCategory(row);
        updateTotalLabel();
    }

    /**
     * Method for rendering required  data
     */
    private void generateReport() {
        double income;
        try {
            income = Double.parseDouble(incomeField.getText());
            if (income <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter a valid income");
            return;
        }

        if (!calculationService.validatePercentage(
                categoryTableModel.getCategories())) {
            JOptionPane.showMessageDialog(
                    this,
                    "The total allocation must equal exactly 100%.\n"
                            + "Please adjust your category percentages.",
                    "Invalid Allocation",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        List<Category> categories =
                categoryTableModel.getCategories();

        lastAllocations =
                calculationService.calculate(income, categories);
        lastGroupSummaries =
                calculationService.calculateGroupSummary(income, categories);

        allocationTableModel.setData(lastAllocations);
        groupSummaryTableModel.setData(lastGroupSummaries);
        pieChartPanel.updateChart(lastGroupSummaries);
        barChartPanel.updateChart(lastAllocations);
    }

    /**
     * Method for resetting fund manager
     *
     */
    private void resetCategories() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Reset all categories to default?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            saveState();
            categoryTableModel.setCategories(
                    deepCopy(DefaultCategories.load())
            );
            allocationTableModel.setData(null);
            groupSummaryTableModel.setData(null);
            pieChartPanel.clear();
            barChartPanel.clear();
            updateTotalLabel();
        }
    }

    /**
     * Method to Extract generated report to Excel file
     *
     */
    private void exportExcel() {
        if (lastAllocations == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Excel Report");
        chooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Excel Files (*.xlsx)", "xlsx"
                )
        );

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }

            try {
                exportService.exportExcel(
                        file,
                        lastAllocations,
                        lastGroupSummaries
                );
                JOptionPane.showMessageDialog(this, "Excel exported successfully");
            } catch (Exception ex) {
                // ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Excel export failed:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Method to Extract generated report to pdf file
     */
    private void exportPdf() {
        if (lastAllocations == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save PDF Report");
        chooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "PDF Files (*.pdf)", "pdf"
                )
        );

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }

            try {
                exportService.exportPdf(
                        file,
                        lastAllocations,
                        lastGroupSummaries
                );
                JOptionPane.showMessageDialog(this, "PDF exported successfully");
            } catch (Exception ex) {
                // ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "PDF export failed:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void updateTotalLabel() {
        double total = categoryTableModel.getCategories()
                .stream()
                .mapToDouble(Category::getPercentage)
                .sum();

        if (Math.abs(total - 100) > 0.01) {
            totalLabel.setForeground(Color.RED);
            totalLabel.setText(
                    String.format("Total: %.2f%%", total)
            );
        } else {
            totalLabel.setForeground(new Color(0, 130, 0));
            totalLabel.setText("Total: 100%");
        }
    }


    private void saveState() {
        undoStack.push(deepCopy(categoryTableModel.getCategories()));
        redoStack.clear();
    }

    private void undo() {
        if (undoStack.isEmpty()) return;
        redoStack.push(deepCopy(categoryTableModel.getCategories()));
        categoryTableModel.setCategories(undoStack.pop());
        updateTotalLabel();
    }

    private void redo() {
        if (redoStack.isEmpty()) return;
        undoStack.push(deepCopy(categoryTableModel.getCategories()));
        categoryTableModel.setCategories(redoStack.pop());
        updateTotalLabel();
    }


    private void setupKeyboardShortcuts() {
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke("ctrl G"), "generate");
        im.put(KeyStroke.getKeyStroke("ctrl R"), "reset");
        im.put(KeyStroke.getKeyStroke("ctrl Z"), "undo");
        im.put(KeyStroke.getKeyStroke("ctrl Y"), "redo");

        am.put("generate", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
        am.put("reset", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                resetCategories();
            }
        });
        am.put("undo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });
        am.put("redo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });
    }


    private List<Category> deepCopy(List<Category> src) {
        List<Category> copy = new ArrayList<>();
        for (Category c : src) {
            copy.add(new Category(
                    c.getName(),
                    c.getPercentage(),
                    c.isLocked(),
                    c.getGroupType()
            ));
        }
        return copy;
    }


    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        );

        JLabel copyright =
                new JLabel("© 2026 rugved.dev • Version v1.0",
                        SwingConstants.CENTER);

        copyright.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );
        copyright.setForeground(Color.GRAY);

        footer.add(copyright, BorderLayout.CENTER);
        return footer;
    }

    private void setupCategoryContextMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem renameItem = new JMenuItem("Rename");
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem infoItem = new JMenuItem("Category Info");

        popupMenu.add(renameItem);
        popupMenu.add(deleteItem);
        popupMenu.addSeparator();
        popupMenu.add(infoItem);

        categoryTable.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) showMenu(e);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) showMenu(e);
            }

            private void showMenu(java.awt.event.MouseEvent e) {

                int row = categoryTable.rowAtPoint(e.getPoint());

                if (row >= 0) {
                    categoryTable.setRowSelectionInterval(row, row);

                    Category selected =
                            categoryTableModel.getCategories().get(row);

                    boolean editable = !selected.isLocked();

                    renameItem.setEnabled(editable);
                    deleteItem.setEnabled(editable);

                    popupMenu.show(e.getComponent(),
                            e.getX(), e.getY());
                }
            }
        });


        renameItem.addActionListener(e -> {
            int row = categoryTable.getSelectedRow();
            if (row < 0) return;

            Category category =
                    categoryTableModel.getCategories().get(row);

            String newName = JOptionPane.showInputDialog(
                    this,
                    "Enter new category name:",
                    category.getName()
            );

            if (newName != null && !newName.trim().isEmpty()) {
                category.setName(newName.trim());
                categoryTableModel.fireTableRowsUpdated(row, row);
            }
        });

        deleteItem.addActionListener(e -> {
            int row = categoryTable.getSelectedRow();
            if (row < 0) return;

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete this category?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                categoryTableModel.removeCategory(row);
            }
        });

        infoItem.addActionListener(e -> {
            int row = categoryTable.getSelectedRow();
            if (row < 0) return;

            Category category =
                    categoryTableModel.getCategories().get(row);

            JOptionPane.showMessageDialog(
                    this,
                    "Category: " + category.getName()
                            + "\nPercentage: " + category.getPercentage()
                            + "\nType: "
                            + (category.isLocked()
                            ? "Default (Locked)"
                            : "User Added (Editable)"),
                    "Category Info",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

}
