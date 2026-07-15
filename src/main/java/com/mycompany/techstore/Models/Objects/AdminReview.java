package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;

public class AdminReview {
    private int reviewId,userId,productId,rating;
    private String userName,userEmail,productName,comment,status;
    private Timestamp createdAt,moderatedAt;
    public int getReviewId(){return reviewId;} public void setReviewId(int v){reviewId=v;}
    public int getUserId(){return userId;} public void setUserId(int v){userId=v;}
    public int getProductId(){return productId;} public void setProductId(int v){productId=v;}
    public int getRating(){return rating;} public void setRating(int v){rating=v;}
    public String getUserName(){return userName;} public void setUserName(String v){userName=v;}
    public String getUserEmail(){return userEmail;} public void setUserEmail(String v){userEmail=v;}
    public String getProductName(){return productName;} public void setProductName(String v){productName=v;}
    public String getComment(){return comment;} public void setComment(String v){comment=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public Timestamp getCreatedAt(){return createdAt;} public void setCreatedAt(Timestamp v){createdAt=v;}
    public Timestamp getModeratedAt(){return moderatedAt;} public void setModeratedAt(Timestamp v){moderatedAt=v;}
}
