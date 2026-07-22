package com.mycompany.techstore.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OrderStatusPolicyTest {
  @Test
  void acceptsRequiredWorkflow() {
    assertTrue(OrderStatusPolicy.canTransition("Pending", "Confirmed"));
    assertTrue(OrderStatusPolicy.canTransition("Confirmed", "Shipping"));
    assertTrue(OrderStatusPolicy.canTransition("Shipping", "Delivered"));
  }

  @Test
  void allowsCancellationOnlyBeforeShipping() {
    assertTrue(OrderStatusPolicy.canTransition("Pending", "Cancelled"));
    assertTrue(OrderStatusPolicy.canTransition("Confirmed", "Cancelled"));
    assertFalse(OrderStatusPolicy.canTransition("Shipping", "Cancelled"));
    assertFalse(OrderStatusPolicy.canTransition("Delivered", "Cancelled"));
  }

  @Test
  void rejectsSkippedOrRepeatedStatus() {
    assertFalse(OrderStatusPolicy.canTransition("Pending", "Shipping"));
    assertFalse(OrderStatusPolicy.canTransition("Pending", "Delivered"));
    assertFalse(OrderStatusPolicy.canTransition("Confirmed", "Confirmed"));
    assertThrows(
        IllegalArgumentException.class,
        () -> OrderStatusPolicy.requireValid("Pending", "Delivered"));
  }
}
