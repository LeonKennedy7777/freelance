package model;

/** Abstract base class: every person on the platform is a User. */
public abstract class User {
    private static int counter = 1;
    private final int id;
    private final String name;
    private final String email;
    private final String password;

    protected User(String name, String email, String password) {
        this.id = counter++;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public boolean checkPassword(String input) {
        return password.equals(input);
    }

    /** Each role shows its own menu (polymorphism). */
    public abstract String getDashboard();

    @Override
    public String toString() {
        return "#" + id + " " + name + " <" + email + "> [" + getClass().getSimpleName() + "]";
    }
}
