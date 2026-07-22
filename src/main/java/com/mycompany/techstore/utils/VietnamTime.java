package com.mycompany.techstore.utils;

import java.sql.Timestamp;

public final class VietnamTime {
  private static final int UTC_OFFSET_HOURS = 7;

  private VietnamTime() {}

  public static Timestamp fromUtc(Timestamp timestamp) {
    return timestamp == null
        ? null
        : Timestamp.valueOf(timestamp.toLocalDateTime().plusHours(UTC_OFFSET_HOURS));
  }
}
