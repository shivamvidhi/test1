package com.tothenew.ecommerceapp.repositories;

import com.tothenew.ecommerceapp.entities.product.ProductVariation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductVariationRepo extends CrudRepository<ProductVariation,Long> {

    @Query(value = "select * from product_variation where product_id=:productId",nativeQuery = true)
    List<ProductVariation> getAll(@Param("productId") Long productId, Pageable pageable);

    @Query(value = "SELECT * FROM hibernate_sequence limit 1", nativeQuery = true)
    BigDecimal getNextValMySequence();

    @Query(value = "select max(price) from product_variation inner join product where product_variation.product_id=product.id AND product.category_id=:catId",nativeQuery = true)
    Optional<String> getMaxPrice(@Param("catId") Long catId);

    @Query(value = "select min(price) from product_variation inner join product where product_variation.product_id=product.id AND product.category_id=:catId",nativeQuery = true)
    Optional<String> getMinPrice(@Param("catId") Long catId);
}
