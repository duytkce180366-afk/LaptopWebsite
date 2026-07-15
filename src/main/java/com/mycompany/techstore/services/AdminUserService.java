package com.mycompany.techstore.services;

import com.mycompany.techstore.Models.Objects.*;
import com.mycompany.techstore.Repositories.AdminUserRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class AdminUserService {
    private final AdminUserRepository repository=new AdminUserRepository();
    public PageResult<AdminUser> findAll(String q,int role,String status,int page)throws SQLException{return repository.findAll(q,role,status,Math.max(1,page),12);}
    public List<LookupOption> roles()throws SQLException{return repository.roles();}
    public void setStatus(int id,String status,int adminId)throws SQLException{
        if(id==adminId)throw new IllegalArgumentException("You cannot change your own status.");
        if(!Set.of("Active","Blocked").contains(status))throw new IllegalArgumentException("Invalid user status.");
        if("Blocked".equals(status)&&repository.isLastActiveAdmin(id))throw new IllegalArgumentException("The last active administrator cannot be blocked.");
        repository.setStatus(id,status,adminId);
    }
    public void setRole(int id,int roleId,int adminId)throws SQLException{
        if(id==adminId)throw new IllegalArgumentException("You cannot change your own role.");
        if(roleId!=1&&roleId!=2)throw new IllegalArgumentException("Invalid role.");
        if(roleId!=1&&repository.isLastActiveAdmin(id))throw new IllegalArgumentException("The last active administrator cannot be demoted.");
        repository.setRole(id,roleId,adminId);
    }
}
