package com.mycompany.techstore.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class PasswordUtilTest {

    @Test
    public void hashAndVerify() throws Exception {
        String pwd = "S3cureP@ssw0rd";
        String stored = PasswordUtil.hashPassword(pwd);
        assertNotNull(stored);
        assertTrue(PasswordUtil.verifyPassword(pwd, stored));
        assertFalse(PasswordUtil.verifyPassword("wrong", stored));
    }
}
