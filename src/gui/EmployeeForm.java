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
import model.Employee;

public class EmployeeForm extends JPanel {
    private final DatabaseOperations db = new DatabaseOperations();
    private final JTextField id = new JTextField();
    private final JTextField name = new JTextField();
    private final JTextField email = new JTextField();
    private final JTextField phone = new JTextField();
    private final JTextField basic = new JTextField();
    private final JComboBox<String> department = new JComboBox<>(new String[]{"IT", "HR", "Finance", "Sales"});
    private final JComboBox<String> type = new JComboBox<>(new String[]{"PERMANENT", "INTERN"});
    private final DefaultTableModel tableModel = Ui.model("ID", "Name", "Email", "Phone", "Department", "Type", "Basic");
    private final JTable table = new JTable(tableModel);

    public EmployeeForm() {
        setLayout(new BorderLayout(8, 8));
        department.setEditable(true);
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        JTextField[] fields = {id, name, email, phone, basic};
        String[] labels = {"Employee ID", "Name", "Email", "Phone", "Basic Salary / Stipend"};
        for (int i = 0; i < labels.length; i++) {
            form.add(new JLabel(labels[i]));
            form.add(fields[i]);
        }
        form.add(new JLabel("Department"));
        form.add(department);
        form.add(new JLabel("Employee Type"));
        form.add(type);
        JPanel buttons = new JPanel();
        buttons.add(Ui.button("Add", e -> add()));
        buttons.add(Ui.button("Update", e -> update()));
        buttons.add(Ui.button("Delete", e -> delete()));
        buttons.add(Ui.button("Search", e -> search()));
        buttons.add(Ui.button("View All", e -> refresh()));
        buttons.add(Ui.button("Clear", e -> clear()));
        JPanel north = new JPanel(new BorderLayout(0, 8));
        north.add(form, BorderLayout.CENTER);
        north.add(buttons, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) fill(table.getSelectedRow());
        });
        refresh();
    }

    private Employee read() {
        return Employee.create(Ui.integer(id, "Employee ID"), name.getText().trim(), email.getText().trim(),
                phone.getText().trim(), String.valueOf(department.getSelectedItem()).trim(),
                String.valueOf(type.getSelectedItem()), Ui.decimal(basic, "Basic salary"));
    }

    private void add() {
        try { db.addEmployee(read()); Ui.info(this, "Employee added"); refresh(); }
        catch (Exception ex) { Ui.error(this, ex); }
    }

    private void update() {
        try { db.updateEmployee(read()); Ui.info(this, "Employee updated"); refresh(); }
        catch (Exception ex) { Ui.error(this, ex); }
    }

    private void delete() {
        try {
            db.deleteEmployee(Ui.integer(id, "Employee ID"));
            Ui.info(this, "Employee deleted");
            refresh();
        } catch (Exception ex) { Ui.error(this, ex); }
    }

    private void search() {
        try {
            Ui.clear(tableModel);
            String idText = id.getText().trim();
            String nameText = name.getText().trim().toLowerCase();
            for (Employee employee : db.getAllEmployees()) {
                boolean idMatch = !idText.isEmpty() && String.valueOf(employee.getEmployeeId()).equals(idText);
                boolean nameMatch = !nameText.isEmpty() && employee.getName().toLowerCase().contains(nameText);
                if (idMatch || nameMatch) addRow(employee);
            }
        } catch (Exception ex) { Ui.error(this, ex); }
    }

    private void refresh() {
        try {
            Ui.clear(tableModel);
            for (Employee employee : db.getAllEmployees()) addRow(employee);
        } catch (Exception ex) { Ui.error(this, ex); }
    }

    private void addRow(Employee employee) {
        tableModel.addRow(new Object[]{
            employee.getEmployeeId(), employee.getName(), employee.getEmail(), employee.getPhone(),
            employee.getDepartment(), employee.getEmployeeType(), DatabaseOperations.money(employee.getBasicSalary())
        });
    }

    private void fill(int row) {
        id.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        name.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        email.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        phone.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        department.setSelectedItem(tableModel.getValueAt(row, 4));
        type.setSelectedItem(tableModel.getValueAt(row, 5));
        basic.setText(String.valueOf(tableModel.getValueAt(row, 6)));
    }

    private void clear() {
        id.setText(""); name.setText(""); email.setText(""); phone.setText(""); basic.setText("");
        table.clearSelection();
    }
}
