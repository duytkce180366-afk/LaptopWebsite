package com.mycompany.techstore.services;

import com.mycompany.techstore.model.User;

public class AuthService {
    private final String emailFormat = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
    private final String passwordFormat  = "^[a-z0-9]{32}$"; // MD5 format
    
    public AuthService() {
        
    }
    
    
    
    public User GetUserSignin() {
        return null;
    }
}
