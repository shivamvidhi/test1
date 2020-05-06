package com.tothenew.ecommerceapp.controllers;

import com.tothenew.ecommerceapp.dtos.CustomerAllProductByCategoryDTO;
import com.tothenew.ecommerceapp.dtos.CustomerProductViewByIdDTO;
import com.tothenew.ecommerceapp.dtos.ProductDTO;
import com.tothenew.ecommerceapp.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/add")
    public String addProduct(@RequestParam("name") String name, @RequestParam("brand") String brand, @RequestParam("categoryId") Long categoryId, @RequestParam("desc") Optional<String> desc, @RequestParam(name = "isCancellable") Optional<Boolean> isCancellable, @RequestParam(name = "isReturnable") Optional<Boolean> isReturnable, HttpServletResponse response, HttpServletRequest request) {
        String getMessage = productService.addProduct(request,name,brand,categoryId,desc,isCancellable,isReturnable);
        if ("Success".contentEquals(getMessage)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return getMessage;
    }

    @GetMapping("/view/{id}")
    public ProductDTO viewProduct(@PathVariable Long id, HttpServletRequest request) {
        return productService.viewProduct(id,request);
    }

    @GetMapping("/view/all")
    public List<?> viewAllProduct(HttpServletRequest request, @RequestParam(defaultValue = "0") String page, @RequestParam(defaultValue = "10") String size, @RequestParam(defaultValue = "id") String SortBy, @RequestParam(defaultValue = "ASC") String order, @RequestParam Optional<String> query) {
        return productService.viewAllProducts(request,page,size,SortBy,order,query);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProductById(@PathVariable Long id,HttpServletRequest request,HttpServletResponse response) {
        String getMessage = productService.deleteProductById(id,request);
        if ("Success".contentEquals(getMessage)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return getMessage;
    }

    @PutMapping("/update/{id}")
    public String updateProductById(@PathVariable Long id,@RequestParam Optional<String> name,@RequestParam Optional<String> desc, @RequestParam Optional<Boolean> isCancellable, @RequestParam Optional<Boolean> isReturnable,HttpServletResponse response,HttpServletRequest request) {
        String getMessage = productService.updateProductById(request,id,name,desc,isCancellable,isReturnable);
        if ("Success".contentEquals(getMessage)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return getMessage;
    }

    @GetMapping("/customer/{productId}")
    public CustomerProductViewByIdDTO viewProductCustomer(@PathVariable Long productId) throws IOException {
        return productService.viewProductCustomer(productId);
    }

    @GetMapping("/customer/all/{categoryId}")
    public CustomerAllProductByCategoryDTO viewAllProductOfCategory(@PathVariable Long categoryId, @RequestParam(defaultValue = "0") String page, @RequestParam(defaultValue = "10") String size, @RequestParam(defaultValue = "id") String SortBy, @RequestParam(defaultValue = "ASC") String order) {
       return productService.viewAllProductsOfCategory(categoryId,page,size,SortBy,order);
    }

    @GetMapping("/customer/similar/{productId}")
    public CustomerAllProductByCategoryDTO viewAllSimilarProducts(@PathVariable Long productId,@RequestParam(defaultValue = "0") String page, @RequestParam(defaultValue = "10") String size, @RequestParam(defaultValue = "id") String SortBy, @RequestParam(defaultValue = "ASC") String order) {
        return productService.viewAllSimilarProducts(productId,page,size,SortBy,order);
    }

    @GetMapping("/admin/{productId}")
    public CustomerProductViewByIdDTO viewProductAdmin(@PathVariable Long productId) throws IOException {
        return productService.viewProductAdmin(productId);
    }

    @GetMapping("/admin/all")
    public CustomerAllProductByCategoryDTO viewAllProductsAdmin( @RequestParam(defaultValue = "0") String page, @RequestParam(defaultValue = "10") String size, @RequestParam(defaultValue = "id") String SortBy, @RequestParam(defaultValue = "ASC") String order,@RequestParam(name = "query") Optional<Long> query) {
        return productService.viewAllProductsAdmin(page,size,SortBy,order,query);
    }

    @PutMapping("/admin/activate/{productId}")
    public String activateProduct(@PathVariable Long productId,HttpServletResponse response) {
        String getMessage = productService.activateProduct(productId);
        if ("Success".contentEquals(getMessage)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return getMessage;
    }

    @PutMapping("/admin/deactivate/{productId}")
    public String deactivateProduct(@PathVariable Long productId,HttpServletResponse response) {
        String getMessage = productService.deactivateProduct(productId);
        if ("Success".contentEquals(getMessage)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return getMessage;
    }
}
