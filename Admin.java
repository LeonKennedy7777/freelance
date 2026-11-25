package model;

/** An administrator can view users and projects. */
public class Admin extends User {
    public Admin(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public String getDashboard() {
        return "\n--- ADMIN DASHBOARD ---\n1. View all users\n2. View all projects\n"
             + "3. Platform statistics\n0. Logout";
    }
}
