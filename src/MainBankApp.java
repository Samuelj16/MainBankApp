/*
 * ============================================================================
 *  BankApp: Single-Flow Console Application  (Java)
 * ============================================================================
 *  FLOW:  main() -> welcome() -> login() -> adminDashboard() OR customerDashboard()
 *
 *  2 stakeholders : Admin, Customer
 *  2 menus        : AdminMenu, CustomerMenu
 *  CRUD ops       : Add Customer, Update, Delete, Add Account, View All Accounts
 *                   (+ Deposit / Withdraw / Add Interest)
 *
 *  THE 4 OOP PILLARS, AND WHERE TO POINT IF ASKED:
 *    1. Encapsulation -> private fields in Customer/Account + public getters/setters.
 *                        Outside code can't touch the data directly, only through methods.
 *    2. Inheritance   -> Customer & Admin extend User; Savings & Checking extend Account.
 *                        Subclasses reuse the parent's fields/constructor via super(...).
 *    3. Abstraction   -> Account is `abstract` with an abstract addInterest().
 *                        You can't do `new Account()`; it's just a shared blueprint.
 *    4. Polymorphism  -> a.addInterest() runs the RIGHT version (2% vs 3%) based on the
 *                        real object type at runtime, even though `a` is typed as Account.
 *
 *  Also demonstrates: loops (while), switch control flow, try/catch input handling.
 *
 *  NOTE: In Java the public class name must match the file name. Original code said
 *        `public class Main` but the file is MainBankApp.java -> wouldn't compile.
 *        So the public class is renamed to MainBankApp.
 * ============================================================================
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainBankApp {

    // ONE Scanner for the whole program. Making more than one on System.in causes bugs.
    static Scanner sc = new Scanner(System.in);

    // Auto-incrementing IDs so we never hand out the same id/account number twice.
    static int customerIdCounter = 1;      // first customer becomes id 1
    static int accountNumberCounter = 1001; // first account becomes #1001

    // Our in-memory "database". No real DB/file -> everything resets when the app closes.
    static List<Customer> customers = new ArrayList<>();

    /*
     * STATIC INITIALIZER BLOCK: runs once when the class loads, before main().
     * We use it to seed test data so you can log in immediately without registering.
     *   Admin login:    admin / admin123   (hardcoded in login(), not stored here)
     *   Customer logins: rohit/rohit123, mohit/mohit123, shobhit/shobhit123
     */
    static {
        Customer c1 = new Customer(customerIdCounter++, "rohit", new ArrayList<>(), "rohit", "rohit123");
        Customer c2 = new Customer(customerIdCounter++, "mohit", new ArrayList<>(), "mohit", "mohit123");
        Customer c3 = new Customer(customerIdCounter++, "shobhit", new ArrayList<>(), "shobhit", "shobhit123");

        // Give a couple of them a starter account so the "view" options show something.
        c1.getAccounts().add(new SavingsAccount(accountNumberCounter++, 5000));
        c2.getAccounts().add(new CheckingAccount(accountNumberCounter++, 1200));

        customers.add(c1);
        customers.add(c2);
        customers.add(c3);
    }

    // Program entry point. Keeps the high-level flow easy to read.
    public static void main(String[] args) {
        welcome();
        String loginResult = login(); // returns "admin", a username, or "validation_failed"

        if (loginResult.equals("validation_failed")) {
            System.out.println("Validation failed. Wrong username or password.");
            return; // stop the program
        }

        // Route to the correct dashboard based on who logged in.
        if (loginResult.equals("admin")) {
            adminDashboard();
        } else {
            customerDashboard(loginResult);
        }
    }

    private static void welcome() {
        System.out.println("=======================================");
        System.out.println("       Welcome to ABC Digital Bank");
        System.out.println("=======================================");
    }

    /*
     * Reads "username password" on one line and figures out who it is.
     * Returns: "admin" | the customer's username | "validation_failed"
     */
    private static String login() {
        System.out.println("Please enter username and password, space separated:");
        String entered = sc.nextLine().trim();

        // split on any run of whitespace. GUARD: if there's no space we'd only get 1 piece,
        // and reading parts[1] would throw ArrayIndexOutOfBoundsException (your original bug).
        String[] parts = entered.split("\\s+");
        if (parts.length < 2) {
            return "validation_failed";
        }

        String username = parts[0];
        String password = parts[1];

        // Admin is a hardcoded special case.
        if (username.equals("admin") && password.equals("admin123")) {
            return "admin";
        }

        // Otherwise loop the customer list and check credentials.
        for (Customer c : customers) {
            if (c.getUsername().equals(username) && c.getPassword().equals(password)) {
                return c.getUsername();
            }
        }
        return "validation_failed";
    }

    // ============================ ADMIN ============================

    /*
     * Admin menu. The `while(true)` keeps showing the menu until the admin picks Logout,
     * which `return`s out of the method. The switch routes each number to a helper method
     * (cleaner than a giant if/else-if chain).
     */
    private static void adminDashboard() {
        System.out.println("Welcome, Admin");
        while (true) {
            System.out.println();
            System.out.println("------ ADMIN MENU ------");
            System.out.println("1) View all customers");
            System.out.println("2) View all accounts");
            System.out.println("3) Add customer");
            System.out.println("4) Update customer");
            System.out.println("5) Delete customer");
            System.out.println("6) Add account to a customer");
            System.out.println("7) Delete an account");
            System.out.println("8) Logout");
            System.out.print("Choose an option: ");

            int choice = readInt();
            switch (choice) {
                case 1: viewAllCustomers();          break;
                case 2: viewAllAccounts();           break;
                case 3: addCustomer();               break;
                case 4: updateCustomer();            break;
                case 5: deleteCustomer();            break;
                case 6: addAccount();                break;
                case 7: deleteAccount();             break;
                case 8: System.out.println("Logging out..."); return; // exits the loop+method
                default: System.out.println("Invalid option, try again.");
            }
        }
    }

    // READ: print every customer (relies on Customer.toString()).
    private static void viewAllCustomers() {
        if (customers.isEmpty()) {
            System.out.println("No customers yet.");
            return;
        }
        for (Customer c : customers) {
            System.out.println(c);
        }
    }

    // READ: nested loop -> each customer, then each of their accounts.
    private static void viewAllAccounts() {
        boolean found = false;
        for (Customer c : customers) {
            for (Account a : c.getAccounts()) {
                System.out.println(c.getName() + " -> " + a); // uses Account.toString()
                found = true;
            }
        }
        if (!found) System.out.println("No accounts exist yet.");
    }

    // CREATE: build a new Customer and add it to the list.
    private static void addCustomer() {
        System.out.print("Enter name: ");
        String name = sc.nextLine().trim();
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        Customer c = new Customer(customerIdCounter++, name, new ArrayList<>(), username, password);
        customers.add(c);
        System.out.println("Customer added with id " + c.getId());
    }

    // UPDATE: find by id, then change the name (blank input = keep old name).
    private static void updateCustomer() {
        System.out.print("Enter customer id to update: ");
        int id = readInt();
        Customer c = findCustomerById(id);
        if (c == null) { System.out.println("No customer with id " + id); return; }

        System.out.print("New name (leave blank to keep '" + c.getName() + "'): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) c.setName(name); // setter = encapsulation in action
        System.out.println("Updated: " + c);
    }

    // DELETE: find by id, then remove from the list.
    private static void deleteCustomer() {
        System.out.print("Enter customer id to delete: ");
        int id = readInt();
        Customer c = findCustomerById(id);
        if (c == null) { System.out.println("No customer with id " + id); return; }
        customers.remove(c);
        System.out.println("Deleted customer " + c.getName());
    }

    /*
     * CREATE (account): pick a customer, choose a type, set a balance.
     * NOTE the polymorphism setup: `Account a` can hold EITHER a SavingsAccount or a
     * CheckingAccount. The variable type is the parent; the real object is the subclass.
     */
    private static void addAccount() {
        System.out.print("Enter customer id to add an account to: ");
        int id = readInt();
        Customer c = findCustomerById(id);
        if (c == null) { System.out.println("No customer with id " + id); return; }

        System.out.print("Account type - 1) Savings  2) Checking: ");
        int type = readInt();
        System.out.print("Initial balance: ");
        double balance = readDouble();

        Account a;
        if (type == 1) {
            a = new SavingsAccount(accountNumberCounter++, balance);
        } else if (type == 2) {
            a = new CheckingAccount(accountNumberCounter++, balance);
        } else {
            System.out.println("Invalid account type.");
            return;
        }
        c.getAccounts().add(a);
        System.out.println("Account created: " + a);
    }

    // DELETE (account): search every customer for the matching account number, then remove it.
    private static void deleteAccount() {
        System.out.print("Enter account number to delete: ");
        int accNo = readInt();
        for (Customer c : customers) {
            Account target = null;
            for (Account a : c.getAccounts()) {
                if (a.getAccountNumber() == accNo) { target = a; break; }
            }
            // Remove AFTER the inner loop -> avoids modifying the list while iterating it.
            if (target != null) {
                c.getAccounts().remove(target);
                System.out.println("Deleted account " + accNo + " from " + c.getName());
                return;
            }
        }
        System.out.println("No account found with number " + accNo);
    }

    // ========================== CUSTOMER ==========================

    /*
     * Customer menu. We first look up THIS customer's object so every action below
     * operates only on their own accounts.
     */
    private static void customerDashboard(String username) {
        Customer me = findCustomerByUsername(username);
        if (me == null) { System.out.println("Could not load your profile."); return; }
        System.out.println("Welcome, " + me.getName() + "!");

        while (true) {
            System.out.println();
            System.out.println("------ CUSTOMER MENU ------");
            System.out.println("1) View my accounts");
            System.out.println("2) View a balance");
            System.out.println("3) Deposit");
            System.out.println("4) Withdraw");
            System.out.println("5) Add interest to an account");
            System.out.println("6) Logout");
            System.out.print("Choose an option: ");

            int choice = readInt();
            switch (choice) {
                case 1: viewMyAccounts(me);  break;
                case 2: viewBalance(me);     break;
                case 3: deposit(me);         break;
                case 4: withdraw(me);        break;
                case 5: applyInterest(me);   break;
                case 6: System.out.println("Logging out..."); return;
                default: System.out.println("Invalid option, try again.");
            }
        }
    }

    private static void viewMyAccounts(Customer me) {
        if (me.getAccounts().isEmpty()) {
            System.out.println("You have no accounts yet. Ask an admin to add one.");
            return;
        }
        for (Account a : me.getAccounts()) System.out.println(a);
    }

    private static void viewBalance(Customer me) {
        Account a = pickAccount(me);
        if (a != null) {
            // %.2f = 2 decimal places, %n = platform-correct newline
            System.out.printf("Balance for account %d: $%.2f%n", a.getAccountNumber(), a.getBalance());
        }
    }

    private static void deposit(Customer me) {
        Account a = pickAccount(me);
        if (a == null) return;
        System.out.print("Amount to deposit: ");
        double amt = readDouble();
        if (amt <= 0) { System.out.println("Amount must be positive."); return; }
        a.setBalance(a.getBalance() + amt);
        System.out.printf("Deposited $%.2f. New balance: $%.2f%n", amt, a.getBalance());
    }

    private static void withdraw(Customer me) {
        Account a = pickAccount(me);
        if (a == null) return;
        System.out.print("Amount to withdraw: ");
        double amt = readDouble();
        if (amt <= 0) { System.out.println("Amount must be positive."); return; }
        if (amt > a.getBalance()) { System.out.println("Insufficient funds."); return; }
        a.setBalance(a.getBalance() - amt);
        System.out.printf("Withdrew $%.2f. New balance: $%.2f%n", amt, a.getBalance());
    }

    /*
     * POLYMORPHISM HIGHLIGHT: we call a.addInterest() without knowing or caring whether
     * `a` is Savings (3%) or Checking (2%). Java picks the correct override at runtime.
     */
    private static void applyInterest(Customer me) {
        Account a = pickAccount(me);
        if (a == null) return;
        double updated = a.addInterest();
        System.out.printf("Interest applied. New balance: $%.2f%n", updated);
    }

    // ========================== HELPERS ==========================

    // Shared "which account?" prompt used by balance/deposit/withdraw/interest.
    private static Account pickAccount(Customer me) {
        if (me.getAccounts().isEmpty()) {
            System.out.println("You have no accounts.");
            return null;
        }
        System.out.print("Enter account number: ");
        int accNo = readInt();
        for (Account a : me.getAccounts()) {
            if (a.getAccountNumber() == accNo) return a;
        }
        System.out.println("No account with number " + accNo);
        return null; // caller checks for null before using it
    }

    // Linear search helpers. Return null when nothing matches (caller handles it).
    private static Customer findCustomerById(int id) {
        for (Customer c : customers) if (c.getId() == id) return c;
        return null;
    }

    private static Customer findCustomerByUsername(String username) {
        for (Customer c : customers) if (c.getUsername().equals(username)) return c;
        return null;
    }

    /*
     * EXCEPTION HANDLING: keep re-prompting until the user types a real number.
     * Integer.parseInt throws NumberFormatException on junk like "abc"; we catch it
     * so the program never crashes on a typo.
     */
    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a whole number: ");
            }
        }
    }

    private static double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid amount: ");
            }
        }
    }
}

