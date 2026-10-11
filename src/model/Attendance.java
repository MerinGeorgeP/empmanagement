package model;

public class Attendance {
    public static final String[] MONTHS = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    private int attendanceId;
    private int employeeId;
    private int month;
    private int year;
    private int workingDays;
    private int presentDays;
    private int leaveDays;

    public Attendance(int attendanceId, int employeeId, int month, int year,
                      int workingDays, int presentDays, int leaveDays) {
        this.attendanceId = attendanceId;
        this.employeeId = employeeId;
        this.month = month;
        this.year = year;
        this.workingDays = workingDays;
        this.presentDays = presentDays;
        this.leaveDays = leaveDays;
    }

    public static void validate(int month, int year, int working, int present, int leave) {
        if (month < 1 || month > 12) throw new IllegalArgumentException("Month must be 1 to 12");
        if (year < 2000 || year > 2100) throw new IllegalArgumentException("Year is out of range");
        if (working <= 0) throw new IllegalArgumentException("Working days must be greater than 0");
        if (present < 0 || leave < 0) throw new IllegalArgumentException("Days cannot be negative");
        if (present + leave > working)
            throw new IllegalArgumentException("Present days plus leave days cannot exceed working days");
    }

    public double calculateAttendancePercentage() {
        return (presentDays * 100.0) / workingDays;
    }

    public static String monthName(int month) { return MONTHS[month - 1]; }

    public int getAttendanceId() { return attendanceId; }
    public int getEmployeeId() { return employeeId; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public int getWorkingDays() { return workingDays; }
    public int getPresentDays() { return presentDays; }
    public int getLeaveDays() { return leaveDays; }
}
