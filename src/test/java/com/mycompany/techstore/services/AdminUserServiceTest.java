package com.mycompany.techstore.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AdminUserServiceTest {

  @Test
  void acceptsValidEmail() {
    assertTrue(AdminUserService.isValidEmail("staff@example.com"));
    assertTrue(AdminUserService.isValidEmail(" staff.member@example.com "));
  }

  @Test
  void rejectsInvalidEmail() {
    assertFalse(AdminUserService.isValidEmail(null));
    assertFalse(AdminUserService.isValidEmail("staff.example.com"));
    assertFalse(AdminUserService.isValidEmail("staff@@example.com"));
    assertFalse(AdminUserService.isValidEmail("staff@example"));
    assertFalse(AdminUserService.isValidEmail("staff @example.com"));
  }

  @Test
  void rejectsOversizedEmailWithoutRegularExpressionBacktracking() {
    assertFalse(AdminUserService.isValidEmail("a".repeat(10_000) + "@example.com"));
  }
}
