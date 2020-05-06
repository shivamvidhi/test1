package com.tothenew.ecommerceapp.entities.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tothenew.ecommerceapp.dtos.CategoryDTO;
import com.tothenew.ecommerceapp.entities.product.Product;
import com.tothenew.ecommerceapp.entities.utils.AuditingInformation;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Embedded
    private AuditingInformation auditingInformation;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Category parentId;

    @OneToMany(mappedBy = "parentId",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Set<Category> childrenCategories;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Set<Product> products;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Set<CategoryMetadataFieldValues> categoryMetadataFieldValues;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParentId() {
        return parentId;
    }

    public void setParentId(Category parentId) {
        this.parentId = parentId;
    }

    @JsonIgnore
    public AuditingInformation getAuditingInformation() {
        return auditingInformation;
    }

    public void setAuditingInformation(AuditingInformation auditingInformation) {
        this.auditingInformation = auditingInformation;
    }

    @JsonIgnore
    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    @JsonIgnore
    public Set<CategoryMetadataFieldValues> getCategoryMetadataFieldValues() {
        return categoryMetadataFieldValues;
    }

    public void setCategoryMetadataFieldValues(Set<CategoryMetadataFieldValues> categoryMetadataFieldValues) {
        this.categoryMetadataFieldValues = categoryMetadataFieldValues;
    }

    @JsonIgnore
    public Set<Category> getChildrenCategories() {
        return childrenCategories;
    }

    public void setChildrenCategories(Set<Category> childrenCategories) {
        this.childrenCategories = childrenCategories;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", auditingInformation=" + auditingInformation +
                ", parentId=" + parentId +
                ", products=" + products +
                ", categoryMetadataFieldValues=" + categoryMetadataFieldValues +
                '}';
    }
}
