package fundsmanager.ui;

import fundsmanager.model.Category;
import fundsmanager.model.GroupType;

import javax.swing.*;
import java.awt.*;

    public class AddCategoryDialog extends JDialog {

    private JTextField nameField;
    private JSpinner percentageSpinner;
    private JComboBox<GroupType> groupCombo;
    private JCheckBox lockedCheck;

    private JButton addButton;
    private JButton cancelButton;

    private Category category;

    public AddCategoryDialog(JFrame parent) {
        super(parent, "Add New Category", true);
        setSize(420, 280);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        // ✅ Correct way to set default button
        getRootPane().setDefaultButton(addButton);
    }

    // ---------------- FORM PANEL ----------------

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Category Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Category Name"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField();
        panel.add(nameField, gbc);

        // Percentage
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Percentage (%)"), gbc);

        gbc.gridx = 1;
        percentageSpinner = new JSpinner(
                new SpinnerNumberModel(0.0, 0.0, 100.0, 0.5)
        );
        panel.add(percentageSpinner, gbc);

        // Group Type
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Group"), gbc);

        gbc.gridx = 1;
        groupCombo = new JComboBox<>(GroupType.values());
        panel.add(groupCombo, gbc);

        // Locked checkbox
        gbc.gridx = 1;
        gbc.gridy++;
        lockedCheck = new JCheckBox("Lock this category (cannot be edited)");
        panel.add(lockedCheck, gbc);

        return panel;
    }

    // ---------------- BUTTON PANEL ----------------

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        cancelButton = new JButton("Cancel");
        addButton = new JButton("Add Category");

        cancelButton.addActionListener(e -> dispose());
        addButton.addActionListener(e -> onAdd());

        panel.add(cancelButton);
        panel.add(addButton);

        return panel;
    }

    // ---------------- ACTION ----------------

    private void onAdd() {
        String name = nameField.getText().trim();
        double percent = (double) percentageSpinner.getValue();
        GroupType group = (GroupType) groupCombo.getSelectedItem();
        boolean locked = lockedCheck.isSelected();

        if (name.isEmpty()) {
            showError("Category name cannot be empty");
            return;
        }

        if (percent < 0 || percent > 100) {
            showError("Percentage must be between 0 and 100");
            return;
        }

        category = new Category(name, percent, locked, group);
        dispose();
    }

    // ---------------- UTIL ----------------

    private void showError(String msg) {
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public Category getCategory() {
        return category;
    }
}
