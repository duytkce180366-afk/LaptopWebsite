package com.mycompany.techstore.services;

import com.mycompany.techstore.Models.Objects.*;
import com.mycompany.techstore.Repositories.AdminProductRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

public class AdminProductService {
    private static final Set<String> STATUSES=Set.of("Active","Out of Stock","Hidden","Inactive");
    private static final Set<String> LAPTOP_SPECS=Set.of("cpu","ram","storage","gpu","display","battery","os");
    private final AdminProductRepository repository=new AdminProductRepository();

    public PageResult<AdminProduct> findAll(String q,int category,int brand,String status,int page)throws SQLException{
        return repository.findAll(q,category,brand,status,Math.max(1,page),12);
    }
    public AdminProduct findById(int id)throws SQLException{return repository.findById(id);}
    public List<LookupOption> categories()throws SQLException{return repository.categories();}
    public List<LookupOption> brands()throws SQLException{return repository.brands();}
    public int create(AdminProduct product,int adminId)throws SQLException{product.setStock(0);validate(product);return repository.create(product,adminId);}
    public void update(AdminProduct product,int adminId)throws SQLException{
        AdminProduct current=repository.findById(product.getProductId());if(current==null)throw new IllegalArgumentException("Product not found.");
        product.setStock(current.getStock());validate(product);repository.update(product,adminId);
    }
    public void deactivate(int id,int adminId)throws SQLException{repository.deactivate(id,adminId);}
    public void receiveStock(int id,int quantity,String note,int adminId)throws SQLException{
        if(id<=0)throw new IllegalArgumentException("Product is required.");
        if(quantity<=0)throw new IllegalArgumentException("Received quantity must be greater than zero.");
        if(quantity>100000)throw new IllegalArgumentException("Received quantity is too large.");
        repository.receiveStock(id,quantity,clean(note),adminId);
    }
    public List<StockReceipt> recentReceipts()throws SQLException{return repository.recentReceipts();}

    private void validate(AdminProduct p)throws SQLException{
        p.setSku(clean(p.getSku()));p.setProductName(clean(p.getProductName()));
        p.setDescription(clean(p.getDescription()));p.setThumbnail(clean(p.getThumbnail()));
        p.setStatus(canonicalStatus(p.getStatus()));
        if(p.getSku().isEmpty())throw new IllegalArgumentException("SKU is required.");
        if(p.getProductName().isEmpty())throw new IllegalArgumentException("Product name is required.");
        if(p.getCategoryId()<=0||p.getBrandId()<=0)throw new IllegalArgumentException("Category and brand are required.");
        if(p.getPrice()==null||p.getPrice().compareTo(BigDecimal.ZERO)<=0)throw new IllegalArgumentException("Price must be greater than zero.");
        if(p.getStock()<0)throw new IllegalArgumentException("Stock cannot be negative.");
        if(repository.skuExists(p.getSku(),p.getProductId()))throw new IllegalArgumentException("SKU already exists.");
        if(p.getStock()==0&&"Active".equals(p.getStatus()))p.setStatus("Out of Stock");
        if(isLaptop(p.getCategoryId()))for(String key:LAPTOP_SPECS){
            if(clean(p.getSpecifications().get(key)).isEmpty())throw new IllegalArgumentException("Laptop specification '"+key+"' is required.");
        }
    }
    private boolean isLaptop(int categoryId)throws SQLException{
        for(LookupOption option:repository.categories())if(option.getId()==categoryId)return "Laptops".equalsIgnoreCase(option.getName());
        return false;
    }
    private String canonicalStatus(String value){
        for(String status:STATUSES)if(status.equalsIgnoreCase(clean(value)))return status;
        throw new IllegalArgumentException("Invalid product status.");
    }
    private String clean(String value){return value==null?"":value.trim();}
}
