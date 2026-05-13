# Finance Tracker

A personal finance tracker application built with Java Swing. This application helps users track their income and expenses, view breakdowns via charts, and manage their financial profile.

## Features

### Existing Features
- **Income Tracking**: Add and delete income records.
- **Expense Tracking**: Add and delete expense records.
- **Visual Breakdown**: Pie charts showing income and expense categories (powered by JFreeChart).
- **Profile Management**: Save user details such as name, email, and target savings.
- **Persistence**: Data is saved to CSV files (`income.csv`, `expenses.csv`) and a text file (`profile.txt`).

### New & Improved Features
- **Table Sorting**: Click on table headers in Income and Expenses tabs to sort data.
- **Date Validation**: Enforces `YYYY-MM-DD` format for date inputs to prevent errors.
- **Budget Tracking**:
    - Set a Monthly Budget in the Profile tab.
    - View remaining budget on the Home tab.
    - Visual warning (Red text) when the budget is exceeded.
- **Search/Filter**: Search bar added to Income and Expenses tabs to filter transactions by keyword.

## Technologies Used
- Java (Swing for GUI)
- JFreeChart (for rendering charts)

## How to Run

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

## Project Structure
- `src/`: Contains the Java source code (`FinanceDashboard.java`).
- `lib/`: Contains external JAR files for JFreeChart.
- `out/`: Compiled class files.
- `income.csv`: Saved income transactions.
- `expenses.csv`: Saved expense transactions.
- `profile.txt`: Saved user profile data.
