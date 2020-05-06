package com.tothenew.ecommerceapp.services;

import com.tothenew.ecommerceapp.dtos.CategoryMetadataDTO;
import com.tothenew.ecommerceapp.entities.category.Category;
import com.tothenew.ecommerceapp.entities.category.CategoryMetadataField;
import com.tothenew.ecommerceapp.entities.category.CategoryMetadataFieldValues;
import com.tothenew.ecommerceapp.exceptions.ResourceNotFoundException;
import com.tothenew.ecommerceapp.repositories.CategoryMetadataFieldRepo;
import com.tothenew.ecommerceapp.repositories.CategoryMetadataFieldValuesRepo;
import com.tothenew.ecommerceapp.repositories.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryMetadataService {

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    CategoryMetadataFieldRepo metadataRepo;

    @Autowired
    CategoryMetadataFieldValuesRepo valuesRepo;

    public String addCategoryMetadata(CategoryMetadataDTO categoryMetadataDTO) {
        Optional<Category> category = categoryRepo.findById(categoryMetadataDTO.getCategoryId());
        if (!category.isPresent()) {
            throw new ResourceNotFoundException(categoryMetadataDTO.getCategoryId() + " category does not exist");
        }
        HashMap<String, HashSet<String>> filedIdValues = categoryMetadataDTO.getFiledIdValues();
        Set<String> metadataIds = filedIdValues.keySet();
        metadataIds.forEach(id->{
            Optional<CategoryMetadataField> metadata = metadataRepo.findById(Long.parseLong(id));
            if (!metadata.isPresent()) {
                throw new ResourceNotFoundException(id + " metadata filed does not exist");
            }
        });
        metadataIds.forEach(id->{
            if (filedIdValues.get(id).isEmpty()) {
                throw new ResourceNotFoundException("any one filed does not have values");
            }
        });
        CategoryMetadataFieldValues fieldValues = new CategoryMetadataFieldValues();
        fieldValues.setCategory(category.get());
        metadataIds.forEach(id->{
            Optional<CategoryMetadataField> metadata = metadataRepo.findById(Long.parseLong(id));
            fieldValues.setCategoryMetadataField(metadata.get());
            HashSet<String> values = filedIdValues.get(id);
            String value= String.join(",",values);
            fieldValues.setValue(value);
            metadata.get().getCategoryMetadataFieldValues().add(fieldValues);
            metadataRepo.save(metadata.get());
        });
        return "Success";
    }

    public String updateCategory(CategoryMetadataDTO categoryMetadataDTO) {
        Optional<Category> category = categoryRepo.findById(categoryMetadataDTO.getCategoryId());
        if (!category.isPresent()) {
            throw new ResourceNotFoundException(categoryMetadataDTO.getCategoryId() + " category does not exist");
        }
        HashMap<String, HashSet<String>> filedIdValues = categoryMetadataDTO.getFiledIdValues();
        Set<String> metadataIds = filedIdValues.keySet();
        metadataIds.forEach(id->{
            Optional<CategoryMetadataField> metadata = metadataRepo.findById(Long.parseLong(id));
            if (!metadata.isPresent()) {
                throw new ResourceNotFoundException(id + " metadata filed does not exist");
            }
            Optional<CategoryMetadataFieldValues> associationSet = valuesRepo.findByMetadataId(categoryMetadataDTO.getCategoryId(),Long.parseLong(id));
            if (!associationSet.isPresent()) {
                throw new ResourceNotFoundException("metadata filed is not associated with any category");
            }
            String value= String.join(",",categoryMetadataDTO.getFiledIdValues().get(id));
            associationSet.get().setValue(value);
            metadata.get().getCategoryMetadataFieldValues().add(associationSet.get());
            metadataRepo.save(metadata.get());
        });
        return "Success";
    }
}
