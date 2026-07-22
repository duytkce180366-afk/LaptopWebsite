package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.services.DashboardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/admin/reports")
public class AdminReportController extends HttpServlet {

  private static final DateTimeFormatter CSV_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final DateTimeFormatter CSV_DATE_TIME =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
  private final DashboardService service = new DashboardService();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    String type = text(req, "type");
    if (type.isBlank()) {
      type = "sales";
    }
    LocalDate to = date(req, "to");
    if (to == null) {
      to = LocalDate.now();
    }
    LocalDate from = date(req, "from");
    if (from == null) {
      from = to.minusDays(29);
    }
    if (from.isAfter(to)) {
      req.setAttribute("error", "From date must not be after To date.");
      from = to.minusDays(29);
    }
    try {
      List<Map<String, Object>> rows = service.report(type, from, to);
      if ("csv".equals(text(req, "format"))) {
        csv(res, type, rows);
        return;
      }
      req.setAttribute("rows", rows);
      req.setAttribute("type", type);
      req.setAttribute("from", from);
      req.setAttribute("to", to);
      req.getRequestDispatcher("/WEB-INF/JSPViews/AdminView/reports.jsp").forward(req, res);
    } catch (SQLException ex) {
      throw new ServletException(ex);
    }
  }

  private void csv(HttpServletResponse res, String type, List<Map<String, Object>> rows)
      throws IOException {
    res.setContentType("text/csv;charset=UTF-8");
    res.setHeader("Content-Disposition", "attachment; filename=techstore-" + type + "-report.csv");
    if (rows.isEmpty()) {
      res.getWriter().println("No data");
      return;
    }
    List<String> headers = new ArrayList<>(rows.get(0).keySet());
    res.getWriter().println(String.join(",", headers));
    for (Map<String, Object> row : rows) {
      List<String> cells = new ArrayList<>();
      for (String h : headers) {
        cells.add(escape(row.get(h)));
      }
      res.getWriter().println(String.join(",", cells));
    }
  }

  private String escape(Object value) {
    String v;
    if (value instanceof Timestamp timestamp) {
      v = CSV_DATE_TIME.format(timestamp.toLocalDateTime());
    } else if (value instanceof java.sql.Date date) {
      v = CSV_DATE.format(date.toLocalDate());
    } else {
      v = value == null ? "" : String.valueOf(value);
    }
    return "\"" + v.replace("\"", "\"\"") + "\"";
  }

  private String text(HttpServletRequest r, String n) {
    String v = r.getParameter(n);
    return v == null ? "" : v.trim();
  }

  private LocalDate date(HttpServletRequest r, String n) {
    try {
      return LocalDate.parse(text(r, n));
    } catch (Exception ex) {
      return null;
    }
  }
}
