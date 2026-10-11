package gui;

import database.DatabaseOperations;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.Attendance;

public class PayrollHistoryForm extends JPanel {
    private final DatabaseOperations db = new DatabaseOperations();
    private final JTextField employee = new JTextField();
    private final JComboBox<String> month = new JComboBox<>();
    private final JTextField year = new JTextField();
    private final DefaultTableModel tableModel = Ui.model(
            "Employee", "ID", "Month", "Year", "Gross", "Deduction", "Net");

    public PayrollHistoryForm() {
        setLayout(new BorderLayout(8, 8));
        month.addItem("Any");
        for (String name : Attendance.MONTHS) month.addItem(name);
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Employee name or ID")); form.add(employee);
        form.add(new JLabel("Month")); form.add(month);
        form.add(new JLabel("Year")); form.add(year);
        JPanel buttons = new JPanel();
        buttons.add(Ui.button("Search", e -> load()));
        buttons.add(Ui.button("View All", e -> { employee.setText(""); month.setSelectedIndex(0); year.setText(""); load(); }));
        JPanel north = new JPanel(new BorderLayout(0, 8));
        north.add(form, BorderLayout.CENTER);
        north.add(buttons, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        load();
    }

    private void load() {
        try {
            Integer monthNo = month.getSelectedIndex() == 0 ? null : month.getSelectedIndex();
            Integer y = year.getText().isBlank() ? null : Ui.integer(year, "Year");
            Ui.clear(tableModel);
            for (String[] row : db.getPayrollHistory(employee.getText(), monthNo, y)) tableModel.addRow(row);
        } catch (Exception ex) { Ui.error(this, ex); }
    }
}
