package fundsmanager.ui;

import fundsmanager.model.Category;


import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CategoryTableModel extends AbstractTableModel {

    private static final double TARGET_TOTAL = 100.0;

    private final String[] columns = {"Category Name", "Percentage (%)"};
    private List<Category> categories;

    public CategoryTableModel(List<Category> categories) {
        this.categories = categories;
    }


    @Override
    public int getRowCount() {
        return categories.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 1 ? Double.class : String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Category c = categories.get(rowIndex);
        return columnIndex == 0 ? c.getName() : c.getPercentage();
    }


    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        Category category = categories.get(rowIndex);
        return !category.isLocked();
    }


    @Override
    public void setValueAt(Object value, int row, int column) {

        Category category = categories.get(row);

        if (column == 0) {
            if (!category.isLocked()) {
                category.setName(value.toString().trim());
            }
        }

        if (column == 1) {
            try {
                double newPercent = Double.parseDouble(value.toString());

                if (newPercent < 0) {
                    throw new IllegalArgumentException(
                            "Percentage cannot be negative."
                    );
                }

                if (newPercent > 100) {
                    throw new IllegalArgumentException(
                            "Percentage cannot exceed 100."
                    );
                }

                double totalWithoutCurrent = categories.stream()
                        .filter(c -> c != category)
                        .mapToDouble(Category::getPercentage)
                        .sum();

                if (totalWithoutCurrent + newPercent > 100) {
                    throw new IllegalArgumentException(
                            "Total percentage cannot exceed 100%.\n"
                                    + "Current total (excluding this): "
                                    + totalWithoutCurrent
                    );
                }

                category.setPercentage(newPercent);

            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                        "Please enter a valid numeric percentage value."
                );
            }
        }

        fireTableRowsUpdated(row, row);
    }


    private void autoBalance(int editedRow) {

        double lockedTotal = categories.stream()
                .filter(Category::isLocked)
                .mapToDouble(Category::getPercentage)
                .sum();

        double editedValue = categories.get(editedRow).getPercentage();
        double remaining = TARGET_TOTAL - lockedTotal - editedValue;

        if (remaining < 0) return;

        List<Category> adjustable = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            Category c = categories.get(i);
            if (!c.isLocked() && i != editedRow) {
                adjustable.add(c);
            }
        }

        if (adjustable.isEmpty()) return;

        double perCategory = remaining / adjustable.size();

        for (Category c : adjustable) {
            c.setPercentage(round(perCategory));
        }
    }


    private void normalize() {
        double total = categories.stream()
                .mapToDouble(Category::getPercentage)
                .sum();

        double diff = TARGET_TOTAL - total;

        if (Math.abs(diff) < 0.01) return;

        for (Category c : categories) {
            if (!c.isLocked()) {
                c.setPercentage(round(c.getPercentage() + diff));
                break;
            }
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }


    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        fireTableDataChanged();
    }

    public void addCategory(Category category) {
        categories.add(category);
        normalize();
        fireTableDataChanged();
    }

    public void removeCategory(int row) {

        Category category = categories.get(row);

        if (category.isLocked()) {
            throw new IllegalStateException(
                    "Default categories cannot be removed."
            );
        }

        categories.remove(row);

        fireTableDataChanged();
    }
}
