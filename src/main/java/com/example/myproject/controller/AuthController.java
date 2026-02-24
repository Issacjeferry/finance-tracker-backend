package com.example.myproject.controller;

import com.example.myproject.config.JwtUtil;
import com.example.myproject.entity.User;
import com.example.myproject.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService,
                          JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {

        User loggedUser = authService.login(user.getEmail(),
                user.getPassword());

        String token = jwtUtil.generateToken(loggedUser.getEmail());

        return Map.of("token", token);
    }
}