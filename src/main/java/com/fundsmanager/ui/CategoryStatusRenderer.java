package fundsmanager.ui;

import fundsmanager.model.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CategoryStatusRenderer extends DefaultTableCellRenderer {

    private final Icon lockIcon;
    private final Icon editIcon;

    public CategoryStatusRenderer() {
        lockIcon = loadIcon("/icons/lock.png");
        editIcon = loadIcon("/icons/edit.png");
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        CategoryTableModel model =
                (CategoryTableModel) table.getModel();
        Category category = model.getCategories().get(row);


        if (column == 0) {
            if (category.isLocked()) {
                setIcon(lockIcon);
                setToolTipText("Default category (name locked)");
            } else {
                setIcon(editIcon);
                setToolTipText("User category (editable)");
            }
        } else {
            setIcon(null);
            setToolTipText(null);
        }


        if (!isSelected) {
            if (category.isLocked()) {
                setBackground(new Color(245, 245, 245));
            } else {
                setBackground(Color.WHITE);
            }
        }

        setForeground(Color.DARK_GRAY);

        return this;
    }

    private Icon loadIcon(String path) {
        try {
            return new ImageIcon(
                    CategoryStatusRenderer.class.getResource(path)
            );
        } catch (Exception e) {
            return null;
        }
    }
}
