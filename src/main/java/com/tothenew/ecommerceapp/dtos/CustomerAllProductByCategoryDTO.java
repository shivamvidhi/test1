package com.tothenew.ecommerceapp.dtos;

import com.tothenew.ecommerceapp.entities.product.Product;

import java.util.List;

public class CustomerAllProductByCategoryDTO {
    private List<Product> products;
    private String image;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
