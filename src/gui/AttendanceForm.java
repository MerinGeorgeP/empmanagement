package gui;

import database.DatabaseOperations;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.Attendance;
import model.Employee;

public class AttendanceForm extends JPanel {
    private final DatabaseOperations db = new DatabaseOperations();
    private final JComboBox<String> employee = new JComboBox<>();
    private final JComboBox<String> month = Ui.months();
    private final JTextField year = new JTextField("2026");
    private final JTextField working = new JTextField();
    private final JTextField present = new JTextField();
    private final JTextField leave = new JTextField();
    private final JLabel percent = new JLabel("Attendance: -");
    private final DefaultTableModel tableModel = Ui.model(
            "Employee", "Month", "Year", "Working", "Present", "Leave", "Percent");
    private final JTable table = new JTable(tableModel);

    public AttendanceForm() {
        setLayout(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Employee")); form.add(employee);
        form.add(new JLabel("Month")); form.add(month);
        form.add(new JLabel("Year")); form.add(year);
        form.add(new JLabel("Working Days")); form.add(working);
        form.add(new JLabel("Present Days")); form.add(present);
        form.add(new JLabel("Leave Days")); form.add(leave);
        form.add(new JLabel("")); form.add(percent);
        JPanel buttons = new JPanel();
        buttons.add(Ui.button("Add", e -> save(false)));
        buttons.add(Ui.button("Update", e -> save(true)));
        buttons.add(Ui.button("View", e -> refresh()));
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

    private Attendance read() {
        String selected = String.valueOf(employee.getSelectedItem());
        int employeeId = Integer.parseInt(selected.substring(0, selected.indexOf(' ')));
        int monthNo = month.getSelectedIndex() + 1;
        int y = Ui.integer(year, "Year");
        int w = Ui.integer(working, "Working days");
        int p = Ui.integer(present, "Present days");
        int l = Ui.integer(leave, "Leave days");
        Attendance.validate(monthNo, y, w, p, l);
        return new Attendance(0, employeeId, monthNo, y, w, p, l);
    }

    private void save(boolean update) {
        try {
            Attendance attendance = read();
            if (update) db.updateAttendance(attendance);
            else db.addAttendance(attendance);
            percent.setText(String.format("Attendance: %.2f%%", attendance.calculateAttendancePercentage()));
            Ui.info(this, update ? "Attendance updated" : "Attendance saved");
            refresh();
        } catch (Exception ex) { Ui.error(this, ex); }
    }

    private void refresh() {
        try {
            employee.removeAllItems();
            List<Employee> employees = db.getAllEmployees();
            for (Employee item : employees) employee.addItem(item.getEmployeeId() + " " + item.getName());
            Ui.clear(tableModel);
            for (Attendance attendance : db.getAllAttendance()) {
                String label = String.valueOf(attendance.getEmployeeId());
                for (Employee item : employees) {
                    if (item.getEmployeeId() == attendance.getEmployeeId()) label = item.getEmployeeId() + " " + item.getName();
                }
                tableModel.addRow(new Object[]{
                    label, Attendance.monthName(attendance.getMonth()), attendance.getYear(),
                    attendance.getWorkingDays(), attendance.getPresentDays(), attendance.getLeaveDays(),
                    String.format("%.2f", attendance.calculateAttendancePercentage())
                });
            }
        } catch (Exception ex) { Ui.error(this, ex); }
    }

    private void fill(int row) {
        employee.setSelectedItem(tableModel.getValueAt(row, 0));
        month.setSelectedItem(tableModel.getValueAt(row, 1));
        year.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        working.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        present.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        leave.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        percent.setText("Attendance: " + tableModel.getValueAt(row, 6) + "%");
    }
}
