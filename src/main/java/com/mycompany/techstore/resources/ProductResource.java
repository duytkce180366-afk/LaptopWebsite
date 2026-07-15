package com.mycompany.techstore.resources;

import com.mycompany.techstore.Models.Objects.Product;
import com.mycompany.techstore.services.ProductService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    private final ProductService productService = new ProductService();

    // ==========================
    // Get all products (Pagination)
    // ==========================
    @GET
    public Response getAllProducts(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        List<Product> list = productService.getAll();

        int from = (page - 1) * size;

        if (from >= list.size()) {
            return Response.ok(new ArrayList<>()).build();
        }

        int to = Math.min(from + size, list.size());

        return Response.ok(list.subList(from, to)).build();
    }

    // ==========================
    // Get product by id
    // ==========================
    @GET
    @Path("/{id}")
    public Response getProductById(@PathParam("id") int id) {

        Product product = productService.getById(id);

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Product not found")
                    .build();
        }

        return Response.ok(product).build();
    }

    // ==========================
    // Search Product
    // ==========================
    @GET
    @Path("/search")
    public Response searchProducts(
            @QueryParam("keyword") String keyword,
            @QueryParam("category") String category,
            @QueryParam("minPrice") @DefaultValue("0") long minPrice,
            @QueryParam("maxPrice") @DefaultValue("999999999") long maxPrice) {

        List<Product> list = productService.search(
                keyword,
                category,
                minPrice,
                maxPrice,
                Map.of(),
                "recommended"
        );

        return Response.ok(list).build();
    }

    // ==========================
    // Create Product
    // ==========================
    @POST
    public Response createProduct(Product product) {

        boolean check = productService.createProduct(
                product.getCategoryNumericId(),
                product.getBrandId(),
                product.getSku(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getThumbnail(),
                product.getStatus()
        );

        if (check) {
            return Response.status(Response.Status.CREATED)
                    .entity("Create Product Successfully")
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Create Product Failed")
                .build();
    }

    // ==========================
    // Update Product
    // ==========================
    @PUT
    @Path("/{id}")
    public Response updateProduct(
            @PathParam("id") int id,
            Product product) {

        Product oldProduct = productService.getById(id);

        if (oldProduct == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Product not found")
                    .build();
        }

        boolean check = productService.updateProduct(
                id,
                product.getCategoryNumericId(),
                product.getBrandId(),
                product.getSku(),
                product.getProductName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getThumbnail(),
                product.getStatus()
        );

        if (check) {
            return Response.ok("Update Product Successfully").build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Update Product Failed")
                .build();
    }

    // ==========================
    // Delete Product
    // ==========================
    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") int id) {

        Product oldProduct = productService.getById(id);

        if (oldProduct == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Product not found")
                    .build();
        }

        boolean check = productService.deleteProduct(id);

        if (check) {
            return Response.ok("Delete Product Successfully").build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Delete Product Failed")
                .build();
    }
}
