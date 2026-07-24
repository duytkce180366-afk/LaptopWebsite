package com.mycompany.techstore.services;

public final class BackOfficeAuthorizationPolicy {
  private BackOfficeAuthorizationPolicy() {}

  public static boolean isBackOfficeRole(String role) {
    return "Admin".equals(role) || "Staff".equals(role);
  }

  public static boolean canAccess(String role, String method, String path) {
    if ("Admin".equals(role)) {
      return true;
    }
    if (!"Staff".equals(role) || path == null) {
      return false;
    }

    if (matches(path, "/admin/products")) {
      return false;
    }
    if (matches(path, "/admin/reports")) {
      return isReadOnly(method);
    }
    if (matches(path, "/admin/users")) {
      return isReadOnly(method) ? "/admin/users".equals(path) : "/admin/users/status".equals(path);
    }
    return path.equals("/admin")
        || path.equals("/admin/")
        || matches(path, "/admin/dashboard")
        || matches(path, "/admin/inventory")
        || matches(path, "/admin/orders")
        || matches(path, "/admin/reviews");
  }

  private static boolean isReadOnly(String method) {
    return "GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method);
  }

  private static boolean matches(String path, String root) {
    return path.equals(root) || path.startsWith(root + "/");
  }
}
