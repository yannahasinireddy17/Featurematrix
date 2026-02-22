package com.productcompare.controller;

import com.productcompare.dto.AuthRequest;
import com.productcompare.dto.AuthResponse;
import com.productcompare.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public AuthResponse me(@RequestHeader("X-Auth-Token") String token) {
        return authService.me(token);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("X-Auth-Token") String token) {
        authService.logout(token);
    }
}
