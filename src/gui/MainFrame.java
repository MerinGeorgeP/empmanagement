package gui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("Employee Payroll Management");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Employees", new EmployeeForm());
        tabs.addTab("Attendance", new AttendanceForm());
        tabs.addTab("Payroll", new PayrollForm());
        tabs.addTab("History", new PayrollHistoryForm());
        add(tabs);
        setSize(860, 560);
        setLocationRelativeTo(null);
    }
}
