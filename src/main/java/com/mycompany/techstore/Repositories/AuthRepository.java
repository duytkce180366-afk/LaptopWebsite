package com.mycompany.techstore.Repositories;

import com.mycompany.techstore.Models.Objects.User;

public class AuthRepository {

    public User GetUserSignIn(String email, String password) {
        User user = null;
        
        String sqlCommand = """
                            SELECT TOP(1) 
                            """;

        return user;
    }
}
