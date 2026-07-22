package com.mycompany.techstore.services;

import com.mycompany.techstore.Exceptions.BackOfficeValidationException;
import com.mycompany.techstore.Models.Objects.*;
import com.mycompany.techstore.Repositories.AdminOrderRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;

public class AdminOrderService {
  private static final Set<String> TARGETS =
      Set.of("Confirmed", "Shipping", "Delivered", "Cancelled");
  private final AdminOrderRepository repository = new AdminOrderRepository();

  public PageResult<AdminOrder> findAll(
      String q, String status, String payment, LocalDate from, LocalDate to, int page)
      throws SQLException {
    return repository.findAll(q, status, payment, from, to, Math.max(1, page), 12);
  }

  public AdminOrder findById(int id) throws SQLException {
    return repository.findById(id);
  }

  public void changeStatus(int id, String target, String note, int adminId) throws SQLException {
    if (!TARGETS.contains(target))
      throw new BackOfficeValidationException("Invalid target status.");
    repository.changeStatus(id, target, note, adminId);
  }
}
