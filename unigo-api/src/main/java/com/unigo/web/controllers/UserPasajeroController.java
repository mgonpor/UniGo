package com.unigo.web.controllers;

import com.unigo.service.PasajeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/pasajeros")
public class UserPasajeroController {

    @Autowired
    private PasajeroService pasajeroService;

    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> getPasajeroByIdUsuario(@PathVariable int idUsuario){
        return ResponseEntity.ok(this.pasajeroService.getPasajeroByIdUsuario(idUsuario));
    }
}
