package gui;

import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public final class Ui {
    private Ui() {}

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message);
    }

    public static void error(Component parent, Exception ex) {
        String message = ex.getMessage() == null ? ex.toString() : ex.getMessage();
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static int integer(JTextField field, String label) {
        try { return Integer.parseInt(field.getText().trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException(label + " must be a whole number"); }
    }

    public static double decimal(JTextField field, String label) {
        try { return Double.parseDouble(field.getText().trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException(label + " must be a number"); }
    }

    public static JButton button(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        return button;
    }

    public static JComboBox<String> months() { return new JComboBox<>(model.Attendance.MONTHS); }

    public static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    public static void clear(DefaultTableModel model) { model.setRowCount(0); }
}
