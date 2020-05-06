package com.tothenew.ecommerceapp.repositories;

import com.tothenew.ecommerceapp.entities.category.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface CategoryRepo extends CrudRepository<Category,Long> {

    Category findByName(String name);

    @Query(value = "select * from category where parent_id=:id",nativeQuery = true)
    List<Optional<Category>> findByParentId(@Param("id") Long id);

    List<Category> findAll(Pageable pageable);

    @Query(value = "select parent_id from category",nativeQuery = true)
    List<Object> findLeafCategories();

    @Query(value = "select id from category",nativeQuery = true)
    List<Object> findCategoryId();

    @Query(value = "select * from category where parent_id is null",nativeQuery = true)
    List<Category> findRootCategories();

    @Query(value = "select * from category where name=:name AND parent_id=:parentId",nativeQuery = true)
    Category findByNameAndParentId(@Param("name") String name,Long parentId);

    @Transactional
    @Modifying
    @Query(value = "delete from category where id=:id",nativeQuery = true)
    void deleteById(@Param("id") Long id);

    @Query(value = "select parent_id from category",nativeQuery = true)
    List<Long> getParentCategories();
}
