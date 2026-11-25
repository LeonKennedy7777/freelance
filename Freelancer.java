package model;

import java.util.ArrayList;
import java.util.List;

/** A freelancer has a skill list, receives reviews and earns money. */
public class Freelancer extends User {
    private final List<String> skills = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();
    private double earnings;

    public Freelancer(String name, String email, String password, String skillList) {
        super(name, email, password);
        for (String s : skillList.split(",")) {
            if (!s.trim().isEmpty()) skills.add(s.trim().toLowerCase());
        }
    }

    public List<String> getSkills() { return skills; }
    public double getEarnings() { return earnings; }
    public void addEarnings(double amount) { earnings += amount; }
    public void addReview(Review review) { reviews.add(review); }

    public double getAverageRating() {
        if (reviews.isEmpty()) return 0;
        double total = 0;
        for (Review r : reviews) total += r.getRating();
        return total / reviews.size();
    }

    @Override
    public String getDashboard() {
        return "\n--- FREELANCER DASHBOARD ---\n1. Search projects\n2. Place a bid\n"
             + "3. Start work\n4. Submit work\n5. My profile\n0. Logout";
    }
}
