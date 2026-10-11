package model;

public class Payroll {
    private int payrollId;
    private int employeeId;
    private String employeeName;
    private int month;
    private int year;
    private double grossSalary;
    private double totalDeduction;
    private double netSalary;
    private double attendancePercent;

    public Payroll(int payrollId, int employeeId, String employeeName, int month, int year,
                   double grossSalary, double totalDeduction, double netSalary, double attendancePercent) {
        this.payrollId = payrollId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.month = month;
        this.year = year;
        this.grossSalary = grossSalary;
        this.totalDeduction = totalDeduction;
        this.netSalary = netSalary;
        this.attendancePercent = attendancePercent;
    }

    public int getPayrollId() { return payrollId; }
    public int getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public double getGrossSalary() { return grossSalary; }
    public double getTotalDeduction() { return totalDeduction; }
    public double getNetSalary() { return netSalary; }
    public double getAttendancePercent() { return attendancePercent; }
}
