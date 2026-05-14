package com.unigo.web.controllers;

import com.unigo.service.ValoracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/valoraciones")
public class UserValoracionController {

    @Autowired
    private ValoracionService valoracionService;

    @GetMapping("/recibidas")
    public ResponseEntity<?> getRecibidas() {
        return ResponseEntity.ok(valoracionService.getRecibidas());
    }

    @GetMapping("/dadas")
    public ResponseEntity<?> getDadas() {
        return ResponseEntity.ok(valoracionService.getDadas());
    }
}
