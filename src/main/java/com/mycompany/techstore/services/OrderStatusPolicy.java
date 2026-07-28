package com.mycompany.techstore.services;

import java.util.Set;

public final class OrderStatusPolicy {

    private OrderStatusPolicy() {
    }

    public static boolean canTransition(String current, String target) {
        if ("Pending".equals(current)) {
            return Set.of("Confirmed", "Cancelled").contains(target);
        }
        if ("Confirmed".equals(current)) {
            return Set.of("Shipping", "Cancelled").contains(target);
        }
        if ("Shipping".equals(current)) {
            return "Delivered".equals(target);
        }
        if ("Delivered".equals(current)) {
            return Set.of("Completed", "Return Requested").contains(target);
        }
        if ("Return Requested".equals(current)) {
            return Set.of("Returned", "Return Rejected").contains(target);
        }
        return false;
    }

    public static void requireValid(String current, String target) {
        if (!canTransition(current, target)) {
            throw new IllegalArgumentException("Invalid order transition: " + current + " -> " + target);
        }
    }
}
