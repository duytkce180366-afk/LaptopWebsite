package com.mycompany.techstore.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BackOfficeAuthorizationPolicyTest {
  @Test
  void staffCanPerformDailyOperations() {
    assertTrue(BackOfficeAuthorizationPolicy.canAccess("Staff", "POST", "/admin/inventory"));
    assertTrue(BackOfficeAuthorizationPolicy.canAccess("Staff", "POST", "/admin/orders/detail"));
    assertTrue(BackOfficeAuthorizationPolicy.canAccess("Staff", "POST", "/admin/reviews/status"));
    assertTrue(BackOfficeAuthorizationPolicy.canAccess("Staff", "GET", "/admin/reports"));
    assertTrue(BackOfficeAuthorizationPolicy.canAccess("Staff", "GET", "/admin/users"));
    assertTrue(BackOfficeAuthorizationPolicy.canAccess("Staff", "POST", "/admin/users/status"));
  }

  @Test
  void staffCannotManageProductsRolesOrReportData() {
    assertFalse(BackOfficeAuthorizationPolicy.canAccess("Staff", "GET", "/admin/products"));
    assertFalse(BackOfficeAuthorizationPolicy.canAccess("Staff", "POST", "/admin/users/role"));
    assertFalse(BackOfficeAuthorizationPolicy.canAccess("Staff", "GET", "/admin/users/new"));
    assertFalse(BackOfficeAuthorizationPolicy.canAccess("Staff", "POST", "/admin/users/delete"));
    assertFalse(BackOfficeAuthorizationPolicy.canAccess("Staff", "POST", "/admin/reports"));
  }

  @Test
  void adminCanAccessAllBackOfficeOperations() {
    assertTrue(BackOfficeAuthorizationPolicy.canAccess("Admin", "POST", "/admin/users/role"));
    assertTrue(
        BackOfficeAuthorizationPolicy.canAccess("Admin", "POST", "/admin/products/deactivate"));
  }
}
