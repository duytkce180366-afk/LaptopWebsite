/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.techstore.Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;

import java.io.IOException;

import com.mycompany.techstore.Models.Objects.Product;
import com.mycompany.techstore.Repositories.BrandRepository;
import repository.CategoryRepository;
import service.ProductService;

@WebServlet("/admin/product/create")
@MultipartConfig
public class ProductCreateController extends HttpServlet {

    private ProductService productService;
    private CategoryRepository categoryRepo;
    private BrandRepository brandRepo;

    @Override
    public void init() {

        productService
                = new ProductService();

        categoryRepo
                = new CategoryRepository();

        brandRepo
                = new BrandRepository();
    }

    
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute(
                "categories",
                categoryRepo.getAllCategories());

        request.setAttribute(
                "brands",
                brandRepo.getAllBrands());

        request.getRequestDispatcher(
                "/admin/product/create.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String categoryRaw
                    = request.getParameter("categoryId");

            String brandRaw
                    = request.getParameter("brandId");

            if (categoryRaw == null
                    || categoryRaw.isEmpty()
                    || brandRaw == null
                    || brandRaw.isEmpty()) {

                request.setAttribute(
                        "error",
                        "Please select category and brand");

                request.setAttribute(
                        "categories",
                        categoryRepo.getAllCategories());

                request.setAttribute(
                        "brands",
                        brandRepo.getAllBrands());

                request.getRequestDispatcher(
                        "/admin/product/create.jsp")
                        .forward(request, response);

                return;
            }
            Product p = new Product();
            p.setCategoryId(
                    Integer.parseInt(categoryRaw));

            p.setBrandId(
                    Integer.parseInt(brandRaw));
            p.setCategoryId(
                    Integer.parseInt(
                            request.getParameter("categoryId")));

            p.setBrandId(
                    Integer.parseInt(
                            request.getParameter("brandId")));

            p.setSku(
                    request.getParameter("sku"));

            p.setProductName(
                    request.getParameter("productName"));

            p.setDescription(
                    request.getParameter("description"));

            p.setPrice(
                    Double.parseDouble(
                            request.getParameter("price")));

            p.setStock(
                    Integer.parseInt(
                            request.getParameter("stock")));

            Part filePart
                    = request.getPart("image");

            String fileName
                    = filePart.getSubmittedFileName();

            if (fileName != null
                    && !fileName.isEmpty()) {

                String uploadPath
                        = getServletContext()
                                .getRealPath("/images");

                File uploadDir
                        = new File(uploadPath);

                if (!uploadDir.exists()) {

                    uploadDir.mkdirs();

                }

                filePart.write(
                        uploadPath
                        + File.separator
                        + fileName);

                p.setThumbnail(
                        "images/" + fileName);

            }

            p.setStatus(
                    request.getParameter("status"));

            String error
                    = productService.createProduct(p);

            if (error != null) {

                request.setAttribute(
                        "error",
                        error);

                request.setAttribute(
                        "product",
                        p);

                request.getRequestDispatcher(
                        "/admin/product/create.jsp")
                        .forward(request, response);

                return;
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/admin/products");

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute(
                    "error",
                    "Invalid data");

            request.getRequestDispatcher(
                    "/admin/product/create.jsp")
                    .forward(request, response);
        }
    }

}
