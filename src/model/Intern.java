package model;

public class Intern extends Employee {
    public Intern(int id, String name, String email, String phone, String department, double stipend) {
        super(id, name, email, phone, department, stipend);
    }

    public double getStipend() { return getBasicSalary(); }

    public void setStipend(double stipend) { setBasicSalary(stipend); }

    @Override
    public double calculateSalary() { return getStipend(); }

    @Override
    public String getEmployeeType() { return "INTERN"; }
}
