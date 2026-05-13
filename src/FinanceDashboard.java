import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.*;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FinanceDashboard {
    private JFrame frame;
    private JPanel contentPanel;
    private JLabel incomeLabel, expenseLabel, balanceLabel, budgetLabel;
    private JTable incomeTable, expenseTable;
    private DefaultTableModel incomeTableModel, expenseTableModel;

    private Profile profile;

    private List<Transaction> incomeList = new ArrayList<>();
    private List<Transaction> expenseList = new ArrayList<>();

    private static final String INCOME_FILE = "income.csv";
    private static final String EXPENSE_FILE = "expenses.csv";
    private static final String PROFILE_FILE = "profile.txt";

    // Color Palette
    private static final Color PRIMARY_COLOR = new Color(30, 41, 59); // Slate 800
    private static final Color SECONDARY_COLOR = new Color(71, 85, 105); // Slate 600
    private static final Color ACCENT_COLOR = new Color(37, 99, 235); // Blue 600
    private static final Color BG_COLOR = new Color(248, 250, 252); // Slate 50
    private static final Color CARD_BG_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(15, 23, 42); // Slate 900
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129); // Emerald 500
    private static final Color DANGER_COLOR = new Color(239, 68, 68); // Rose 500

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(FinanceDashboard::new);
    }

    public FinanceDashboard() {
        loadProfile();
        loadTransactions();

        frame = new JFrame("💼 Personal Finance Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700);
        frame.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(PRIMARY_COLOR);
        JLabel title = new JLabel("💼 Personal Finance Tracker");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title);
        frame.add(header, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(4, 1, 5, 5));
        sidebar.setBackground(PRIMARY_COLOR);
        sidebar.setPreferredSize(new Dimension(180, 0));

        JButton homeButton = createSidebarButton("🏠 Home");
        JButton incomeButton = createSidebarButton("💰 Income");
        JButton expensesButton = createSidebarButton("💸 Expenses");
        JButton profileButton = createSidebarButton("👤 Profile");

        sidebar.add(homeButton);
        sidebar.add(incomeButton);
        sidebar.add(expensesButton);
        sidebar.add(profileButton);

        frame.add(sidebar, BorderLayout.WEST);

        // Content panel
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Color.WHITE);
        frame.add(contentPanel, BorderLayout.CENTER);

        initIncomePanel();
        initExpensesPanel();
        initHomePanel();
        initProfilePanel();

        refreshTables();
        updateTotals();

        homeButton.addActionListener(e -> showPanel("Home"));
        incomeButton.addActionListener(e -> showPanel("Income"));
        expensesButton.addActionListener(e -> showPanel("Expenses"));
        profileButton.addActionListener(e -> showPanel("Profile"));

        frame.setVisible(true);
        showPanel("Home");
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(SECONDARY_COLOR);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(SECONDARY_COLOR);
            }
        });
        return btn;
    }

    private void initHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JPanel stats = new JPanel();
        stats.setBackground(BG_COLOR);
        stats.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));

        incomeLabel = new JLabel("₹0.00");
        expenseLabel = new JLabel("₹0.00");
        balanceLabel = new JLabel("₹0.00");
        budgetLabel = new JLabel("₹0.00");

        stats.add(createCard("Total Income", incomeLabel, CARD_BG_COLOR, SUCCESS_COLOR));
        stats.add(createCard("Total Expenses", expenseLabel, CARD_BG_COLOR, DANGER_COLOR));
        stats.add(createCard("Net Balance", balanceLabel, CARD_BG_COLOR, ACCENT_COLOR));
        stats.add(createCard("Remaining Budget", budgetLabel, CARD_BG_COLOR, TEXT_COLOR));
        panel.add(stats, BorderLayout.NORTH);

        JPanel charts = new JPanel(new GridLayout(1, 2, 15, 15));
        charts.setBackground(BG_COLOR);
        charts.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        charts.add(createChartPanel(incomeList, "Income"));
        charts.add(createChartPanel(expenseList, "Expenses"));
        panel.add(charts, BorderLayout.CENTER);

        contentPanel.add(panel, "Home");
    }

    private JPanel createCard(String title, JLabel valueLabel, Color bgColor, Color textColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setPreferredSize(new Dimension(220, 100));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(SECONDARY_COLOR);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(textColor);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_COLOR);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(226, 232, 240));
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleButton(JButton btn, Color bgColor, Color fgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void initIncomePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        incomeTableModel = new DefaultTableModel(new Object[]{"Date", "Category", "Amount"}, 0);
        incomeTable = new JTable(incomeTableModel);
        incomeTable.setAutoCreateRowSorter(true);
        styleTable(incomeTable);
        JScrollPane scroll = new JScrollPane(incomeTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel top = new JPanel();
        JTextField searchField = new JTextField(15);
        styleTextField(searchField);
        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn, ACCENT_COLOR, Color.WHITE);
        top.add(new JLabel("Search:"));
        top.add(searchField);
        top.add(searchBtn);
        panel.add(top, BorderLayout.NORTH);

        searchBtn.addActionListener(e -> {
            String text = searchField.getText();
            TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) incomeTable.getRowSorter();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JPanel bottom = new JPanel();

        JTextField dateField = new JTextField(8);
        styleTextField(dateField);
        JTextField categoryField = new JTextField(8);
        styleTextField(categoryField);
        JTextField amountField = new JTextField(6);
        styleTextField(amountField);

        JButton addBtn = new JButton("Add Income");
        styleButton(addBtn, SUCCESS_COLOR, Color.WHITE);
        JButton deleteBtn = new JButton("Delete Selected");
        styleButton(deleteBtn, DANGER_COLOR, Color.WHITE);
        JButton chartBtn = new JButton("Show Chart");
        styleButton(chartBtn, SECONDARY_COLOR, Color.WHITE);

        bottom.add(new JLabel("Date (YYYY-MM-DD):"));
        bottom.add(dateField);
        bottom.add(new JLabel("Category:"));
        bottom.add(categoryField);
        bottom.add(new JLabel("Amount:"));
        bottom.add(amountField);
        bottom.add(addBtn);
        bottom.add(deleteBtn);
        bottom.add(chartBtn);

        addBtn.addActionListener(e -> {
            String date = dateField.getText();
            String category = categoryField.getText();
            if (!isValidDate(date)) {
                JOptionPane.showMessageDialog(frame, "Invalid date format. Use YYYY-MM-DD.");
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount.");
                return;
            }
            Transaction t = new Transaction(date, category, amount);
            incomeList.add(t);
            saveTransactions();
            refreshTables();
            updateTotals();
            dateField.setText("");
            categoryField.setText("");
            amountField.setText("");
        });

        deleteBtn.addActionListener(e -> {
            int row = incomeTable.getSelectedRow();
            if (row >= 0) {
                incomeList.remove(row);
                saveTransactions();
                refreshTables();
                updateTotals();
            }
        });

        chartBtn.addActionListener(e -> showChart(incomeList, "Income Breakdown"));

        panel.add(bottom, BorderLayout.SOUTH);
        contentPanel.add(panel, "Income");
    }

    private void initExpensesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        expenseTableModel = new DefaultTableModel(new Object[]{"Date", "Category", "Amount"}, 0);
        expenseTable = new JTable(expenseTableModel);
        expenseTable.setAutoCreateRowSorter(true);
        styleTable(expenseTable);
        JScrollPane scroll = new JScrollPane(expenseTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel top = new JPanel();
        JTextField searchField = new JTextField(15);
        styleTextField(searchField);
        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn, ACCENT_COLOR, Color.WHITE);
        top.add(new JLabel("Search:"));
        top.add(searchField);
        top.add(searchBtn);
        panel.add(top, BorderLayout.NORTH);

        searchBtn.addActionListener(e -> {
            String text = searchField.getText();
            TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) expenseTable.getRowSorter();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JPanel bottom = new JPanel();

        JTextField dateField = new JTextField(8);
        styleTextField(dateField);
        JTextField categoryField = new JTextField(8);
        styleTextField(categoryField);
        JTextField amountField = new JTextField(6);
        styleTextField(amountField);

        JButton addBtn = new JButton("Add Expense");
        styleButton(addBtn, DANGER_COLOR, Color.WHITE);
        JButton deleteBtn = new JButton("Delete Selected");
        styleButton(deleteBtn, DANGER_COLOR, Color.WHITE);
        JButton chartBtn = new JButton("Show Chart");
        styleButton(chartBtn, SECONDARY_COLOR, Color.WHITE);

        bottom.add(new JLabel("Date (YYYY-MM-DD):"));
        bottom.add(dateField);
        bottom.add(new JLabel("Category:"));
        bottom.add(categoryField);
        bottom.add(new JLabel("Amount:"));
        bottom.add(amountField);
        bottom.add(addBtn);
        bottom.add(deleteBtn);
        bottom.add(chartBtn);

        addBtn.addActionListener(e -> {
            String date = dateField.getText();
            String category = categoryField.getText();
            if (!isValidDate(date)) {
                JOptionPane.showMessageDialog(frame, "Invalid date format. Use YYYY-MM-DD.");
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount.");
                return;
            }
            Transaction t = new Transaction(date, category, amount);
            expenseList.add(t);
            saveTransactions();
            refreshTables();
            updateTotals();
            dateField.setText("");
            categoryField.setText("");
            amountField.setText("");
        });

        deleteBtn.addActionListener(e -> {
            int row = expenseTable.getSelectedRow();
            if (row >= 0) {
                expenseList.remove(row);
                saveTransactions();
                refreshTables();
                updateTotals();
            }
        });

        chartBtn.addActionListener(e -> showChart(expenseList, "Expense Breakdown"));

        panel.add(bottom, BorderLayout.SOUTH);
        contentPanel.add(panel, "Expenses");
    }

    private JPanel createChartPanel(List<Transaction> data, String title) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Double> grouped = new HashMap<>();
        for (Transaction t : data) {
            grouped.put(t.category, grouped.getOrDefault(t.category, 0.0) + t.amount);
        }
        grouped.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                title + " (All Time)", dataset, true, true, false);
        return new ChartPanel(chart);
    }

    private void showChart(List<Transaction> data, String title) {
        JFrame f = new JFrame(title);
        f.setContentPane(createChartPanel(data, title));
        f.setSize(500, 400);
        f.setVisible(true);
    }

    private void showPanel(String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
        refreshTables();
        updateTotals();
    }

    private void refreshTables() {
        incomeTableModel.setRowCount(0);
        for (Transaction t : incomeList)
            incomeTableModel.addRow(new Object[]{t.date, t.category, t.amount});
        expenseTableModel.setRowCount(0);
        for (Transaction t : expenseList)
            expenseTableModel.addRow(new Object[]{t.date, t.category, t.amount});
    }

    private void updateTotals() {
        double income = incomeList.stream().mapToDouble(t -> t.amount).sum();
        double expense = expenseList.stream().mapToDouble(t -> t.amount).sum();
        double balance = income - expense;
        incomeLabel.setText("₹" + String.format("%.2f", income));
        expenseLabel.setText("₹" + String.format("%.2f", expense));
        balanceLabel.setText("₹" + String.format("%.2f", balance));
        
        double remainingBudget = profile.monthlyBudget - expense;
        budgetLabel.setText("₹" + String.format("%.2f", remainingBudget));
        if (remainingBudget < 0) {
            budgetLabel.setForeground(DANGER_COLOR);
        } else {
            budgetLabel.setForeground(TEXT_COLOR);
        }
    }

    private void saveTransactions() {
        saveListToCSV(INCOME_FILE, incomeList);
        saveListToCSV(EXPENSE_FILE, expenseList);
    }

    private void saveListToCSV(String file, List<Transaction> list) {
        try (PrintWriter pw = new PrintWriter(new File(file))) {
            for (Transaction t : list)
                pw.println(t.date + "," + t.category + "," + t.amount);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadTransactions() {
        incomeList = loadListFromCSV(INCOME_FILE);
        expenseList = loadListFromCSV(EXPENSE_FILE);
    }

    private List<Transaction> loadListFromCSV(String file) {
        List<Transaction> list = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            for (String line : lines) {
                String[] parts = line.split(",");
                list.add(new Transaction(parts[0], parts[1], Double.parseDouble(parts[2])));
            }
        } catch (Exception ignored) {}
        return list;
    }

    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private void initProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Add the heading
        JLabel heading = new JLabel("User Details");
        heading.setFont(new Font("SansSerif", Font.BOLD, 20));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        panel.add(heading, gbc);

        // Reset gridwidth for fields
        gbc.gridwidth = 1;
        gbc.weightx = 0;

        JTextField nameField = new JTextField(profile.name);
        JTextField emailField = new JTextField(profile.email);
        JTextField phoneField = new JTextField(profile.phone);
        JTextField addressField = new JTextField(profile.address);
        JTextField avgIncomeField = new JTextField(String.valueOf(profile.avgIncome));
        JTextField avgExpenseField = new JTextField(String.valueOf(profile.avgExpense));
        JTextField targetSavingsField = new JTextField(String.valueOf(profile.targetSavings));
        JTextField monthlyBudgetField = new JTextField(String.valueOf(profile.monthlyBudget));

        String[] labels = {
                "Name:", "Email:", "Phone:", "Address:",
                "Avg Income:", "Avg Expenses:", "Target Savings:", "Monthly Budget:"
        };
        JTextField[] fields = {
                nameField, emailField, phoneField, addressField,
                avgIncomeField, avgExpenseField, targetSavingsField, monthlyBudgetField
        };

        for (JTextField f : fields) {
            styleTextField(f);
        }

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i+1;
            gbc.weightx = 0.3;
            panel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            panel.add(fields[i], gbc);
        }

        JButton saveBtn = new JButton("Save Profile");
        styleButton(saveBtn, ACCENT_COLOR, Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = labels.length+1;
        gbc.gridwidth = 2;
        panel.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            profile.name = nameField.getText();
            profile.email = emailField.getText();
            profile.phone = phoneField.getText();
            profile.address = addressField.getText();
            try {
                profile.avgIncome = Double.parseDouble(avgIncomeField.getText());
                profile.avgExpense = Double.parseDouble(avgExpenseField.getText());
                profile.targetSavings = Double.parseDouble(targetSavingsField.getText());
                profile.monthlyBudget = Double.parseDouble(monthlyBudgetField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid numeric input");
                return;
            }
            saveProfile();
            JOptionPane.showMessageDialog(frame, "Profile saved successfully!");
        });

        contentPanel.add(panel, "Profile");
    }



    private void saveProfile() {
        try (PrintWriter pw = new PrintWriter(new File(PROFILE_FILE))) {
            pw.println(profile.name);
            pw.println(profile.email);
            pw.println(profile.phone);
            pw.println(profile.address);
            pw.println(profile.avgIncome);
            pw.println(profile.avgExpense);
            pw.println(profile.targetSavings);
            pw.println(profile.monthlyBudget);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadProfile() {
        profile = new Profile();
        try {
            List<String> lines = Files.readAllLines(Paths.get(PROFILE_FILE));
            profile.name = lines.get(0);
            profile.email = lines.get(1);
            profile.phone = lines.get(2);
            profile.address = lines.get(3);
            profile.avgIncome = Double.parseDouble(lines.get(4));
            profile.avgExpense = Double.parseDouble(lines.get(5));
            profile.targetSavings = Double.parseDouble(lines.get(6));
            profile.monthlyBudget = lines.size() > 7 ? Double.parseDouble(lines.get(7)) : 0;
        } catch (Exception ignored) {}
    }

    static class Transaction {
        String date, category;
        double amount;

        Transaction(String d, String c, double a) {
            date = d;
            category = c;
            amount = a;
        }
    }

    static class Profile {
        String name = "", email = "", phone = "", address = "";
        double avgIncome = 0, avgExpense = 0, targetSavings = 0, monthlyBudget = 0;
    }
}
