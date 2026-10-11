package model;

public abstract class Employee {
    private int employeeId;
    private String name;
    private String email;
    private String phone;
    private String department;
    private double basicSalary;

    public Employee(int employeeId, String name, String email, String phone,
                    String department, double basicSalary) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.basicSalary = basicSalary;
    }

    public abstract double calculateSalary();

    public abstract String getEmployeeType();

    public static Employee create(int id, String name, String email, String phone,
                                  String department, String type, double basic) {
        validate(id, name, email, phone, department, basic);
        if ("INTERN".equals(type)) {
            return new Intern(id, name, email, phone, department, basic);
        }
        return new PermanentEmployee(id, name, email, phone, department, basic);
    }

    public static void validate(int id, String name, String email, String phone,
                                String department, double basic) {
        if (id <= 0) throw new IllegalArgumentException("Employee ID must be positive");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (email == null || !email.contains("@") || !email.contains("."))
            throw new IllegalArgumentException("Enter a valid email");
        if (phone == null || !phone.matches("\\d{10}"))
            throw new IllegalArgumentException("Phone must be 10 digits");
        if (department == null || department.isBlank())
            throw new IllegalArgumentException("Department is required");
        if (basic <= 0) throw new IllegalArgumentException("Basic salary must be greater than 0");
    }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
}
