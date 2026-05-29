package Models.Objects;

public class Review {
    private String user;
    private int rating;
    private String date;
    private String comment;

    public Review(String user, int rating, String date, String comment) {
        this.user = user;
        this.rating = rating;
        this.date = date;
        this.comment = comment;
    }

    // Getters and Setters
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
