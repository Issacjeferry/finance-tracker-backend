package com.example.myproject.service;

import com.example.myproject.config.JwtUtil;
import com.example.myproject.dto.*;
import com.example.myproject.entity.User;
import com.example.myproject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final GoogleAuthService googleAuthService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil,
                       GoogleAuthService googleAuthService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.googleAuthService = googleAuthService;
    }

    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        User user = User.builder()
                .email(normalizedEmail)
                .name(request.getName() != null && !request.getName().isBlank() ? request.getName().trim() : normalizedEmail.split("@")[0])
                .password(encoder.encode(request.getPassword()))
                .authProvider("LOCAL")
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(mapToDTO(savedUser))
                .message("Registration successful")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (user.getPassword() == null) {
            throw new IllegalArgumentException("This account was created with Google. Please use the 'Continue with Google' button to sign in.");
        }

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(mapToDTO(user))
                .message("Login successful")
                .build();
    }

    public AuthResponse loginWithGoogle(String idToken) {
        GoogleAuthService.GoogleUserProfile profile = googleAuthService.verifyToken(idToken);
        String normalizedEmail = profile.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .map(existingUser -> {
                    if (existingUser.getName() == null || existingUser.getName().isBlank()) {
                        existingUser.setName(profile.getName());
                    }
                    if (existingUser.getPictureUrl() == null) {
                        existingUser.setPictureUrl(profile.getPictureUrl());
                    }
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(normalizedEmail)
                            .name(profile.getName())
                            .pictureUrl(profile.getPictureUrl())
                            .authProvider("GOOGLE")
                            .build();
                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(mapToDTO(user))
                .message("Google authentication successful")
                .build();
    }

    public UserDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .pictureUrl(user.getPictureUrl())
                .authProvider(user.getAuthProvider())
                .build();
    }
}