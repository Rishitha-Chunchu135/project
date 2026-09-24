import csv
import os
from datetime import datetime
import matplotlib.pyplot as plt

FILE_NAME = "expenses.csv"

# Initialize CSV file with headers if it doesn't exist
def initialize_storage():
    if not os.path.exists(FILE_NAME):
        with open(FILE_NAME, mode='w', newline='') as file:
            writer = csv.writer(file)
            writer.writerow(["Date", "Category", "Amount", "Description"])

# Function to record daily expenses
def add_expense():
    print("\n--- Add New Expense ---")
    
    # Date validation
    date_str = input("Enter date (YYYY-MM-DD) or press Enter for today: ").strip()
    if not date_str:
        date_str = datetime.now().strftime("%Y-%m-%d")
    else:
        try:
            datetime.strptime(date_str, "%Y-%m-%d")
        except ValueError:
            print("Invalid date format! Using today's date instead.")
            date_str = datetime.now().strftime("%Y-%m-%d")

    # Category selection
    print("\nCategories: Food, Travel, Bills, Shopping, Entertainment, Others")
    category = input("Enter category: ").strip().capitalize()
    if not category:
        category = "Others"

    # Amount validation
    try:
        amount = float(input("Enter amount spent: "))
        if amount <= 0:
            print("Amount must be greater than zero.")
            return
    except ValueError:
        print("Invalid input for amount. Transaction cancelled.")
        return

    description = input("Enter a brief description: ").strip()

    # Save to CSV
    with open(FILE_NAME, mode='a', newline='') as file:
        writer = csv.writer(file)
        writer.writerow([date_str, category, f"{amount:.2f}", description])

    print("\nExpense recorded successfully!")

# Function to view all expenses
def view_expenses():
    if not os.path.exists(FILE_NAME):
        print("\nNo expense records found.")
        return

    print("\n--- All Recorded Expenses ---")
    print(f"{'Date':<12} | {'Category':<15} | {'Amount':<10} | {'Description'}")
    print("-" * 55)

    with open(FILE_NAME, mode='r') as file:
        reader = csv.reader(file)
        next(reader)  # Skip header
        for row in reader:
            if row:
                print(f"{row[0]:<12} | {row[1]:<15} | ${float(row[2]):<9.2f} | {row[3]}")

# Function to generate monthly summaries, detect highest spending, and render charts
def generate_insights():
    if not os.path.exists(FILE_NAME):
        print("\nNo data available to generate insights.")
        return

    month_input = input("\nEnter month to analyze (MM) e.g., '03' or '10': ").strip()
    year_input = input("Enter year (YYYY) e.g., '2026': ").strip()

    category_totals = {}
    total_spent = 0.0

    with open(FILE_NAME, mode='r') as file:
        reader = csv.reader(file)
        next(reader)  # Skip header
        for row in reader:
            if row:
                exp_date, category, amount, _ = row
                exp_year, exp_month, _ = exp_date.split('-')

                if exp_month == month_input.zfill(2) and exp_year == year_input:
                    amount_val = float(amount)
                    total_spent += amount_val
                    category_totals[category] = category_totals.get(category, 0.0) + amount_val

    if not category_totals:
        print(f"\nNo expenses found for {month_input}/{year_input}.")
        return

    print(f"\n==========================================")
    print(f"       SUMMARY FOR {month_input}/{year_input}")
    print(f"==========================================")
    print(f"Total Money Spent: ${total_spent:.2f}\n")
    print("Category-wise Breakdown:")
    for cat, amt in category_totals.items():
        percentage = (amt / total_spent) * 100
        print(f" - {cat:<12}: ${amt:.2f} ({percentage:.1f}%)")

    # Detect highest spending category
    highest_category = max(category_totals, key=category_totals.get)
    print(f"\nInsight: Your highest spending category was '{highest_category}' with ${category_totals[highest_category]:.2f}.")
    print(f"Suggestion: Consider setting a budget for '{highest_category}' to reduce unnecessary spending.")

    # Generate Pie Chart using Matplotlib
    show_chart = input("\nWould you like to display the category pie chart? (y/n): ").lower()
    if show_chart == 'y':
        labels = list(category_totals.keys())
        sizes = list(category_totals.values())

        plt.figure(figsize=(7, 7))
        plt.pie(sizes, labels=labels, autopct='%1.1f%%', startangle=140)
        plt.title(f"Expense Breakdown - {month_input}/{year_input}")
        plt.axis('equal')
        plt.show()

# Main Menu loop
def main():
    initialize_storage()
    while True:
        print("\n==============================")
        print("  SMART EXPENSE TRACKER CLI   ")
        print("==============================")
        print("1. Add Daily Expense")
        print("2. View All Expenses")
        print("3. Generate Monthly Insights & Chart")
        print("4. Exit")

        choice = input("Select an option (1-4): ").strip()

        if choice == '1':
            add_expense()
        elif choice == '2':
            view_expenses()
        elif choice == '3':
            generate_insights()
        elif choice == '4':
            print("\nThank you for using Smart Expense Tracker. Goodbye!")
            break
        else:
            print("\nInvalid choice. Please enter a number between 1 and 4.")

if __name__ == "__main__":
    main()