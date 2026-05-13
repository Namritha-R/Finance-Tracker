# 💼 Finance Tracker

A Java Swing-based desktop application for managing personal income and expenses with data visualization charts.

---

## ✨ Features

✅ **Dark-Themed Sidebar & Modern Layout**  
A modern and intuitive layout with a dark sidebar and light content panels.

✅ **Income & Expense Tracking**  
- Add, delete, and view transactions.
- Automatically calculate total income, expenses, and balance.

✅ **Visual Breakdown Charts**  
Visualize spending and earning patterns with pie charts (powered by JFreeChart).

✅ **Profile Management**  
Store and update user details and financial targets.

### 🚀 New & Improved Features
- **Table Sorting**: Click on table headers in Income and Expenses tabs to sort data.
- **Date Validation**: Enforces `YYYY-MM-DD` format for date inputs to prevent errors.
- **Budget Tracking**:
    - Set a Monthly Budget in the Profile tab.
    - View remaining budget on the Home tab.
    - Visual warning (Red text) when the budget is exceeded.
- **Search/Filter**: Search bar added to Income and Expenses tabs to filter transactions by keyword.

---

## 🛠️ Technologies Used

- **Java 8+**
- **Swing** – for GUI components
- **JFreeChart** – for charting

---

## 🚀 How to Run

### Prerequisites
- Java Development Kit (JDK) installed.
- External libraries (JFreeChart) are included in the `lib` directory.

### Running the Application

1. Open a terminal/command prompt in the project root directory.
2. Compile the project:
   ```bash
   javac -cp ".;lib/*" src/FinanceDashboard.java -d out
   ```
   *(Note: On Linux/Mac, use colon `:` instead of semicolon `;` for the classpath separator)*
3. Run the application:
   ```bash
   java -cp ".;lib/*;out" FinanceDashboard
   ```

---

## 📁 Project Structure
- `src/`: Contains the Java source code (`FinanceDashboard.java`).
- `lib/`: Contains external JAR files for JFreeChart.
- `out/`: Compiled class files.
- `income.csv`: Saved income transactions.
- `expenses.csv`: Saved expense transactions.
- `profile.txt`: Saved user profile data.
