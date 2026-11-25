package model;

/** Created automatically when a client accepts a bid. */
public class Contract {
    private final Project project;
    private final Bid bid;
    private boolean workSubmitted;

    public Contract(Project project, Bid bid) {
        this.project = project;
        this.bid = bid;
    }

    public Project getProject() { return project; }
    public double getAmount() { return bid.getAmount(); }
    public Freelancer getFreelancer() { return bid.getFreelancer(); }
    public boolean isWorkSubmitted() { return workSubmitted; }
    public void setWorkSubmitted(boolean value) { this.workSubmitted = value; }
}
