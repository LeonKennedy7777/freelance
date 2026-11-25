package model;

import java.util.ArrayList;
import java.util.List;

/** A project posted by a client. Fields are private (encapsulation). */
public class Project {
    private static int counter = 1;
    private final int id;
    private final String title;
    private final String description;
    private final String skill;
    private final double budget;
    private final Client client;
    private ProjectStatus status = ProjectStatus.OPEN;
    private final List<Bid> bids = new ArrayList<>();
    private Contract contract;

    public Project(String title, String description, String skill, double budget, Client client) {
        this.id = counter++;
        this.title = title;
        this.description = description;
        this.skill = skill.toLowerCase();
        this.budget = budget;
        this.client = client;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSkill() { return skill; }
    public double getBudget() { return budget; }
    public Client getClient() { return client; }
    public ProjectStatus getStatus() { return status; }
    public List<Bid> getBids() { return bids; }
    public Contract getContract() { return contract; }
    public void setContract(Contract contract) { this.contract = contract; }

    /** The service layer checks that the change is legal before calling this. */
    public void setStatus(ProjectStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Project #" + id + " | " + title + " | skill: " + skill
             + " | budget: Rs." + budget + " | " + status;
    }
}
