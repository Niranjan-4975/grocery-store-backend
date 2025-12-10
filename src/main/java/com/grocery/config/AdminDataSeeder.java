package com.grocery.config;

import com.grocery.auth.AccountStatus;
import com.grocery.auth.Role;
import com.grocery.auth.User;
import com.grocery.auth.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Collections;

@Component
public class AdminDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception{
        //1. Define the Admin Email
        String adminEmail = "support.admin@grocery.com";
        //2. Check if already present in system
        if (userRepository.findByEmail(adminEmail).isEmpty()){
            User admin = new User();
            //3. Set Admin Details
            admin.setFullName("Grocery SuperAdmin");
            admin.setEmail(adminEmail);
            admin.setMobile("9875643186");
            admin.setAddressLine("Pune Headquarters");
            admin.setCity("Pune");
            admin.setPinCode("412210");
            admin.setAccountStatus(AccountStatus.ACTIVE);
            //4. Critical Hash Password
            admin.setPasswordHash(passwordEncoder.encode("supportadmin@123"));
            //5. Assign Role
            admin.setRoles(Collections.singleton(Role.ROLE_ADMIN));
            //6. Save to DB
            userRepository.save(admin);
            System.out.println("---------------------------------------------");
            System.out.println("ADMIN SEEDER: Default Admin User Created Successfully");
            System.out.println("Email: " + adminEmail);
            System.out.println("Password: supportadmin@123");
            System.out.println("---------------------------------------------");
        }else {
            System.out.println("ADMIN SEEDER: Admin user already exists. Skipping.");
        }
    }
}
