CREATE DATABASE IF NOT EXISTS payroll_db;
USE payroll_db;

CREATE TABLE IF NOT EXISTS employee (
    employee_id   INT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL,
    phone         VARCHAR(15)  NOT NULL,
    department    VARCHAR(50)  NOT NULL,
    employee_type VARCHAR(20)  NOT NULL,
    basic_salary  DOUBLE       NOT NULL
);

CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id   INT NOT NULL,
    month         INT NOT NULL,
    year          INT NOT NULL,
    working_days  INT NOT NULL,
    present_days  INT NOT NULL,
    leave_days    INT NOT NULL,
    UNIQUE (employee_id, month, year),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS salary (
    salary_id   INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    allowance   DOUBLE NOT NULL,
    bonus       DOUBLE NOT NULL,
    deduction   DOUBLE NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS payroll (
    payroll_id      INT AUTO_INCREMENT PRIMARY KEY,
    employee_id     INT NOT NULL,
    month           INT NOT NULL,
    year            INT NOT NULL,
    gross_salary    DOUBLE NOT NULL,
    total_deduction DOUBLE NOT NULL,
    net_salary      DOUBLE NOT NULL,
    UNIQUE (employee_id, month, year),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE CASCADE
);

INSERT INTO employee VALUES
    (1001, 'Rahul', 'rahul@gmail.com', '9876543210', 'IT', 'PERMANENT', 40000),
    (1002, 'Anu', 'anu@gmail.com', '9876543211', 'HR', 'INTERN', 15000);

INSERT INTO salary (employee_id, allowance, bonus, deduction) VALUES
    (1001, 4000, 2000, 2000),
    (1002, 0, 0, 500);

INSERT INTO attendance (employee_id, month, year, working_days, present_days, leave_days) VALUES
    (1001, 6, 2026, 26, 24, 2),
    (1001, 7, 2026, 27, 26, 1),
    (1002, 7, 2026, 26, 25, 1);
