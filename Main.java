package main;

import exception.*;
import model.*;
import service.MarketplaceService;
import util.Validator;

import java.util.List;
import java.util.Scanner;

/** Console menu: routes Client, Freelancer and Admin users to their dashboards. */
public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static final MarketplaceService service = new MarketplaceService();

    public static void main(String[] args) {
        service.register(new Admin("Admin", "admin@market.com", "admin123"));
        System.out.println("=== FREELANCE PROJECT MARKETPLACE ===");
        while (true) {
            System.out.println("\n1. Register\n2. Login\n0. Exit");
            int choice = readInt("Choose: ");
            if (choice == 0) { System.out.println("Goodbye!"); return; }
            try {
                if (choice == 1) register();
                else if (choice == 2) login();
                else System.out.println("Invalid choice.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void register() {
        System.out.print("Role (1=Client, 2=Freelancer): ");
        int role = readInt("");
        String name = readLine("Name: ");
        String email = readLine("Email: ");
        String pass = readLine("Password (min 6 chars): ");
        if (!Validator.isValidPassword(pass))
            throw new IllegalArgumentException("Password must have at least 6 characters.");
        if (role == 1) service.register(new Client(name, email, pass));
        else if (role == 2) service.register(new Freelancer(name, email, pass,
                readLine("Skills (comma separated): ")));
        else throw new IllegalArgumentException("Invalid role.");
        System.out.println("Registered successfully.");
    }

    private static void login() {
        User user = service.login(readLine("Email: "), readLine("Password: "));
        if (user == null) { System.out.println("Wrong email or password."); return; }
        System.out.println("Welcome, " + user.getName() + "!");
        while (true) {
            System.out.println(user.getDashboard());          // polymorphic call
            int choice = readInt("Choose: ");
            if (choice == 0) return;
            try {
                if (user instanceof Client) clientAction((Client) user, choice);
                else if (user instanceof Freelancer) freelancerAction((Freelancer) user, choice);
                else adminAction(choice);
            } catch (InvalidBidException | InvalidProjectStateException
                     | InsufficientBalanceException e) {
                System.out.println("Not allowed: " + e.getMessage());   // app keeps running
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void clientAction(Client c, int choice) throws InvalidProjectStateException,
            InsufficientBalanceException {
        switch (choice) {
            case 1:
                Project p = service.postProject(c, readLine("Title: "), readLine("Description: "),
                        readLine("Required skill: "), readDouble("Budget: "));
                System.out.println("Posted: " + p);
                break;
            case 2:
                for (Project pr : service.getProjects()) {
                    if (pr.getClient() != c) continue;
                    System.out.println(pr);
                    for (Bid b : pr.getBids()) System.out.println("    " + b);
                }
                break;
            case 3:
                service.acceptBid(c, readInt("Project id: "), readInt("Bid id: "));
                System.out.println("Bid accepted. Contract created.");
                break;
            case 4:
                int pid = readInt("Project id: ");
                PaymentMethod method = readInt("Pay by 1=UPI, 2=Card: ") == 1
                        ? new UpiPayment(readLine("UPI id: "))
                        : new CardPayment(readLine("Last 4 digits: "));
                System.out.println(service.approveAndPay(c, pid, method));
                break;
            case 5:
                service.addReview(c, readInt("Project id: "), readInt("Rating (1-5): "),
                        readLine("Comment: "));
                System.out.println("Review saved.");
                break;
            case 6:
                c.addBalance(readDouble("Amount: "));
                System.out.println("Balance: Rs." + c.getBalance());
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void freelancerAction(Freelancer f, int choice) throws InvalidBidException,
            InvalidProjectStateException {
        switch (choice) {
            case 1:
                List<Project> found = service.searchProject(readLine("Keyword (blank = all): "));
                if (found.isEmpty()) System.out.println("No open projects found.");
                for (Project p : found) System.out.println(p);
                break;
            case 2:
                Bid bid = service.placeBid(f, readInt("Project id: "), readDouble("Your bid: "));
                System.out.println("Placed: " + bid);
                break;
            case 3:
                service.startWork(f, readInt("Project id: "));
                System.out.println("Work started.");
                break;
            case 4:
                service.submitWork(f, readInt("Project id: "));
                System.out.println("Work submitted for client approval.");
                break;
            case 5:
                System.out.println(f + "\nSkills: " + f.getSkills() + "\nRating: "
                        + f.getAverageRating() + "\nEarnings: Rs." + f.getEarnings());
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void adminAction(int choice) {
        switch (choice) {
            case 1: service.getUsers().forEach(System.out::println); break;
            case 2: service.getProjects().forEach(System.out::println); break;
            case 3: System.out.println(service.statistics()); break;
            default: System.out.println("Invalid choice.");
        }
    }

    // ---------- Input helpers (bad numbers do not crash the program) ----------
    private static String readLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    private static int readInt(String prompt) {
        try { return Integer.parseInt(readLine(prompt)); }
        catch (NumberFormatException e) { return -1; }
    }

    private static double readDouble(String prompt) {
        try { return Double.parseDouble(readLine(prompt)); }
        catch (NumberFormatException e) { return -1; }
    }
}
