package com.mycompany.techstore.services;

import com.mycompany.techstore.Models.Objects.DashboardStats;
import com.mycompany.techstore.Repositories.DashboardRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class DashboardService {
  private final DashboardRepository repository = new DashboardRepository();

  public DashboardStats load(LocalDate from, LocalDate to) throws SQLException {
    return repository.load(from, to);
  }

  public List<Map<String, Object>> report(String type, LocalDate from, LocalDate to)
      throws SQLException {
    return repository.report(type, from, to);
  }
}
