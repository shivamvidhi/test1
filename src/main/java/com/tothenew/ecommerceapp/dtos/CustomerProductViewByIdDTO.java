package com.tothenew.ecommerceapp.dtos;

import com.tothenew.ecommerceapp.entities.product.Product;

public class CustomerProductViewByIdDTO {
    private Product product;
    private ProductVarPlusImagesDTO productVarPlusImagesDTO;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductVarPlusImagesDTO getProductVarPlusImagesDTO() {
        return productVarPlusImagesDTO;
    }

    public void setProductVarPlusImagesDTO(ProductVarPlusImagesDTO productVarPlusImagesDTO) {
        this.productVarPlusImagesDTO = productVarPlusImagesDTO;
    }
}





