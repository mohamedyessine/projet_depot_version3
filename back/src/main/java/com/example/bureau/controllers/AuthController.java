package com.example.bureau.controllers;


import com.example.bureau.exceptions.EmailTakenException;
import com.example.bureau.exceptions.InvalidEmailException;
import com.example.bureau.exceptions.PasswordTooLongException;
import com.example.bureau.exceptions.UsernameTakenException;
import com.example.bureau.models.Depot;
import com.example.bureau.models.User;
import com.example.bureau.payload.request.LoginRequest;
import com.example.bureau.payload.request.SignupRequest;
import com.example.bureau.payload.response.ApiResponse;
import com.example.bureau.payload.response.JwtResponse;
import com.example.bureau.payload.response.MessageResponse;
import com.example.bureau.repo.RoleRepo;
import com.example.bureau.repo.UserRepo;
import com.example.bureau.security.jwt.JwtUtils;
import com.example.bureau.security.services.UserDetailsImpl;
import com.example.bureau.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {


   private final AuthService authService;

    private final AuthenticationManager authenticationManager;


    private final UserRepo userRepository;


    private final RoleRepo roleRepository;


    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserRepo userRepository, RoleRepo roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public List<User> getAllDepots() {
        return authService.findAllUser();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        authService.deleteUserById(userId);
        ApiResponse response = new ApiResponse("Utilisateur supprimé avec succès.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            authService.registerUser(signUpRequest);
            // Authenticate the newly registered user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signUpRequest.getUsername(), signUpRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));
        } catch (UsernameTakenException e) {
            return ResponseEntity.ok(new MessageResponse("Erreur: le nom d'utilisateur est déjà pris!"));
        } catch (EmailTakenException e) {
            return ResponseEntity.ok(new MessageResponse("Erreur: l'e-mail est déjà utilisé!"));
        } catch (InvalidEmailException e) {
            return ResponseEntity.ok(new MessageResponse("Erreur: l'e-mail doit être valide!"));
        } catch (PasswordTooLongException e) {
            return ResponseEntity.ok(new MessageResponse("Erreur: Le mot de passe doit être inférieur ou égal à 120caractères!"));
        } catch (Exception e) {
            return handleException(e);
        }
    }


    private ResponseEntity<?> handleException(Exception e) {
        String message = e.getMessage() != null ? e.getMessage() : "An error occurred while processing your request.";
        return ResponseEntity.badRequest().body(new MessageResponse(message));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.ok(new MessageResponse("L'utilisateur s'est déconnecté avec succès!"));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkUserExists(@RequestParam String username, @RequestParam String email) {
        boolean userExists = authService.isUserExists(username, email);
        return ResponseEntity.ok(userExists);
    }

    @GetMapping("/user-details")
    public ResponseEntity<UserDetailsImpl> getUserDetails(@RequestParam("token") String token) {
        try {
            UserDetailsImpl userDetails = authService.getUser(token);
            return ResponseEntity.ok(userDetails);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    // Helper method to map UserDetailsImpl to UserDetailsDto


}
