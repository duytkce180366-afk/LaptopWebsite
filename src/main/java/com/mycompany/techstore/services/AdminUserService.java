package com.mycompany.techstore.services;

import com.mycompany.techstore.Models.Objects.*;
import com.mycompany.techstore.Repositories.AdminUserRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class AdminUserService {
    private final AdminUserRepository repository=new AdminUserRepository();
    private final EmailService emailService=new EmailService();
    public PageResult<AdminUser> findAll(String q,int role,String status,int page)throws SQLException{return repository.findAll(q,role,status,Math.max(1,page),12);}
    public List<LookupOption> roles()throws SQLException{return repository.roles();}
    public void setStatus(int id,String status,int adminId)throws SQLException{
        if(id==adminId)throw new IllegalArgumentException("You cannot change your own status.");
        if(!Set.of("Active","Blocked").contains(status))throw new IllegalArgumentException("Invalid user status.");
        if("Blocked".equals(status)&&repository.isLastActiveAdmin(id))throw new IllegalArgumentException("The last active administrator cannot be blocked.");
        repository.setStatus(id,status,adminId);
        AdminUser user=repository.findById(id);if(user!=null)emailService.sendAccountChangeEmail(user.getEmail(),user.getFullName(),"TechStore account status changed","Your TechStore account status is now: "+user.getStatus()+".");
    }
    public void setRole(int id,int roleId,int adminId)throws SQLException{
        if(id==adminId)throw new IllegalArgumentException("You cannot change your own role.");
        if(!repository.isManagedRole(roleId))throw new IllegalArgumentException("Invalid role.");
        AdminUser selected=repository.findById(id);if(selected==null)throw new IllegalArgumentException("User not found.");
        if("Admin".equals(selected.getRoleName())&&repository.isLastActiveAdmin(id)){
            for(LookupOption role:repository.roles())if(role.getId()==roleId&&!"Admin".equals(role.getName()))throw new IllegalArgumentException("The last active administrator cannot be demoted.");
        }
        repository.setRole(id,roleId,adminId);
        AdminUser user=repository.findById(id);if(user!=null)emailService.sendAccountChangeEmail(user.getEmail(),user.getFullName(),"TechStore account role changed","Your TechStore account role is now: "+user.getRoleName()+".");
    }
}
