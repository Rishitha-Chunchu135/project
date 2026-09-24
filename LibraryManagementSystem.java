import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Model class for Books
class Book {
    private int id;
    private String title;
    private String author;
    private boolean isIssued;

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isIssued = false;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isIssued() { return isIssued; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setIssued(boolean issued) { isIssued = issued; }

    @Override
    public String toString() {
        return "ID: " + id + " | Title: " + title + " | Author: " + author + " | Status: " + (isIssued ? "Issued" : "Available");
    }
}

// Model class for Users
class User {
    private int userId;
    private String name;

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "User ID: " + userId + " | Name: " + name;
    }
}

// Model class for Issue Transactions
class Transaction {
    private int transactionId;
    private int bookId;
    private int userId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;

    public Transaction(int transactionId, int bookId, int userId, LocalDate issueDate, int daysAllowed) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.userId = userId;
        this.issueDate = issueDate;
        this.dueDate = issueDate.plusDays(daysAllowed);
        this.returnDate = null;
        this.fine = 0.0;
    }

    public int getTransactionId() { return transactionId; }
    public int getBookId() { return bookId; }
    public int getUserId() { return userId; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        calculateFine();
    }

    // $1 fine per day overdue
    private void calculateFine() {
        if (returnDate != null && returnDate.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
            this.fine = daysLate * 1.0; 
        } else {
            this.fine = 0.0;
        }
    }

    public double getFine() { return fine; }

    @Override
    public String toString() {
        return "Tx ID: " + transactionId + " | Book ID: " + bookId + " | User ID: " + userId +
               " | Issued: " + issueDate + " | Due: " + dueDate + 
               " | Returned: " + (returnDate != null ? returnDate : "Not Returned") + 
               " | Fine: $" + fine;
    }
}

// Core Management System
public class LibraryManagementSystem {
    private static List<Book> books = new ArrayList<>();
    private static List<User> users = new ArrayList<>();
    private static List<Transaction> transactions = new ArrayList<>();
    
    private static int bookIdCounter = 1;
    private static int userIdCounter = 1;
    private static int transactionIdCounter = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Pre-populating sample data
        books.add(new Book(bookIdCounter++, "The Great Gatsby", "F. Scott Fitzgerald"));
        books.add(new Book(bookIdCounter++, "To Kill a Mockingbird", "Harper Lee"));
        users.add(new User(userIdCounter++, "Alice"));
        users.add(new User(userIdCounter++, "Bob"));

        while (true) {
            System.out.println("\n========== LIBRARY MANAGEMENT SYSTEM ==========");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Update Book");
            System.out.println("4. Register User");
            System.out.println("5. Issue Book");
            System.out.println("6. Return Book");
            System.out.println("7. Search Books (Title/Author)");
            System.out.println("8. Display All Books");
            System.out.println("9. Display All Users");
            System.out.println("10. Display Transactions");
            System.out.println("11. Exit");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> addBook(scanner);
                case 2 -> removeBook(scanner);
                case 3 -> updateBook(scanner);
                case 4 -> registerUser(scanner);
                case 5 -> issueBook(scanner);
                case 6 -> returnBook(scanner);
                case 7 -> searchBooks(scanner);
                case 8 -> displayBooks();
                case 9 -> displayUsers();
                case 10 -> displayTransactions();
                case 11 -> {
                    System.out.println("Exiting system. Goodbye!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private static void addBook(Scanner scanner) {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();

        books.add(new Book(bookIdCounter++, title, author));
        System.out.println("Book added successfully!");
    }

    private static void removeBook(Scanner scanner) {
        System.out.print("Enter Book ID to remove: ");
        int id = scanner.nextInt();
        
        Book bookToRemove = findBookById(id);
        if (bookToRemove != null) {
            if (bookToRemove.isIssued()) {
                System.out.println("Cannot remove book. It is currently issued to a user.");
            } else {
                books.remove(bookToRemove);
                System.out.println("Book removed successfully!");
            }
        } else {
            System.out.println("Book not found!");
        }
    }

    private static void updateBook(Scanner scanner) {
        System.out.print("Enter Book ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Book book = findBookById(id);
        if (book != null) {
            System.out.print("Enter new title (leave blank to keep current): ");
            String title = scanner.nextLine();
            if (!title.trim().isEmpty()) book.setTitle(title);

            System.out.print("Enter new author (leave blank to keep current): ");
            String author = scanner.nextLine();
            if (!author.trim().isEmpty()) book.setAuthor(author);

            System.out.println("Book updated successfully!");
        } else {
            System.out.println("Book not found!");
        }
    }

    private static void registerUser(Scanner scanner) {
        System.out.print("Enter user name: ");
        String name = scanner.nextLine();

        users.add(new User(userIdCounter++, name));
        System.out.println("User registered successfully!");
    }

    private static void issueBook(Scanner scanner) {
        System.out.print("Enter Book ID: ");
        int bookId = scanner.nextInt();
        System.out.print("Enter User ID: ");
        int userId = scanner.nextInt();

        Book book = findBookById(bookId);
        User user = findUserById(userId);

        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
        if (book.isIssued()) {
            System.out.println("Book is already issued to another user!");
            return;
        }

        book.setIssued(true);
        // Default loan period set to 14 days
        Transaction transaction = new Transaction(transactionIdCounter++, bookId, userId, LocalDate.now(), 14);
        transactions.add(transaction);

        System.out.println("Book issued successfully!");
        System.out.println("Due Date: " + transaction.getDueDate());
    }

    private static void returnBook(Scanner scanner) {
        System.out.print("Enter Book ID to return: ");
        int bookId = scanner.nextInt();

        Book book = findBookById(bookId);
        if (book == null || !book.isIssued()) {
            System.out.println("This book is either invalid or was not issued.");
            return;
        }

        Transaction transaction = null;
        for (Transaction t : transactions) {
            if (t.getBookId() == bookId && t.getReturnDate() == null) {
                transaction = t;
                break;
            }
        }

        if (transaction != null) {
            book.setIssued(false);
            
            // Set return date (Change LocalDate.now() to a future date to simulate late return testing)
            transaction.setReturnDate(LocalDate.now());
            
            System.out.println("Book returned successfully!");
            if (transaction.getFine() > 0) {
                System.out.println("Late Return Fee Due: $" + transaction.getFine());
            } else {
                System.out.println("Returned on time. No fine.");
            }
        }
    }

    private static void searchBooks(Scanner scanner) {
        System.out.print("Enter title or author to search: ");
        String query = scanner.nextLine().toLowerCase();

        boolean found = false;
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(query) || book.getAuthor().toLowerCase().contains(query)) {
                System.out.println(book);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching books found.");
        }
    }

    private static void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in library.");
            return;
        }
        System.out.println("\n--- Book List ---");
        for (Book b : books) System.out.println(b);
    }

    private static void displayUsers() {
        if (users.isEmpty()) {
            System.out.println("No users registered.");
            return;
        }
        System.out.println("\n--- Registered Users ---");
        for (User u : users) System.out.println(u);
    }

    private static void displayTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transaction history.");
            return;
        }
        System.out.println("\n--- Transaction History ---");
        for (Transaction t : transactions) System.out.println(t);
    }

    private static Book findBookById(int id) {
        for (Book b : books) {
            if (b.getId() == id) return b;
        }
        return null;
    }

    private static User findUserById(int id) {
        for (User u : users) {
            if (u.getUserId() == id) return u;
        }
        return null;
    }
}