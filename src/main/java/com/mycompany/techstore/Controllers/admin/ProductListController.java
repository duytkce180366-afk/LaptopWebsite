/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import com.mycompany.techstore.Models.Objects.Product;
import repository.CategoryRepository;
import service.ProductService;
import com.mycompany.techstore.Repositories.BrandRepository;

@WebServlet("/admin/products")

public class ProductListController extends HttpServlet {

    private ProductService productService;
    private CategoryRepository categoryRepo;
    private BrandRepository brandRepo;

    @Override
    public void init() {

        productService = new ProductService();
        categoryRepo = new CategoryRepository();
        brandRepo = new BrandRepository();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String keyword
                = request.getParameter("keyword");
        String categoryId
                = request.getParameter("categoryId");

        String status
                = request.getParameter("status");

        String pageRaw
                = request.getParameter("page");

        String brandId
                = request.getParameter("brandId");

        int page = 1;
        int pageSize = 5;

        try {

            if (pageRaw != null) {

                page = Integer.parseInt(pageRaw);

            }

        } catch (Exception e) {

            page = 1;

        }

        List<Product> products;

        Integer category = null;
        Integer brand = null;

        if (categoryId != null
                && !categoryId.isEmpty()) {

            category
                    = Integer.parseInt(categoryId);
        }

        if (brandId != null
                && !brandId.isEmpty()) {

            brand
                    = Integer.parseInt(brandId);
        }
        System.out.println("keyword = " + keyword);
        System.out.println("status = " + status);
        System.out.println("category = " + category);
        System.out.println("brand = " + brand);
        products
                = productService.filterProducts(
                        keyword,
                        status,
                        category,
                        brand);

        request.setAttribute(
                "products",
                products);

        request.setAttribute(
                "categories",
                categoryRepo.getAllCategories());

        request.setAttribute(
                "brands",
                brandRepo.getAllBrands());

        request.setAttribute(
                "keyword",
                keyword);

        request.getRequestDispatcher(
                "/admin/product/list.jsp")
                .forward(request, response);
    }
}
