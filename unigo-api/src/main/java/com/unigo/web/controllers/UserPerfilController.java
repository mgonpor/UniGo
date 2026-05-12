package com.unigo.web.controllers;

import com.unigo.service.PerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/perfil")
public class UserPerfilController {

    @Autowired
    private PerfilService perfilService;

    @GetMapping
    public ResponseEntity<?> getMiPerfil() {
        return ResponseEntity.ok(perfilService.getMiPerfil());
    }
}
