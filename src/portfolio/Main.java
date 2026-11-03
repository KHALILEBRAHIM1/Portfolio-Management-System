package portfolio;

import java.util.ArrayList;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Main {

    static ArrayList<String> names = new ArrayList<>();
    static ArrayList<Double> invested = new ArrayList<>();
    static ArrayList<Double> current = new ArrayList<>();

    static JFrame frame;
    static DefaultTableModel model;
    static JTable table;

    static final String FILE_NAME = "investments.txt";

    public static void main(String[] args) {
        loadData();
        SwingUtilities.invokeLater(() -> createWindow());
    }

    public static void createWindow() {

        frame = new JFrame("Portfolio Management System");
        frame.setSize(850, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel title = new JLabel(
            "PORTFOLIO MANAGEMENT SYSTEM",
            SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(new Color(25, 65, 120));

        mainPanel.add(title, BorderLayout.NORTH);

        String[] columns = {
            "ID", "Investment Name",
            "Invested Amount", "Current Value"
        };

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);

        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(
            new GridLayout(1, 5, 10, 10)
        );

        JButton addButton = new JButton("Add Investment");
        JButton viewButton = new JButton("View Investments");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton profitButton = new JButton("Profit / Loss");

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(profitButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addInvestment());
        viewButton.addActionListener(e -> refreshTable());
        updateButton.addActionListener(e -> updateInvestment());
        deleteButton.addActionListener(e -> deleteInvestment());
        profitButton.addActionListener(e -> calculateProfitLoss());

        frame.add(mainPanel);
        refreshTable();
        frame.setVisible(true);
    }

    // Add Investment
    public static void addInvestment() {

        String name = JOptionPane.showInputDialog(
            frame, "Enter investment name:"
        );

        if (name == null || name.trim().isEmpty()) return;

        try {
            double amount = Double.parseDouble(
                JOptionPane.showInputDialog(frame, "Enter invested amount:")
            );

            double value = Double.parseDouble(
                JOptionPane.showInputDialog(frame, "Enter current value:")
            );

            if (amount < 0 || value < 0) {
                JOptionPane.showMessageDialog(frame, "Amounts cannot be negative.");
                return;
            }

            names.add(name.trim().replace("|", " "));
            invested.add(amount);
            current.add(value);

            saveData();
            refreshTable();

            JOptionPane.showMessageDialog(frame, "Investment added successfully!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numbers.");
        }
    }

    // View Investments
    public static void refreshTable() {

        model.setRowCount(0);

        for (int i = 0; i < names.size(); i++) {
            model.addRow(new Object[] {
                i + 1,
                names.get(i),
                invested.get(i),
                current.get(i)
            });
        }
    }

    // Update Investment
    public static void updateInvestment() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select an investment first.");
            return;
        }

        String name = JOptionPane.showInputDialog(
            frame, "Enter new investment name:", names.get(row)
        );

        if (name == null || name.trim().isEmpty()) return;

        try {
            double amount = Double.parseDouble(
                JOptionPane.showInputDialog(
                    frame, "Enter new invested amount:", invested.get(row)
                )
            );

            double value = Double.parseDouble(
                JOptionPane.showInputDialog(
                    frame, "Enter new current value:", current.get(row)
                )
            );

            if (amount < 0 || value < 0) {
                JOptionPane.showMessageDialog(frame, "Amounts cannot be negative.");
                return;
            }

            names.set(row, name.trim().replace("|", " "));
            invested.set(row, amount);
            current.set(row, value);

            saveData();
            refreshTable();

            JOptionPane.showMessageDialog(frame, "Investment updated successfully!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numbers.");
        }
    }

    // Delete Investment
    public static void deleteInvestment() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select an investment first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            frame,
            "Are you sure you want to delete this investment?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            names.remove(row);
            invested.remove(row);
            current.remove(row);

            saveData();
            refreshTable();

            JOptionPane.showMessageDialog(frame, "Investment deleted successfully!");
        }
    }

    // Calculate Profit and Loss
    public static void calculateProfitLoss() {

        double totalInvested = 0;
        double totalCurrent = 0;

        for (int i = 0; i < names.size(); i++) {
            totalInvested += invested.get(i);
            totalCurrent += current.get(i);
        }

        double result = totalCurrent - totalInvested;

        String message = "Total Investment: " + totalInvested
            + "\nCurrent Value: " + totalCurrent;

        if (result > 0) {
            message += "\nTotal Profit: " + result;
        } else if (result < 0) {
            message += "\nTotal Loss: " + Math.abs(result);
        } else {
            message += "\nNo Profit, No Loss.";
        }

        JOptionPane.showMessageDialog(
            frame, message, "Profit / Loss Report",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Save Data
    public static void saveData() {

        try (BufferedWriter writer = Files.newBufferedWriter(
            Paths.get(FILE_NAME), StandardCharsets.UTF_8)) {

            for (int i = 0; i < names.size(); i++) {
                writer.write(
                    names.get(i) + "|" +
                    invested.get(i) + "|" +
                    current.get(i)
                );
                writer.newLine();
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                frame, "Error saving investment data!"
            );
        }
    }

    // Load Data
    public static void loadData() {

        Path path = Paths.get(FILE_NAME);

        if (!Files.exists(path)) return;

        try (BufferedReader reader = Files.newBufferedReader(
            path, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\|", -1);

                if (parts.length == 3) {
                    try {
                        names.add(parts[0]);
                        invested.add(Double.parseDouble(parts[1]));
                        current.add(Double.parseDouble(parts[2]));
                    } catch (NumberFormatException ex) {
                        // Skip invalid records
                    }
                }
            }

        } catch (IOException ex) {
            System.out.println("Error loading investment data!");
        }
    }
}