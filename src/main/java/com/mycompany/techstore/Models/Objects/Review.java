package com.mycompany.techstore.Models.Objects;

public class Review {
    private int review_id;
    private int user_id;
    private int product_id;
    private int rating;
    private String comment;
    private String created_at;
    private String user_name;

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
        this.rating = rating;
        this.comment = comment;
        this.created_at = created_at;
        this.user_name = user_name;
    }

    public int getReview_id() {
        return review_id;
    }

    public void setReview_id(int review_id) {
        this.review_id = review_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getReviewId() {
        return review_id;
    }

    public void setReviewId(int review_id) {
        this.review_id = review_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public int getProductId() {
        return product_id;
    }

    public void setProductId(int product_id) {
        this.product_id = product_id;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public String getDate() {
        return created_at;
    }

    public void setDate(String created_at) {
        this.created_at = created_at;
    }

    public String getUser() {
        return user_name == null ? "" : user_name;
    }

    public void setUser(String user) {
        this.user_name = user;
    }
}
