//package com.mycompany.techstore.services;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import org.junit.jupiter.api.Test;
//
//class AdminUserServiceTest {
//
//  @Test
//  void acceptsValidEmail() {
//    assertTrue(AdminUserService.isValidEmail("staff@example.com"));
//    assertTrue(AdminUserService.isValidEmail(" staff.member@example.com "));
//  }
//
//  @Test
//  void rejectsInvalidEmail() {
//    assertFalse(AdminUserService.isValidEmail(null));
//    assertFalse(AdminUserService.isValidEmail("staff.example.com"));
//    assertFalse(AdminUserService.isValidEmail("staff@@example.com"));
//    assertFalse(AdminUserService.isValidEmail("staff@example"));
//    assertFalse(AdminUserService.isValidEmail("staff @example.com"));
//  }
//
//  @Test
//  void rejectsOversizedEmailWithoutRegularExpressionBacktracking() {
//    assertFalse(AdminUserService.isValidEmail("a".repeat(10_000) + "@example.com"));
//  }
//
//  @Test
//  void acceptsMatchingStrongStaffPasswords() {
//    assertDoesNotThrow(
//        () -> AdminUserService.validatePasswordConfirmation("Staff123", "Staff123"));
//  }
//
//  @Test
//  void rejectsMissingStaffPasswordConfirmation() {
//    assertThrows(
//        IllegalArgumentException.class,
//        () -> AdminUserService.validatePasswordConfirmation("Staff123", ""));
//  }
//
//  @Test
//  void rejectsWeakStaffPassword() {
//    assertThrows(
//        IllegalArgumentException.class,
//        () -> AdminUserService.validatePasswordConfirmation("staff123", "staff123"));
//  }
//
//  @Test
//  void rejectsDifferentStaffPasswords() {
//    assertThrows(
//        IllegalArgumentException.class,
//        () -> AdminUserService.validatePasswordConfirmation("Staff123", "Staff124"));
//  }
//}
