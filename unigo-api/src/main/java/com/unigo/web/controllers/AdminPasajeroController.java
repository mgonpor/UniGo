package com.unigo.web.controllers;

import com.unigo.service.dtos.PasajeroResponse;
import com.unigo.service.PasajeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/pasajeros")
public class AdminPasajeroController {

    @Autowired
    private PasajeroService pasajeroService;

    @GetMapping
    public ResponseEntity<List<PasajeroResponse>> getPasajeros(){
        return ResponseEntity.ok(pasajeroService.getPasajeros());
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> getPasajeroByIdUsuario(@PathVariable int idUsuario){
        return ResponseEntity.ok(pasajeroService.getPasajeroByIdUsuario(idUsuario));
    }

    @PostMapping("/{idUsuario}")
    public ResponseEntity<?> createPasajero(@PathVariable int idUsuario) {
        return ResponseEntity.ok(pasajeroService.createPasajero(idUsuario));
    }

}