// =========================== DOMAIN CLASSES ===========================
// (Extra top-level classes live in the same file for a simple single-file project.
//  In a real project each would be its own .java file.)

/*
 * Base class for anyone who can log in. Customer and Admin both "are-a" User,
 * so the shared login fields live here once (inheritance). `protected` lets
 * subclasses use these fields while still hiding them from unrelated code.
 */
class User {
    protected String username;
    protected String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

/*
 * A bank customer. ENCAPSULATION: fields are private, reached only via getters/setters.
 * username/password are NOT redeclared here -- they're inherited from User and set
 * through super(...). (Redeclaring them, like the original did, "shadows" the parent
 * fields and causes confusing bugs.)
 */
class Customer extends User {
    private int id;
    private String name;
    private List<Account> accounts; // one customer can hold many accounts

    public Customer(int id, String name, List<Account> accounts, String username, String password) {
        super(username, password); // hand the login info up to the User constructor
        this.id = id;
        this.name = name;
        this.accounts = accounts;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Account> getAccounts() { return accounts; }
    public void setAccounts(List<Account> accounts) { this.accounts = accounts; }

    // toString() controls how a Customer prints, e.g. in viewAllCustomers().
    @Override
    public String toString() {
        return "Customer{id=" + id + ", name='" + name + "', accounts=" + accounts.size() + "}";
    }
}

// Admin also "is-a" User. Defined for structure; login() currently checks admin directly.
class Admin extends User {
    public Admin(String username, String password) {
        super(username, password);
    }
}

/*
 * ABSTRACTION: Account is the shared blueprint for all account types. It's `abstract`
 * so `new Account(...)` is illegal -- you must create a concrete Savings/Checking.
 * addInterest() is abstract (no body) -> every subclass is FORCED to define its own rule.
 */
abstract class Account {
    protected int accountNumber;
    protected double balance;     // double, not int -> can hold cents / interest
    protected String accountType;

    public Account(int accountNumber, double balance, String accountType) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
    }

    public int getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getAccountType() { return accountType; }

    // Contract: every account type MUST implement this. The "what" is here; the "how" is below.
    public abstract double addInterest();

    @Override
    public String toString() {
        return String.format("%s #%d, balance=$%.2f", accountType, accountNumber, balance);
    }
}

// Concrete account: Checking earns 2%. Overrides the abstract method (polymorphism).
class CheckingAccount extends Account {
    public CheckingAccount(int accountNumber, double balance) {
        super(accountNumber, balance, "Checking");
    }

    @Override
    public double addInterest() {
        balance += balance * 0.02; // +2%
        return balance;
    }
}

// Concrete account: Savings earns 3%. Same method name, different behavior.
class SavingsAccount extends Account {
    public SavingsAccount(int accountNumber, double balance) {
        super(accountNumber, balance, "Savings");
    }

    @Override
    public double addInterest() {
        balance += balance * 0.03; // +3%
        return balance;
    }
}