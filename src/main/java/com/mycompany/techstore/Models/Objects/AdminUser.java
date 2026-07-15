package com.mycompany.techstore.Models.Objects;

import java.sql.Timestamp;

public class AdminUser {
    private int userId,roleId;
    private String roleName,fullName,email,phone,status;
    private boolean verified;
    private Timestamp createdAt,updatedAt;
    public int getUserId(){return userId;} public void setUserId(int v){userId=v;}
    public int getRoleId(){return roleId;} public void setRoleId(int v){roleId=v;}
    public String getRoleName(){return roleName;} public void setRoleName(String v){roleName=v;}
    public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public boolean isVerified(){return verified;} public void setVerified(boolean v){verified=v;}
    public Timestamp getCreatedAt(){return createdAt;} public void setCreatedAt(Timestamp v){createdAt=v;}
    public Timestamp getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Timestamp v){updatedAt=v;}
}
