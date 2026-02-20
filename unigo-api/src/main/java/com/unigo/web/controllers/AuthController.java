package com.unigo.web.controllers;

import com.unigo.service.dtos.LoginRequest;
import com.unigo.service.dtos.LoginResponse;
import com.unigo.service.dtos.RefreshDTO;
import com.unigo.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private LoginService loginService;

    // login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(this.loginService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, this.loginService.registrar(request)).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshDTO request) {
        return ResponseEntity.ok(this.loginService.refresh(request));
    }

}
