package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.DashboardStats;
import com.mycompany.techstore.resources.DbClass;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class DashboardRepository {
    public DashboardStats load()throws SQLException{
        DashboardStats s=new DashboardStats();try(Connection con=new DbClass().getConnection()){
            s.setProducts(number(con,"SELECT COUNT(*) FROM dbo.bs_Products WHERE status<>'Inactive'"));
            s.setOrders(number(con,"SELECT COUNT(*) FROM dbo.bs_Orders"));s.setUsers(number(con,"SELECT COUNT(*) FROM dbo.bs_user WHERE role_id=2"));
            s.setReviews(number(con,"SELECT COUNT(*) FROM dbo.bs_Reviews"));s.setRevenue(money(con,"SELECT COALESCE(SUM(total_amount+shipping_fee-discount_amount),0) FROM dbo.bs_Orders WHERE order_status='Delivered'"));
            s.setOrderStatuses(rows(con,"SELECT order_status label,COUNT(*) value FROM dbo.bs_Orders GROUP BY order_status ORDER BY order_status"));
            s.setDailyRevenue(rows(con,"SELECT TOP 30 CONVERT(date,created_at) label,SUM(total_amount+shipping_fee-discount_amount) value FROM dbo.bs_Orders WHERE order_status='Delivered' GROUP BY CONVERT(date,created_at) ORDER BY label"));
            s.setTopProducts(rows(con,"SELECT TOP 5 p.product_name label,SUM(d.quantity) value FROM dbo.bs_OrderDetails d JOIN dbo.bs_Orders o ON o.order_id=d.order_id JOIN dbo.bs_Products p ON p.product_id=d.product_id WHERE o.order_status='Delivered' GROUP BY p.product_name ORDER BY value DESC"));
            s.setLowStock(rows(con,"SELECT TOP 8 product_id id,product_name label,stock value FROM dbo.bs_Products WHERE status<>'Inactive' AND stock<=5 ORDER BY stock,product_name"));
            s.setRecentOrders(rows(con,"SELECT TOP 8 o.order_id id,u.full_name label,o.order_status status,o.total_amount+o.shipping_fee-o.discount_amount value FROM dbo.bs_Orders o JOIN dbo.bs_user u ON u.user_id=o.user_id ORDER BY o.created_at DESC"));
            s.setRecentAudits(rows(con,"SELECT TOP 8 a.audit_id id,u.full_name label,a.action status,a.entity_type+' #'+COALESCE(a.entity_id,'') detail FROM dbo.bs_AdminAuditLogs a JOIN dbo.bs_user u ON u.user_id=a.admin_id ORDER BY a.created_at DESC"));
        }return s;
    }
    public List<Map<String,Object>> report(String type,LocalDate from,LocalDate to)throws SQLException{
        String sql=switch(type){
            case "orders"->"SELECT o.order_id,u.full_name customer,u.email,o.payment_method,o.order_status,o.total_amount+o.shipping_fee-o.discount_amount total,o.created_at FROM dbo.bs_Orders o JOIN dbo.bs_user u ON u.user_id=o.user_id WHERE (? IS NULL OR o.created_at>=?) AND (? IS NULL OR o.created_at<DATEADD(day,1,?)) ORDER BY o.created_at DESC";
            case "products"->"SELECT p.sku,p.product_name,SUM(d.quantity) quantity,SUM(d.subtotal) revenue FROM dbo.bs_OrderDetails d JOIN dbo.bs_Orders o ON o.order_id=d.order_id JOIN dbo.bs_Products p ON p.product_id=d.product_id WHERE o.order_status='Delivered' AND (? IS NULL OR o.created_at>=?) AND (? IS NULL OR o.created_at<DATEADD(day,1,?)) GROUP BY p.sku,p.product_name ORDER BY quantity DESC";
            default->"SELECT CONVERT(date,created_at) report_date,COUNT(*) orders,SUM(total_amount+shipping_fee-discount_amount) revenue FROM dbo.bs_Orders WHERE order_status='Delivered' AND (? IS NULL OR created_at>=?) AND (? IS NULL OR created_at<DATEADD(day,1,?)) GROUP BY CONVERT(date,created_at) ORDER BY report_date";};
        try(Connection con=new DbClass().getConnection();PreparedStatement ps=con.prepareStatement(sql)){
            setDate(ps,1,from);setDate(ps,2,from);setDate(ps,3,to);setDate(ps,4,to);try(ResultSet rs=ps.executeQuery()){return allColumns(rs);}
        }
    }
    private long number(Connection c,String sql)throws SQLException{try(PreparedStatement p=c.prepareStatement(sql);ResultSet r=p.executeQuery()){r.next();return r.getLong(1);}}
    private BigDecimal money(Connection c,String sql)throws SQLException{try(PreparedStatement p=c.prepareStatement(sql);ResultSet r=p.executeQuery()){r.next();return r.getBigDecimal(1);}}
    private List<Map<String,Object>> rows(Connection c,String sql)throws SQLException{try(PreparedStatement p=c.prepareStatement(sql);ResultSet r=p.executeQuery()){return allColumns(r);}}
    private List<Map<String,Object>> allColumns(ResultSet rs)throws SQLException{List<Map<String,Object>> out=new ArrayList<>();ResultSetMetaData m=rs.getMetaData();while(rs.next()){Map<String,Object> row=new LinkedHashMap<>();for(int i=1;i<=m.getColumnCount();i++)row.put(m.getColumnLabel(i),rs.getObject(i));out.add(row);}return out;}
    private void setDate(PreparedStatement p,int i,LocalDate d)throws SQLException{if(d==null)p.setNull(i,Types.DATE);else p.setDate(i,java.sql.Date.valueOf(d));}
}
