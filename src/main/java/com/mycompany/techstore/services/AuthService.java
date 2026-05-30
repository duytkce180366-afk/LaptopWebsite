package com.mycompany.techstore.services;

import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.resources.DbClass;

public class AuthService extends DbClass {
    private final String emailFormat = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
    private final String passwordFormat  = "^[a-z0-9]{32}$"; // MD5 format
    
    public User GetUserSignIn(String ) {
        User user = null;
        
        String sqlCommand = """
                            
                            """;
        
        return user;
    }
}
