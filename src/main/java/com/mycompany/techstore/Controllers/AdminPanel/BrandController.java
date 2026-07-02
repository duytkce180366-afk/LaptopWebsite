package com.mycompany.techstore.Controllers.AdminPanel;

import com.mycompany.techstore.Models.Objects.Brand;
import com.mycompany.techstore.Services.BrandService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/brands")
public class BrandController extends HttpServlet {

    private BrandService brandService;

    @Override
    public void init() {
        brandService = new BrandService();
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        List<Brand> brands =
                brandService.getAllBrands();

        request.setAttribute("brands", brands);

        request.getRequestDispatcher(
                "/admin/brand/list.jsp")
                .forward(request, response);
    }

}