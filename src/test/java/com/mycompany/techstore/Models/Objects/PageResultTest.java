package com.mycompany.techstore.Models.Objects;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class PageResultTest {
  @Test
  void calculatesPages() {
    PageResult<Integer> result = new PageResult<>(List.of(1, 2), 2, 12, 25);
    assertEquals(3, result.getTotalPages());
    assertEquals(2, result.getPage());
  }

  @Test
  void keepsAtLeastOnePage() {
    assertEquals(1, new PageResult<>(List.of(), 1, 12, 0).getTotalPages());
  }
}
