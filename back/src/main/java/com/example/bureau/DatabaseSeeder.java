package com.example.bureau;

import com.example.bureau.models.ERole;
import com.example.bureau.models.Role;
import com.example.bureau.models.User;
import com.example.bureau.repo.RoleRepo;
import com.example.bureau.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

//@Component
public class DatabaseSeeder implements ApplicationRunner {
      private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseSeeder(UserRepo userRepo, RoleRepo roleRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Create roles if they don't exist
        createRoleIfNotExists(ERole.ROLE_USER);
        createRoleIfNotExists(ERole.ROLE_ADMIN);

        if (!userRepo.existsByUsername("admin")) {
            Role adminRole = roleRepo.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Admin role not found."));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            if (!userRepo.existsByEmail("admin@example.com")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("yessine07"));
                admin.setRoles(roles);
                userRepo.save(admin);
            }
        }
    }

    private void createRoleIfNotExists(ERole roleName) {
        if (!roleRepo.existsByName(roleName)) {
            Role role = new Role(roleName);
            roleRepo.save(role);
        }
    }
}
