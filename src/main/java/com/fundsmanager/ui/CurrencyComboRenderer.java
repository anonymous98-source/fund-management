package fundsmanager.ui;

import fundsmanager.model.CurrencyItem;

import javax.swing.*;
import java.awt.*;

public class CurrencyComboRenderer extends JLabel
        implements ListCellRenderer<CurrencyItem> {

    public CurrencyComboRenderer() {
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends CurrencyItem> list,
            CurrencyItem value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        if (value != null) {
            setText(value.toString());
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
