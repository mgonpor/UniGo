package com.unigo.web.controllers;

import com.unigo.security.user.Usuario;
import com.unigo.service.dtos.PasajeroResponse;
import com.unigo.service.exceptions.DuplicateResourceException;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import com.unigo.service.services.PasajeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pasajeros")
public class PasajeroController {

    @Autowired
    private PasajeroService pasajeroService;

    // ADMIN
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PasajeroResponse>> getPasajeros(@AuthenticationPrincipal Usuario usuario){
        return ResponseEntity.ok(pasajeroService.getPasajeros());
    }

    @PostMapping("/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPasajero(@PathVariable int idUsuario, @AuthenticationPrincipal Usuario usuario) {
        try{
            return ResponseEntity.ok(pasajeroService.createPasajero(idUsuario));
        }catch (UsuarioNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (DuplicateResourceException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

}
