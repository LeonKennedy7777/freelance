package service;

import exception.*;
import model.*;
import util.Validator;

import java.util.*;

/** Service layer: holds the data in collections and enforces all business rules. */
public class MarketplaceService {
    private final List<User> users = new ArrayList<>();
    private final Map<Integer, Project> projects = new HashMap<>();
    private final List<Payment> payments = new ArrayList<>();

    // ---------- Registration and login ----------
    public User register(User user) {
        if (!Validator.isValidName(user.getName()))
            throw new IllegalArgumentException("Name cannot be empty.");
        if (!Validator.isValidEmail(user.getEmail()))
            throw new IllegalArgumentException("Invalid email address.");
        for (User u : users)
            if (u.getEmail().equalsIgnoreCase(user.getEmail()))
                throw new IllegalArgumentException("Email is already registered.");
        users.add(user);
        return user;
    }

    public User login(String email, String password) {
        for (User u : users)
            if (u.getEmail().equalsIgnoreCase(email) && u.checkPassword(password)) return u;
        return null;
    }

    // ---------- Projects ----------
    public Project postProject(Client client, String title, String desc, String skill, double budget) {
        if (budget <= 0) throw new IllegalArgumentException("Budget must be positive.");
        Project p = new Project(title, desc, skill, budget, client);
        projects.put(p.getId(), p);
        return p;
    }

    public Project getProject(int id) { return projects.get(id); }

    /** Search by keyword (only OPEN projects). */
    public List<Project> searchProject(String keyword) {
        List<Project> result = new ArrayList<>();
        for (Project p : projects.values())
            if (p.getStatus() == ProjectStatus.OPEN
                && (p.getTitle().toLowerCase().contains(keyword.toLowerCase())
                    || p.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                result.add(p);
        return sortByBudget(result);
    }

    /** Overloaded: search by keyword and skill. */
    public List<Project> searchProject(String keyword, String skill) {
        List<Project> result = new ArrayList<>();
        for (Project p : searchProject(keyword))
            if (p.getSkill().equalsIgnoreCase(skill)) result.add(p);
        return result;
    }

    /** Overloaded: search by budget range. */
    public List<Project> searchProject(double min, double max) {
        List<Project> result = new ArrayList<>();
        for (Project p : projects.values())
            if (p.getStatus() == ProjectStatus.OPEN && p.getBudget() >= min && p.getBudget() <= max)
                result.add(p);
        return sortByBudget(result);
    }

    private List<Project> sortByBudget(List<Project> list) {
        list.sort(Comparator.comparingDouble(Project::getBudget)); // Comparator
        return list;
    }

    /** Moves a project to the next stage only if the change is legal. */
    private void moveTo(Project p, ProjectStatus target) throws InvalidProjectStateException {
        if (p.getStatus().next() != target)
            throw new InvalidProjectStateException("Cannot move project from "
                + p.getStatus() + " to " + target + ".");
        p.setStatus(target);
    }

    // ---------- Bidding and contract ----------
    public Bid placeBid(Freelancer f, int projectId, double amount)
            throws InvalidBidException, InvalidProjectStateException {
        Project p = findProject(projectId);
        if (p.getStatus() != ProjectStatus.OPEN)
            throw new InvalidProjectStateException("Project is not open for bids.");
        if (amount <= 0)
            throw new InvalidBidException("Bid amount must be positive.");
        if (amount > p.getBudget())
            throw new InvalidBidException("Bid Rs." + amount + " is above the budget Rs." + p.getBudget());
        Bid bid = new Bid(f, amount);
        p.getBids().add(bid);
        return bid;
    }

    /** Accepting a bid creates the Contract and moves the project to ASSIGNED. */
    public Contract acceptBid(Client c, int projectId, int bidId) throws InvalidProjectStateException {
        Project p = findProject(projectId);
        checkOwner(c, p);
        Bid chosen = null;
        for (Bid b : p.getBids()) if (b.getId() == bidId) chosen = b;
        if (chosen == null) throw new IllegalArgumentException("No such bid on this project.");
        moveTo(p, ProjectStatus.ASSIGNED);
        Contract contract = new Contract(p, chosen);
        p.setContract(contract);
        return contract;
    }

    // ---------- Work tracking ----------
    public void startWork(Freelancer f, int projectId) throws InvalidProjectStateException {
        Project p = findProject(projectId);
        checkFreelancer(f, p);
        moveTo(p, ProjectStatus.IN_PROGRESS);
    }

    public void submitWork(Freelancer f, int projectId) throws InvalidProjectStateException {
        Project p = findProject(projectId);
        checkFreelancer(f, p);
        moveTo(p, ProjectStatus.COMPLETED);
        p.getContract().setWorkSubmitted(true);
    }

    /** Payment is released only after the client approves the submitted work. */
    public Payment approveAndPay(Client c, int projectId, PaymentMethod method)
            throws InvalidProjectStateException, InsufficientBalanceException {
        Project p = findProject(projectId);
        checkOwner(c, p);
        if (p.getStatus() != ProjectStatus.COMPLETED || !p.getContract().isWorkSubmitted())
            throw new InvalidProjectStateException("Work has not been submitted yet.");
        Contract contract = p.getContract();
        method.pay(c, contract.getAmount());          // may throw InsufficientBalanceException
        moveTo(p, ProjectStatus.PAID);
        contract.getFreelancer().addEarnings(contract.getAmount());
        Payment payment = new Payment(contract, method.getName());
        payments.add(payment);
        return payment;
    }

    // ---------- Reviews ----------
    public void addReview(Client c, int projectId, int rating, String comment)
            throws InvalidProjectStateException {
        Project p = findProject(projectId);
        checkOwner(c, p);
        if (p.getStatus() != ProjectStatus.PAID)
            throw new InvalidProjectStateException("You can review only after payment.");
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1 to 5.");
        p.getContract().getFreelancer().addReview(new Review(rating, comment));
    }

    public List<Freelancer> freelancersByRating() {
        List<Freelancer> list = new ArrayList<>();
        for (User u : users) if (u instanceof Freelancer) list.add((Freelancer) u);
        list.sort((a, b) -> Double.compare(b.getAverageRating(), a.getAverageRating()));
        return list;
    }

    // ---------- Admin views ----------
    public List<User> getUsers() { return users; }
    public Collection<Project> getProjects() { return projects.values(); }

    public String statistics() {
        int paid = 0;
        for (Project p : projects.values()) if (p.getStatus() == ProjectStatus.PAID) paid++;
        return "Users: " + users.size() + " | Projects: " + projects.size()
             + " | Paid projects: " + paid + " | Payments made: " + payments.size();
    }

    // ---------- Helpers ----------
    private Project findProject(int id) {
        Project p = projects.get(id);
        if (p == null) throw new IllegalArgumentException("Project #" + id + " does not exist.");
        return p;
    }

    private void checkOwner(Client c, Project p) {
        if (p.getClient() != c) throw new IllegalArgumentException("This is not your project.");
    }

    private void checkFreelancer(Freelancer f, Project p) throws InvalidProjectStateException {
        if (p.getContract() == null || p.getContract().getFreelancer() != f)
            throw new InvalidProjectStateException("You are not assigned to this project.");
    }
}
