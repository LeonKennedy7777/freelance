package model;

/** A freelancer's offer on a project. */
public class Bid {
    private static int counter = 1;
    private final int id;
    private final Freelancer freelancer;
    private final double amount;

    public Bid(Freelancer freelancer, double amount) {
        this.id = counter++;
        this.freelancer = freelancer;
        this.amount = amount;
    }

    public int getId() { return id; }
    public Freelancer getFreelancer() { return freelancer; }
    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return "Bid #" + id + " by " + freelancer.getName() + " : Rs." + amount;
    }
}
