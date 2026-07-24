package com.mycompany.techstore.services;

import com.mycompany.techstore.Exceptions.BackOfficeValidationException;
import com.mycompany.techstore.Models.Objects.*;
import com.mycompany.techstore.Repositories.AdminReviewRepository;
import java.sql.SQLException;
import java.util.Set;

public class AdminReviewService {
  private final AdminReviewRepository repository = new AdminReviewRepository();

  public PageResult<AdminReview> findAll(String q, int rating, String status, int page)
      throws SQLException {
    return repository.findAll(q, rating, status, Math.max(1, page), 12);
  }

  public void setStatus(int id, String status, int adminId) throws SQLException {
    if (!Set.of("Visible", "Hidden").contains(status))
      throw new BackOfficeValidationException("Invalid review status.");
    repository.setStatus(id, status, adminId);
  }
}
