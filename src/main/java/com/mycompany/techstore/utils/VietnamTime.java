package com.mycompany.techstore.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class VietnamTime {
  private static final int UTC_OFFSET_HOURS = 7;

  private VietnamTime() {}

  public static Timestamp fromUtc(Timestamp timestamp) {
    return timestamp == null
        ? null
        : Timestamp.valueOf(timestamp.toLocalDateTime().plusHours(UTC_OFFSET_HOURS));
  }

  public static Timestamp fromUtcOrVietnamLocal(Timestamp timestamp) {
    if (timestamp == null) return null;
    LocalDateTime value = timestamp.toLocalDateTime();
    LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);
    return value.isAfter(utcNow.plusMinutes(5)) ? timestamp : fromUtc(timestamp);
  }
}
