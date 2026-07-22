package com.mycompany.techstore.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class VietnamTimeTest {

  @Test
  void convertsUtcTimestampToVietnamTime() {
    Timestamp utc = Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC).minusHours(1));

    assertEquals(utc.toLocalDateTime().plusHours(7), VietnamTime.fromUtcOrVietnamLocal(utc).toLocalDateTime());
  }

  @Test
  void doesNotConvertLegacyTimestampAlreadyStoredInVietnamTime() {
    Timestamp vietnamLocal = Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC).plusHours(7));

    assertEquals(vietnamLocal, VietnamTime.fromUtcOrVietnamLocal(vietnamLocal));
  }
}
