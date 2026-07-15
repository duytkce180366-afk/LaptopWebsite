package com.mycompany.techstore.Filters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AccountStatusFilterTest {

    @Test
    void skipsStaticAssets() {
        assertTrue(AccountStatusFilter.isStaticRequest("/TechStore/css/site.css", "/TechStore"));
        assertTrue(AccountStatusFilter.isStaticRequest("/TechStore/images/logo.png", "/TechStore"));
        assertTrue(AccountStatusFilter.isStaticRequest("/TechStore/lib/app.js", "/TechStore"));
    }

    @Test
    void checksDynamicRequests() {
        assertFalse(AccountStatusFilter.isStaticRequest("/TechStore/home", "/TechStore"));
        assertFalse(AccountStatusFilter.isStaticRequest("/TechStore/cart", "/TechStore"));
        assertFalse(AccountStatusFilter.isStaticRequest("/TechStore/admin/users", "/TechStore"));
    }
}
