package com.mycompany.techstore.Models.Objects;

public class Review {

    private final int review_id;
    private final int user_id;
    private final int product_id;
    private final int rating;
    private final String comment;
    private final String created_at;
    private final String user_name;

    public Review(int review_id, int user_id, int product_id, int rating, String comment, String created_at) {
        this(review_id, user_id, product_id, rating, comment, created_at, null);
    }

    public Review(int review_id, int user_id, int product_id, int rating, String comment, String created_at, String user_name) {
        this.review_id = review_id;
        this.user_id = user_id;
        this.product_id = product_id;
        this.rating = rating;
        this.comment = comment;
        this.created_at = created_at;
        this.user_name = user_name;
    }

    public Review(String user_name, int rating, String created_at, String comment) {
        this(0, 0, 0, rating, comment, created_at, user_name);
    }

    public int getReview_id() {
        return review_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUser_name() {
        return user_name;
    }

    public int getReviewId() {
        return review_id;
    }

    public int getUserId() {
        return user_id;
    }

    public int getProductId() {
        return product_id;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getDate() {
        return created_at;
    }

    public String getUser() {
        return user_name == null ? "" : user_name;
    }
}
