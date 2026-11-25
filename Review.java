package model;

/** A rating (1-5) and comment left for a freelancer. */
public class Review {
    private final int rating;
    private final String comment;

    public Review(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public int getRating() { return rating; }
    public String getComment() { return comment; }
}
