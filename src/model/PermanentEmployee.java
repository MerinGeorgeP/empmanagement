package model;

public class PermanentEmployee extends Employee {
    private double allowance;
    private double bonus;

    public PermanentEmployee(int id, String name, String email, String phone,
                             String department, double basic) {
        this(id, name, email, phone, department, basic, basic * 0.10, basic * 0.05);
    }

    public PermanentEmployee(int id, String name, String email, String phone,
                             String department, double basic, double allowance, double bonus) {
        super(id, name, email, phone, department, basic);
        this.allowance = allowance;
        this.bonus = bonus;
    }

    @Override
    public double calculateSalary() {
        return getBasicSalary() + allowance + bonus;
    }

    @Override
    public String getEmployeeType() { return "PERMANENT"; }

    public double getAllowance() { return allowance; }
    public void setAllowance(double allowance) { this.allowance = allowance; }
    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { this.bonus = bonus; }
}
