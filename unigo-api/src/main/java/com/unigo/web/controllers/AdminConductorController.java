package com.unigo.web.controllers;

import com.unigo.service.dtos.ConductorResponse;
import com.unigo.service.ConductorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/conductores")
public class AdminConductorController {

    @Autowired
    private ConductorService conductorService;

    // CONDUCTOR ADMIN
    @GetMapping
    public ResponseEntity<List<ConductorResponse>> findAll() {
        return ResponseEntity.ok(conductorService.findAll());
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> getConductorByIdUsuario(@PathVariable int idUsuario) {
        return ResponseEntity.ok(conductorService.findByIdUsuario(idUsuario));
    }

    @PostMapping("/{idUsuario}")
    public ResponseEntity<?> createConductor(@PathVariable int idUsuario) {
        return ResponseEntity.ok(conductorService.create(idUsuario));
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<?> updateConductor(@PathVariable int idUsuario, @RequestParam int idConductor,
            @RequestParam float reputacion) {
        return ResponseEntity.ok(conductorService.updateReputacion(idUsuario, idConductor, reputacion));
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<?> deleteConductor(@PathVariable int idUsuario) {
        return ResponseEntity.ok(conductorService.delete(idUsuario));
    }

    // ADMIN Y USER
    @GetMapping("/searchByReputacion")
    public ResponseEntity<?> findByReputacionGreaterThanEqual(@RequestParam float mayorQue) {
        return ResponseEntity.ok(conductorService.findByReputacionGreaterThanEqual(mayorQue));
    }

}
