package com.tothenew.ecommerceapp.controllers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tothenew.ecommerceapp.dtos.AddressDTO;
import com.tothenew.ecommerceapp.dtos.CustomerProfileDTO;
import com.tothenew.ecommerceapp.entities.category.Category;
import com.tothenew.ecommerceapp.services.CategoryService;
import com.tothenew.ecommerceapp.services.CustomerProfileService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/customer/profile")
public class CustomerController {

    @Autowired
    CustomerProfileService customerProfileService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    public CustomerProfileDTO viewProfile(HttpServletRequest request) throws IOException {
        CustomerProfileDTO customerProfileDTO = modelMapper.map(customerProfileService.viewProfile(request),CustomerProfileDTO.class);
        File f = new File("/home/shiva/Documents/javaPrograms/ecommerce-app/src/main/resources/static/users");
        File[] matchingFiles = new File[2];
        System.out.println(matchingFiles.length);
        try {
            matchingFiles = f.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(customerProfileDTO.getId().toString());
                }
            });
        }
        catch (Exception ex) {}
        if (matchingFiles.length>0) {
            File file = new File(matchingFiles[0].toString());
            System.out.println(file);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String encodedFile = new String(Base64.encodeBase64(fileContent), "UTF-8");
            customerProfileDTO.setImage(encodedFile);
        }

        return customerProfileDTO;
    }

    @PostMapping("")
    public String updateProfile(@RequestBody CustomerProfileDTO customerProfileDTO, HttpServletRequest request, HttpServletResponse response) {

        String getMessage = customerProfileService.updateCustomer(customerProfileDTO,request);
        if ("Success".contentEquals(getMessage)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return getMessage;
    }

    @PutMapping("/updatePassword")
    public String updatePassword(@RequestParam String pass,@RequestParam String cPass,HttpServletRequest request,HttpServletResponse response) {
        String getMessage = customerProfileService.updatePassword(pass,cPass,request);
        if ("Success".contentEquals(getMessage)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return getMessage;
    }

    @GetMapping("/addresses")
    public Set<AddressDTO> viewAddresses(HttpServletRequest request) {
        return customerProfileService.viewAddress(request);
    }

    @PostMapping("/address")
    public String newAddress(@RequestBody AddressDTO addressDTO, HttpServletRequest request, HttpServletResponse response) {
        String getMessage = customerProfileService.newAddress(addressDTO,request);
        if ("Success".contentEquals(getMessage)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return getMessage;
    }

    @DeleteMapping("/address/{id}")
    public String deleteAddress(@PathVariable Long id,HttpServletResponse response,HttpServletRequest request) {
        String getMessage = customerProfileService.deleteAddress(id,request);
        if ("Success".contentEquals(getMessage)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return getMessage;
    }

    @PutMapping("/updateAddress/{id}")
    public String updateAddress(@PathVariable Long id,@RequestBody AddressDTO addressDTO,HttpServletResponse response,HttpServletRequest request) {
        String getMessage = customerProfileService.updateAddress(id,addressDTO,request);
        if ("Success".contentEquals(getMessage)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return getMessage;
    }

    @GetMapping("/categories")
    public List<Category> viewLeafCategories(@RequestParam Optional<Long> categoryId) {
        return categoryService.viewCategoriesSameParent(categoryId);
    }

    @GetMapping("/filterCategories/{categoryId}")
    public List<?> filterCategory(@PathVariable Long categoryId) {
        return categoryService.filterCategory(categoryId);
    }
}
