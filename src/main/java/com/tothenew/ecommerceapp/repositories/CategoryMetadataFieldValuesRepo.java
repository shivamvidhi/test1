package com.tothenew.ecommerceapp.repositories;

import com.tothenew.ecommerceapp.entities.category.CategoryMetadataFieldValues;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CategoryMetadataFieldValuesRepo extends CrudRepository<CategoryMetadataFieldValues,Long> {

    @Query(value = "select * from category_metadata_field_values where category_id=:cId AND category_metadata_field_id=:mId",nativeQuery = true)
    Optional<CategoryMetadataFieldValues> findByMetadataId(@Param("cId") Long cId,@Param("mId") Long mId);

    @Query(value = "select category_metadata_field.name,category_metadata_field_values.value from category_metadata_field_values inner join category_metadata_field on category_metadata_field_values.category_metadata_field_id = category_metadata_field.id AND category_metadata_field_values.category_id=:id",nativeQuery = true)
    List<Object[]> findCategoryMetadataFieldValuesById(@Param("id") Long id);
}
