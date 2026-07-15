package com.mycompany.techstore.Models.Objects;

import java.math.BigDecimal;
import java.util.*;

public class DashboardStats {
    private long products,orders,users,reviews;
    private BigDecimal revenue=BigDecimal.ZERO;
    private List<Map<String,Object>> orderStatuses=new ArrayList<>(),dailyRevenue=new ArrayList<>(),topProducts=new ArrayList<>(),lowStock=new ArrayList<>(),recentOrders=new ArrayList<>(),recentAudits=new ArrayList<>();
    public long getProducts(){return products;} public void setProducts(long v){products=v;}
    public long getOrders(){return orders;} public void setOrders(long v){orders=v;}
    public long getUsers(){return users;} public void setUsers(long v){users=v;}
    public long getReviews(){return reviews;} public void setReviews(long v){reviews=v;}
    public BigDecimal getRevenue(){return revenue;} public void setRevenue(BigDecimal v){revenue=v;}
    public List<Map<String,Object>> getOrderStatuses(){return orderStatuses;} public void setOrderStatuses(List<Map<String,Object>> v){orderStatuses=v;}
    public List<Map<String,Object>> getDailyRevenue(){return dailyRevenue;} public void setDailyRevenue(List<Map<String,Object>> v){dailyRevenue=v;}
    public List<Map<String,Object>> getTopProducts(){return topProducts;} public void setTopProducts(List<Map<String,Object>> v){topProducts=v;}
    public List<Map<String,Object>> getLowStock(){return lowStock;} public void setLowStock(List<Map<String,Object>> v){lowStock=v;}
    public List<Map<String,Object>> getRecentOrders(){return recentOrders;} public void setRecentOrders(List<Map<String,Object>> v){recentOrders=v;}
    public List<Map<String,Object>> getRecentAudits(){return recentAudits;} public void setRecentAudits(List<Map<String,Object>> v){recentAudits=v;}
}
