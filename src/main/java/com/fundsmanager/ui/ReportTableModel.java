package fundsmanager.ui;

import fundsmanager.model.FundAllocation;
import fundsmanager.util.MoneyUtil;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ReportTableModel extends AbstractTableModel {

    private final String[] columns = {
            "Category",
            "Percentage (%)",
            "Amount"
    };

    private List<FundAllocation> data;

    public void setData(List<FundAllocation> data) {
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
        FundAllocation fa = data.get(row);
        return switch (col) {
            case 0 -> fa.getCategory();
            case 1 -> fa.getPercentage();
            default -> MoneyUtil.format(fa.getAmount());
        };
    }
}

