# Employee Payroll Management

Java Swing, JDBC, and MySQL. No Spring, Hibernate, or REST.

1. Install JDK 17+ and MySQL. If `root` has a password, set `PASSWORD` in `src/database/DatabaseConnection.java`.
2. Load the schema: `mysql -u root -p < database/payroll.sql`
3. Download the driver:

```bash
mkdir -p lib && curl -L -o lib/mysql-connector-j.jar \
  https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar
```

4. Compile and run:

```bash
javac -cp "lib/mysql-connector-j.jar" -d out $(find src -name '*.java')
java -cp "out:lib/mysql-connector-j.jar" Main
```

Permanent gross = basic + 10% allowance + 5% bonus, minus a 5% deduction. Intern gross = stipend, minus 500. Attendance below 75% removes the bonus, or adds 10% of the stipend for an intern.
# empmanagement
