package model;

/** A client posts projects and pays for approved work. */
public class Client extends User {
    private double balance;

    public Client(String name, String email, String password) {
        super(name, email, password);
    }

    public double getBalance() { return balance; }

    public void addBalance(double amount) {
        if (amount > 0) balance += amount;
    }

    public void withdraw(double amount) {
        balance -= amount;
    }

    @Override
    public String getDashboard() {
        return "\n--- CLIENT DASHBOARD ---\n1. Post project\n2. View my projects and bids\n"
             + "3. Accept a bid\n4. Approve work and pay\n5. Review a freelancer\n"
             + "6. Add balance\n0. Logout";
    }
}
