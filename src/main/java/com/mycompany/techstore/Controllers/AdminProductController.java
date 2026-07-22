package com.mycompany.techstore.Controllers;

import com.mycompany.techstore.Exceptions.BackOfficeNotFoundException;

import com.mycompany.techstore.Models.Objects.*;
import com.mycompany.techstore.services.AdminProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/admin/products", "/admin/products/*"})
public class AdminProductController extends HttpServlet {

    private final AdminProductService service = new AdminProductService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            if ("/new".equals(path)) {
                showForm(req, res, new AdminProduct());
                return;
            }
            if ("/edit".equals(path)) {
                AdminProduct p = service.findById(intParam(req, "id", 0));
                if (p == null) {
                    throw new BackOfficeNotFoundException("Product not found.");
                }
                showForm(req, res, p);
                return;
            }
            list(req, res);
        } catch (BackOfficeNotFoundException ex) {
            req.getSession().setAttribute("adminError", ex.getMessage());
            res.sendRedirect(req.getContextPath() + "/admin/products");
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User admin = (User) req.getSession().getAttribute("loggedUser");
        String path = req.getPathInfo();
        try {
            if ("/deactivate".equals(path)) {
                service.deactivate(intParam(req, "id", 0), admin.getUser_id());
                flash(req, "Product was deactivated.");
            } else {
                AdminProduct product = fromRequest(req);
                if (product.getProductId() == 0) {
                    service.create(product, admin.getUser_id());
                    flash(req, "Product was created.");
                } else {
                    service.update(product, admin.getUser_id());
                    flash(req, "Product was updated.");
                }
            }
            res.sendRedirect(req.getContextPath() + "/admin/products");
        } catch (IllegalArgumentException ex) {
            req.setAttribute("error", ex.getMessage());
            try {
                showForm(req, res, fromRequest(req));
            } catch (SQLException sql) {
                throw new ServletException(sql);
            }
        } catch (BackOfficeNotFoundException ex) {
            req.getSession().setAttribute("adminError", ex.getMessage());
            res.sendRedirect(req.getContextPath() + "/admin/products");
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse res) throws SQLException, ServletException, IOException {
        String q = value(req, "q"), status = value(req, "status");
        int category = intParam(req, "category", 0), brand = intParam(req, "brand", 0), page = intParam(req, "page", 1);
        req.setAttribute("result", service.findAll(q, category, brand, status, page));
        req.setAttribute("categories", service.categories());
        req.setAttribute("brands", service.brands());
        req.setAttribute("q", q);
        req.setAttribute("selectedCategory", category);
        req.setAttribute("selectedBrand", brand);
        req.setAttribute("selectedStatus", status);
        req.getRequestDispatcher("/WEB-INF/JSPViews/AdminView/products.jsp").forward(req, res);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse res, AdminProduct p) throws SQLException, ServletException, IOException {
        req.setAttribute("product", p);
        req.setAttribute("categories", service.categories());
        req.setAttribute("brands", service.brands());
        req.getRequestDispatcher("/WEB-INF/JSPViews/AdminView/product-form.jsp").forward(req, res);
    }

    private AdminProduct fromRequest(HttpServletRequest req) {
        AdminProduct p = new AdminProduct();
        p.setProductId(intParam(req, "id", 0));
        p.setCategoryId(intParam(req, "categoryId", 0));
        p.setBrandId(intParam(req, "brandId", 0));
        p.setSku(value(req, "sku"));
        p.setProductName(value(req, "productName"));
        p.setDescription(value(req, "description"));
        p.setPrice(decimal(req, "price"));
        p.setStock(0);
        p.setThumbnail(value(req, "thumbnail"));
        p.setStatus(value(req, "status"));
        String[] keys = req.getParameterValues("specKey"), values = req.getParameterValues("specValue");
        Map<String, String> specs = new LinkedHashMap<>();
        if (keys != null && values != null) {
            for (int i = 0; i < Math.min(keys.length, values.length); i++) {
                if (!keys[i].isBlank() && !values[i].isBlank()) {
                    specs.put(keys[i].trim(), values[i].trim());
                }
            }
        }
        p.setSpecifications(specs);
        return p;
    }

    private int intParam(HttpServletRequest r, String n, int d) {
        try {
            return Integer.parseInt(value(r, n));
        } catch (Exception ex) {
            return d;
        }
    }

    private BigDecimal decimal(HttpServletRequest r, String n) {
        try {
            return new BigDecimal(value(r, n));
        } catch (Exception ex) {
            return null;
        }
    }

    private String value(HttpServletRequest r, String n) {
        String v = r.getParameter(n);
        return v == null ? "" : v.trim();
    }

    private void flash(HttpServletRequest req, String message) {
        req.getSession().setAttribute("adminMessage", message);
    }
}
