package service;

import database.DatabaseOperations;
import model.Attendance;
import model.Employee;
import model.Payroll;
import model.PermanentEmployee;

public class PayrollProcessor {
    private final DatabaseOperations db = new DatabaseOperations();

    public Payroll process(int employeeId, int month, int year) throws Exception {
        Employee employee = db.getEmployee(employeeId);
        if (employee == null) throw new IllegalArgumentException("Employee not found");
        Attendance attendance = db.getAttendance(employeeId, month, year);
        if (attendance == null) throw new IllegalArgumentException("Add attendance for that month before generating payroll");

        double gross = employee.calculateSalary();
        double deduction = db.getDeduction(employeeId);
        double percent = attendance.calculateAttendancePercentage();
        if (percent < 75) {
            if (employee instanceof PermanentEmployee permanent) gross -= permanent.getBonus();
            else deduction += employee.getBasicSalary() * 0.10;
        }
        gross = Math.round(gross * 100.0) / 100.0;
        deduction = Math.round(Math.min(deduction, gross) * 100.0) / 100.0;
        Payroll payroll = new Payroll(0, employeeId, employee.getName(), month, year,
                gross, deduction, Math.round((gross - deduction) * 100.0) / 100.0, percent);
        db.savePayroll(payroll);
        return payroll;
    }
}
