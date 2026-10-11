package gui;

import database.DatabaseOperations;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import model.Attendance;
import model.Employee;
import model.Payroll;
import service.PayrollProcessor;

public class PayrollForm extends JPanel {
    private final DatabaseOperations db = new DatabaseOperations();
    private final PayrollProcessor processor = new PayrollProcessor();
    private final JComboBox<String> employee = new JComboBox<>();
    private final JComboBox<String> month = Ui.months();
    private final JTextField year = new JTextField("2026");
    private final JTextArea result = new JTextArea(12, 40);

    public PayrollForm() {
        setLayout(new BorderLayout(8, 8));
        result.setEditable(false);
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Employee")); form.add(employee);
        form.add(new JLabel("Month")); form.add(month);
        form.add(new JLabel("Year")); form.add(year);
        JPanel buttons = new JPanel();
        buttons.add(Ui.button("Reload Employees", e -> loadEmployees()));
        buttons.add(Ui.button("Generate Payroll", e -> generate()));
        JPanel north = new JPanel(new BorderLayout(0, 8));
        north.add(form, BorderLayout.CENTER);
        north.add(buttons, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        add(result, BorderLayout.CENTER);
        loadEmployees();
    }

    private void loadEmployees() {
        try {
            employee.removeAllItems();
            for (Employee item : db.getAllEmployees()) employee.addItem(item.getEmployeeId() + " " + item.getName());
        } catch (Exception ex) { Ui.error(this, ex); }
    }

    private void generate() {
        try {
            if (employee.getSelectedItem() == null) throw new IllegalArgumentException("Add an employee first");
            String selected = String.valueOf(employee.getSelectedItem());
            int employeeId = Integer.parseInt(selected.substring(0, selected.indexOf(' ')));
            Payroll payroll = processor.process(employeeId, month.getSelectedIndex() + 1, Ui.integer(year, "Year"));
            result.setText(String.format("""
                    Employee     : %s (%d)
                    Period       : %s %d
                    Attendance   : %.2f%%
                    Gross Salary : %s
                    Deductions   : %s
                    Net Salary   : %s
                    """,
                    payroll.getEmployeeName(), payroll.getEmployeeId(),
                    Attendance.monthName(payroll.getMonth()), payroll.getYear(),
                    payroll.getAttendancePercent(),
                    DatabaseOperations.money(payroll.getGrossSalary()),
                    DatabaseOperations.money(payroll.getTotalDeduction()),
                    DatabaseOperations.money(payroll.getNetSalary())));
        } catch (Exception ex) { Ui.error(this, ex); }
    }
}
