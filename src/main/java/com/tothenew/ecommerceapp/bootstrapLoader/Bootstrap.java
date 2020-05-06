package com.tothenew.ecommerceapp.bootstrapLoader;

import com.tothenew.ecommerceapp.entities.users.Admin;
import com.tothenew.ecommerceapp.entities.users.Role;
import com.tothenew.ecommerceapp.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class Bootstrap implements ApplicationRunner {

    @Autowired
    UserRepo userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if(userRepository.count()<1){

            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            Admin shivam = new Admin();
            shivam.setFirstName("Shivam");
            shivam.setLastName("Arora");
            shivam.setEmail("shivam@admin.com");
            shivam.setCreatedBy("Shivam");
            shivam.setDateCreated(new Date());
            shivam.setLastUpdated(new Date());
            shivam.setUpdatedBy("Shivam");
            shivam.setActive(true);
            shivam.setDeleted(false);
            shivam.setPassword(passwordEncoder.encode("pass"));

            Role role = new Role();
            role.setAuthority("ROLE_ADMIN");
            Role role1 = new Role();
            role1.setAuthority("ROLE_CUSTOMER");
            Set<Role> roleSet = new HashSet<>();
            roleSet.add(role);
            roleSet.add(role1);
            shivam.setRoles(roleSet);

            userRepository.save(shivam);
            System.out.println("Total users saved::"+userRepository.count());
        }
    }
}
