package com.example.bureau.services;

import com.example.bureau.exceptions.EmailTakenException;
import com.example.bureau.exceptions.InvalidEmailException;
import com.example.bureau.exceptions.PasswordTooLongException;
import com.example.bureau.exceptions.UsernameTakenException;
import com.example.bureau.models.ERole;
import com.example.bureau.models.Role;
import com.example.bureau.models.User;
import com.example.bureau.payload.request.SignupRequest;
import com.example.bureau.repo.RoleRepo;
import com.example.bureau.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private final UserRepo userRepo;

    private final RoleRepo roleRepo;

    private final PasswordEncoder encoder;

    public AuthService(UserRepo userRepo, RoleRepo roleRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    public void registerUser(SignupRequest signUpRequest) throws UsernameTakenException, EmailTakenException, InvalidEmailException, PasswordTooLongException {
        if (userRepo.existsByUsername(signUpRequest.getUsername())) {
            throw new UsernameTakenException();
        }

        if (userRepo.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailTakenException();
        }

        if (!isValidEmail(signUpRequest.getEmail())) {
            throw new InvalidEmailException();
        }

        if (signUpRequest.getPassword().length() > 120) {
            throw new PasswordTooLongException();
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepo.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepo.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepo.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepo.save(user);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public List<User> findAllUser(){
        return userRepo.findAll();
    }

    public void deleteUserById(Long userId) {
        userRepo.deleteById(userId);
    }

}

