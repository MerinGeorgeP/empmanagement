package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Attendance;
import model.Employee;
import model.Intern;
import model.Payroll;
import model.PermanentEmployee;

public class DatabaseOperations {
    private static final String EMP = "e.employee_id, e.name, e.email, e.phone, e.department, " +
            "e.employee_type, e.basic_salary, IFNULL(s.allowance,0) allowance, " +
            "IFNULL(s.bonus,0) bonus, IFNULL(s.deduction,0) deduction";

    public void addEmployee(Employee e) throws SQLException {
        String sql = "INSERT INTO employee (employee_id, name, email, phone, department, employee_type, basic_salary) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, e.getEmployeeId());
            ps.setString(2, e.getName());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getPhone());
            ps.setString(5, e.getDepartment());
            ps.setString(6, e.getEmployeeType());
            ps.setDouble(7, e.getBasicSalary());
            ps.executeUpdate();
        }
        saveSalary(e, "INSERT INTO salary (employee_id, allowance, bonus, deduction) VALUES (?,?,?,?)");
    }

    public Employee getEmployee(int id) throws SQLException {
        String sql = "SELECT " + EMP + " FROM employee e LEFT JOIN salary s ON e.employee_id = s.employee_id WHERE e.employee_id=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapEmployee(rs) : null;
            }
        }
    }

    public List<Employee> getAllEmployees() throws SQLException {
        String sql = "SELECT " + EMP + " FROM employee e LEFT JOIN salary s ON e.employee_id = s.employee_id ORDER BY e.employee_id";
        List<Employee> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapEmployee(rs));
        }
        return list;
    }

    public void updateEmployee(Employee e) throws SQLException {
        String sql = "UPDATE employee SET name=?, email=?, phone=?, department=?, employee_type=?, basic_salary=? WHERE employee_id=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getEmail());
            ps.setString(3, e.getPhone());
            ps.setString(4, e.getDepartment());
            ps.setString(5, e.getEmployeeType());
            ps.setDouble(6, e.getBasicSalary());
            ps.setInt(7, e.getEmployeeId());
            if (ps.executeUpdate() == 0) throw new SQLException("Employee not found");
        }
        saveSalary(e, "UPDATE salary SET allowance=?, bonus=?, deduction=? WHERE employee_id=?");
    }

    public void deleteEmployee(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM employee WHERE employee_id=?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) throw new SQLException("Employee not found");
        }
    }

    public void addAttendance(Attendance a) throws SQLException {
        requireEmployee(a.getEmployeeId());
        String sql = "INSERT INTO attendance (employee_id, month, year, working_days, present_days, leave_days) VALUES (?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bindAttendance(ps, a);
            ps.executeUpdate();
        }
    }

    public Attendance getAttendance(int employeeId, int month, int year) throws SQLException {
        String sql = "SELECT * FROM attendance WHERE employee_id=? AND month=? AND year=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapAttendance(rs) : null;
            }
        }
    }

    public List<Attendance> getAllAttendance() throws SQLException {
        List<Attendance> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM attendance ORDER BY year, month, employee_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapAttendance(rs));
        }
        return list;
    }

    public void updateAttendance(Attendance a) throws SQLException {
        String sql = "UPDATE attendance SET working_days=?, present_days=?, leave_days=? WHERE employee_id=? AND month=? AND year=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, a.getWorkingDays());
            ps.setInt(2, a.getPresentDays());
            ps.setInt(3, a.getLeaveDays());
            ps.setInt(4, a.getEmployeeId());
            ps.setInt(5, a.getMonth());
            ps.setInt(6, a.getYear());
            if (ps.executeUpdate() == 0) throw new SQLException("Attendance record not found");
        }
    }

    public void savePayroll(Payroll p) throws SQLException {
        String sql = "INSERT INTO payroll (employee_id, month, year, gross_salary, total_deduction, net_salary) " +
                "VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE gross_salary=VALUES(gross_salary), " +
                "total_deduction=VALUES(total_deduction), net_salary=VALUES(net_salary)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, p.getEmployeeId());
            ps.setInt(2, p.getMonth());
            ps.setInt(3, p.getYear());
            ps.setDouble(4, p.getGrossSalary());
            ps.setDouble(5, p.getTotalDeduction());
            ps.setDouble(6, p.getNetSalary());
            ps.executeUpdate();
        }
    }

    public List<String[]> getPayrollHistory(String employee, Integer month, Integer year) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT e.name, e.employee_id, p.month, p.year, p.gross_salary, p.total_deduction, p.net_salary " +
                "FROM payroll p JOIN employee e ON e.employee_id = p.employee_id WHERE 1=1");
        List<Object> args = new ArrayList<>();
        if (employee != null && !employee.isBlank()) {
            sql.append(" AND (e.name LIKE ? OR CAST(e.employee_id AS CHAR)=?)");
            args.add("%" + employee.trim() + "%");
            args.add(employee.trim());
        }
        if (month != null) { sql.append(" AND p.month=?"); args.add(month); }
        if (year != null) { sql.append(" AND p.year=?"); args.add(year); }
        sql.append(" ORDER BY p.year, p.month, e.name");
        List<String[]> rows = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < args.size(); i++) {
                Object arg = args.get(i);
                if (arg instanceof Integer n) ps.setInt(i + 1, n);
                else ps.setString(i + 1, arg.toString());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new String[]{
                        rs.getString(1), String.valueOf(rs.getInt(2)), Attendance.monthName(rs.getInt(3)),
                        String.valueOf(rs.getInt(4)), money(rs.getDouble(5)), money(rs.getDouble(6)), money(rs.getDouble(7))
                    });
                }
            }
        }
        return rows;
    }

    public double getDeduction(int employeeId) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT deduction FROM salary WHERE employee_id=?")) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    private void requireEmployee(int id) throws SQLException {
        if (getEmployee(id) == null) throw new SQLException("Employee " + id + " does not exist");
    }

    private void saveSalary(Employee e, String sql) throws SQLException {
        double allowance = 0, bonus = 0, deduction = 500;
        if (e instanceof PermanentEmployee p) {
            allowance = p.getAllowance();
            bonus = p.getBonus();
            deduction = Math.round(e.getBasicSalary() * 0.05 * 100.0) / 100.0;
        }
        boolean insert = sql.startsWith("INSERT");
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (insert) {
                ps.setInt(1, e.getEmployeeId());
                ps.setDouble(2, allowance);
                ps.setDouble(3, bonus);
                ps.setDouble(4, deduction);
            } else {
                ps.setDouble(1, allowance);
                ps.setDouble(2, bonus);
                ps.setDouble(3, deduction);
                ps.setInt(4, e.getEmployeeId());
            }
            ps.executeUpdate();
        }
    }

    private void bindAttendance(PreparedStatement ps, Attendance a) throws SQLException {
        ps.setInt(1, a.getEmployeeId());
        ps.setInt(2, a.getMonth());
        ps.setInt(3, a.getYear());
        ps.setInt(4, a.getWorkingDays());
        ps.setInt(5, a.getPresentDays());
        ps.setInt(6, a.getLeaveDays());
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        int id = rs.getInt("employee_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String dept = rs.getString("department");
        double basic = rs.getDouble("basic_salary");
        if ("INTERN".equals(rs.getString("employee_type"))) {
            return new Intern(id, name, email, phone, dept, basic);
        }
        return new PermanentEmployee(id, name, email, phone, dept, basic, rs.getDouble("allowance"), rs.getDouble("bonus"));
    }

    private Attendance mapAttendance(ResultSet rs) throws SQLException {
        return new Attendance(rs.getInt("attendance_id"), rs.getInt("employee_id"), rs.getInt("month"),
                rs.getInt("year"), rs.getInt("working_days"), rs.getInt("present_days"), rs.getInt("leave_days"));
    }

    public static String money(double value) { return String.format("%.2f", value); }
}
