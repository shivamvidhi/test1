package com.tothenew.ecommerceapp.services;

import com.tothenew.ecommerceapp.dtos.CategoryDTO;
import com.tothenew.ecommerceapp.dtos.FilterCategoryDTO;
import com.tothenew.ecommerceapp.entities.category.Category;
import com.tothenew.ecommerceapp.exceptions.FieldAlreadyExistException;
import com.tothenew.ecommerceapp.exceptions.ResourceNotFoundException;
import com.tothenew.ecommerceapp.repositories.CategoryMetadataFieldValuesRepo;
import com.tothenew.ecommerceapp.repositories.CategoryRepo;
import com.tothenew.ecommerceapp.repositories.ProductRepo;
import com.tothenew.ecommerceapp.repositories.ProductVariationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class CategoryService {

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    CategoryMetadataFieldValuesRepo valuesRepo;

    @Autowired
    ProductVariationRepo productVariationRepo;

    public String addCategory(String name, Optional<Long> parentId) {
        Category category = new Category();
        if (parentId.isPresent()) {
            // not associated with any product
            if (!productRepo.findByCategoryId(parentId.get()).isEmpty()) {
                return "parent id is already associated with some product";
            }
            // find root categories
            List<Category> rootCategories = categoryRepo.findRootCategories();
            rootCategories.forEach(r->{
                if (r.getName().equals(name)) {
                    throw new FieldAlreadyExistException(name + " already a root category");
                }
            });



            List<Optional<Category>> immediateChildren = categoryRepo.findByParentId(parentId.get());
            System.out.println(immediateChildren);
            if (!immediateChildren.isEmpty()) {
                immediateChildren.forEach(ic->{
                    if (ic.get().getName().equals(name)) {
                        throw new FieldAlreadyExistException(name + " already in breadth");
                    }
                });
            }
            Optional<Category> parentCategory = categoryRepo.findById(parentId.get());
            if (getCategoryNameTillRoot(parentCategory.get()).contains(name)) {
                throw new FieldAlreadyExistException(name + " already in depth");
            }
            category.setName(name);
            category.setParentId(categoryRepo.findById(parentId.get()).get());
            categoryRepo.save(category);
            return "Success " + categoryRepo.findByNameAndParentId(name,parentId.get()).getId();

        }
        if (!parentId.isPresent()) {
            if (categoryRepo.findByName(name) != null) {
            throw new FieldAlreadyExistException(name + " category already exist");
            }
            category.setName(name);
            categoryRepo.save(category);
            return "Success " + categoryRepo.findByName(name).getId();
        }
        return "Success" + categoryRepo.findByNameAndParentId(name,parentId.get()).getId();
    }

    @Transactional
    public String deleteCategory(Long id) {
        if (!categoryRepo.findById(id).isPresent()) {
            throw new ResourceNotFoundException(id + " category does not exist");
        }
        if (!productRepo.findByCategoryId(id).isEmpty()) {
            return "id is associated with some product, cannot delete";
        }
        if (!categoryRepo.findByParentId(id).isEmpty()) {
            return "id is a parent category, cannot delete";
        }
        categoryRepo.deleteById(id);
        return "Success";
    }

    public String updateCategory(String name,Long id) {
        if (!categoryRepo.findById(id).isPresent()) {
            throw new ResourceNotFoundException(id + " category does not exist");
        }
        List<Category> rootCategories = categoryRepo.findRootCategories();
        rootCategories.forEach(r->{
            if (r.getName().equals(name)) {
                throw new FieldAlreadyExistException(name + " already a root category");
            }
        });
        List<Optional<Category>> immediateChildren = categoryRepo.findByParentId(id);
        System.out.println(immediateChildren);
        if (!immediateChildren.isEmpty()) {
            immediateChildren.forEach(ic->{
                if (ic.get().getName().equals(name)) {
                    throw new FieldAlreadyExistException(name + " already in breadth");
                }
            });
        }
        Optional<Category> parentCategory = categoryRepo.findById(id);
        if (getCategoryNameTillRoot(parentCategory.get()).contains(name)) {
            throw new FieldAlreadyExistException(name + " already in depth");
        }
        Optional<Category> category = categoryRepo.findById(id);
        Category updateCategory = category.get();
        updateCategory.setName(name);
        categoryRepo.save(updateCategory);
        return "Success";
    }

    public CategoryDTO viewCategory(Long id) {
        if (!categoryRepo.findById(id).isPresent()) {
            throw new ResourceNotFoundException(id + " category does not exist");
        }
        CategoryDTO categoryDTO = new CategoryDTO();
        Optional<Category> category = categoryRepo.findById(id);
        try {
            List<Object[]> categoryFieldValues = valuesRepo.findCategoryMetadataFieldValuesById(id);
            Set<HashMap<String,String>> filedValuesSet = new HashSet<>();
            categoryFieldValues.forEach(c->{
                HashMap fieldValueMap = new HashMap<>();
                List<Object> arr = Arrays.asList(c);
                for (int i=0;i<arr.size();i++) {
                    fieldValueMap.put(arr.get(0),arr.get(i));
                }
                filedValuesSet.add(fieldValueMap);
            });
            List<Optional<Category>> childrenCategory = categoryRepo.findByParentId(id);
            Set<Category> childrenCategorySet = new HashSet<>();
            childrenCategory.forEach(c->{
                childrenCategorySet.add(c.get());
            });
            categoryDTO.setCategory(category.get());
            categoryDTO.setChildCategory(childrenCategorySet);
            categoryDTO.setFiledValuesSet(filedValuesSet);
        }catch (Exception ex) {}

        return categoryDTO;
    }

    public List<CategoryDTO> viewCategories(String page, String size, String sortBy, String order,Optional<String> query) {
        if (query.isPresent()) {
            Optional<Category> category = categoryRepo.findById(Long.parseLong(query.get()));
            List<CategoryDTO> categoryDTOS = new ArrayList<>();
            categoryDTOS.add(viewCategory(category.get().getId()));
            return categoryDTOS;
        }

        List<Category> categories = categoryRepo.findAll(PageRequest.of(Integer.parseInt(page),Integer.parseInt(size),Sort.by(Sort.Direction.fromString(order),sortBy)));
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        categories.forEach(c-> {
            categoryDTOS.add(viewCategory(c.getId()));
        });
        return categoryDTOS;
    }


    public List<CategoryDTO> viewLeafCategories() {
        List<Object> leafCategoryIds = categoryRepo.findLeafCategories();
        List<Object> categoryIds = categoryRepo.findCategoryId();
        categoryIds.removeAll(leafCategoryIds);
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        for (Object o : categoryIds) {
            CategoryDTO categoryDTO = viewCategory(Long.parseLong(o.toString()));
            categoryDTOS.add(categoryDTO);
        }
        return categoryDTOS;

    }

    public List<Category> viewCategoriesSameParent(Optional<Long> categoryId) {
        if (categoryId.isPresent()) {
            if (!categoryRepo.findById(categoryId.get()).isPresent()) {
                throw new ResourceNotFoundException(categoryId.get() + " category does not exist");
            }
            List<Optional<Category>> childrenCategory = categoryRepo.findByParentId(categoryId.get());
            List<Category> childrenCategoryList = new ArrayList<>();
            childrenCategory.forEach(c->{
                childrenCategoryList.add(c.get());
            });
            return childrenCategoryList;
        }
        List<Category> categories = categoryRepo.findRootCategories();

        return categories;
    }

    public List<?> filterCategory(Long categoryId) {
        if (!categoryRepo.findById(categoryId).isPresent()) {
            throw new ResourceNotFoundException(categoryId + " category does not exist");
        }
        List<FilterCategoryDTO> categoryDTOS = new ArrayList<>();
        List<Long> leafCategories = categoryRepo.getParentCategories();
        System.out.println(leafCategories);
        if (leafCategories.contains(categoryId)) {
            // not a leaf category
            List<Optional<Category>> immediateChildren = categoryRepo.findByParentId(categoryId);
            immediateChildren.forEach(c->{
                FilterCategoryDTO filterCategoryDTO = filterCategoryProvider(categoryId);
                categoryDTOS.add(filterCategoryDTO);
            });
        }
        if (!leafCategories.contains(categoryId)) {
            // leaf category
            FilterCategoryDTO filterCategoryDTO = filterCategoryProvider(categoryId);
            categoryDTOS.add(filterCategoryDTO);
        }
        return categoryDTOS;
    }

    private FilterCategoryDTO filterCategoryProvider(Long id) {
        List<Object[]> categoryFieldValues = valuesRepo.findCategoryMetadataFieldValuesById(id);
        Set<HashMap<String,String>> filedValuesSet = new HashSet<>();
        categoryFieldValues.forEach(c->{
            HashMap fieldValueMap = new HashMap<>();
            List<Object> arr = Arrays.asList(c);
            for (int i=0;i<arr.size();i++) {
                fieldValueMap.put(arr.get(0),arr.get(i));
            }
            filedValuesSet.add(fieldValueMap);
        });
        FilterCategoryDTO filterCategoryDTO = new FilterCategoryDTO();
        filterCategoryDTO.setFiledValuesSet(filedValuesSet);
        filterCategoryDTO.setBrands(productRepo.getBrandsOfCategory(id));
        Optional<String> minPrice = productVariationRepo.getMinPrice(id);
        if (minPrice.isPresent()) {
            filterCategoryDTO.setMinPrice(minPrice.get());
        }
        Optional<String> maxPrice = productVariationRepo.getMaxPrice(id);
        if (maxPrice.isPresent()) {
            filterCategoryDTO.setMinPrice(maxPrice.get());
        }
        return filterCategoryDTO;
    }


     private List<String> getCategoryNameTillRoot(Category category){
        List<String> categoryNameTillRoot = new ArrayList<>();
        categoryNameTillRoot.add(category.getName());
        while (category.getParentId() != null) {
            category = category.getParentId();
            categoryNameTillRoot.add(category.getName());
        }
        return categoryNameTillRoot;
    }
}
