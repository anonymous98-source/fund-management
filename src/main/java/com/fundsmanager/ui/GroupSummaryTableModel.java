package fundsmanager.ui;

import fundsmanager.model.GroupSummary;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class GroupSummaryTableModel extends AbstractTableModel {

    private final String[] columns = {
            "Group",
            "Total Percentage (%)",
            "Total Amount"
    };

    private List<GroupSummary> data;

    public void setData(List<GroupSummary> data) {
        this.data = data;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        GroupSummary g = data.get(row);
        return switch (col) {
            case 0 -> g.getGroupType().getDisplayName();
            case 1 -> g.getTotalPercentage();
            default -> "₹ " + String.format("%.2f", g.getTotalAmount());
        };
    }
}
