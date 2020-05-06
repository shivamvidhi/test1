package com.tothenew.ecommerceapp;

import com.tothenew.ecommerceapp.entities.product.ProductVariation;
import com.tothenew.ecommerceapp.entities.users.*;
import com.tothenew.ecommerceapp.entities.users.Seller;
import com.tothenew.ecommerceapp.repositories.ProductVariationRepo;
import com.tothenew.ecommerceapp.repositories.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SpringBootTest
class EcommerceAppApplicationTests {

    @Autowired
    UserRepo userRepo;

    @Autowired
    ProductVariationRepo productVariationRepo;

//    @Test
//    void testJsonMetadataInProductVariation() {
//        ProductVariation productVariation = new ProductVariation();
//        productVariation.setPrice(1000L);
//        productVariation.setPrimaryImageName("some iamge path");
//        productVariation.setQuantityAvailable(100L);
//        Map<String,Object> map = new HashMap<>();
//        map.put("ram","16gb");
//        map.put("battery","4000mah");
//        productVariation.setMetadata(map);
//
//
//        productVariationRepo.save(productVariation);
//    }


    @Test
    void createAdminUser() {
        Admin admin = new Admin();
        admin.setFirstName("Shiva");
        admin.setLastName("Tiwari");
        admin.setEmail("shiva@tiwari.com");
//        admin.setPhoneNumber(100);
        admin.setPassword("admin");
//        admin.setDateOfJoining(new Date());

        // ADMIN SPECIFIC ATTRIBUTE
//        admin.setAdminTask("Give Permissions");

        // DEFINE ROLE FOR ADMIN
        Role role = new Role();
        role.setAuthority("ROLE_ADMIN");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        // ADDING ROLES IN ADMIN
        admin.setRoles(roleSet);

        // SAVE ADMIN
        userRepo.save(admin);
    }

    @Test
    void createSellerUser() {
        Seller seller = new Seller();
        seller.setFirstName("Rishab");
        seller.setLastName("Singh");
        seller.setEmail("rishabh.singh@gmail.com");
        seller.setPassword("rishabSingh123");
//        seller.setPhoneNumber(9898989);
//        seller.setDateOfJoining(new Date());

        // DEFINE ROLE FOR SELLER
        Role role = new Role();
        role.setAuthority("ROLE_SELLER");
        Role role1 = new Role();
        role1.setAuthority("ROLE_SELLER_TEST");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        roleSet.add(role1);


        // ADDING ROLES IN ADMIN
        seller.setRoles(roleSet);

        // SELLER SPECIFIC ATTRIBUTES
//        seller.setTotalProducts(12);
//        seller.setType("Fashion Seller");

        // SAVING SELLER
        userRepo.save(seller);
    }

    @Test
    void createBuyerUser() {
        Customer customerRohit = new Customer();
        customerRohit.setFirstName("Rohit");
        customerRohit.setLastName("Sharma");
        customerRohit.setEmail("rohit.sharma@gmail.com");
//        customerRohit.setPhoneNumber(1233121);
        customerRohit.setPassword("rohitSharma123");
//        customerRohit.setDateOfJoining(new Date());

        // DEFINING ROLE TO BUYER
        Role role = new Role();
        role.setAuthority("ROLE_BUYER");
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        // ADDING ROLES IN ADMIN
        customerRohit.setRoles(roleSet);

        // DEFINE ADDRESS FOR ROHIT (SPECIFIC TO BUYER)
        Address addressRohit1 = new Address();
        addressRohit1.setCountry("Street 1");
        addressRohit1.setCity("Mumbai");
        addressRohit1.setZipCode(100190);
        addressRohit1.setAddress("Take left from crossover");
        addressRohit1.setState("Maharashtra");
        addressRohit1.setUser(customerRohit);
        Address addressRohit2 = new Address();
        addressRohit2.setCountry("Street 2");
        addressRohit2.setCity("Pune");
        addressRohit2.setZipCode(204190);
        addressRohit2.setAddress("Take left from crossover");
        addressRohit2.setState("Maharashtra");
        addressRohit2.setUser(customerRohit);
        Set<Address> addressSetRohit = new HashSet<>();
        addressSetRohit.add(addressRohit1);
        addressSetRohit.add(addressRohit2);

        customerRohit.setAddresses(addressSetRohit);

        // SAVING ROHIT
        userRepo.save(customerRohit);
    }



}
