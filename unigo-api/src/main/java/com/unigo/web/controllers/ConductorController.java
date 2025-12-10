package com.unigo.web.controllers;

import com.unigo.security.user.Usuario;
import com.unigo.service.dtos.ConductorResponse;
import com.unigo.service.dtos.VehiculoRequest;
import com.unigo.service.dtos.VehiculoResponse;
import com.unigo.service.exceptions.*;
import com.unigo.service.services.ConductorService;
import com.unigo.service.services.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.DuplicateFormatFlagsException;
import java.util.List;

@RestController
@RequestMapping("/conductores")
public class ConductorController {

    @Autowired
    private ConductorService conductorService;

    @Autowired
    private VehiculoService vehiculoService;

    // CONDUCTOR ADMIN
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConductorResponse>> findAll(@AuthenticationPrincipal Usuario usuario){
        return ResponseEntity.ok(conductorService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getConductorById(@PathVariable int id, @AuthenticationPrincipal Usuario usuario){
        try {
            return ResponseEntity.ok(conductorService.findById(id));
        }catch (ConductorNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // CONDUCTOR USER
    @GetMapping("/search/reputacion")
    public ResponseEntity<?> findByReputacionGreaterThanEqual(@RequestParam float mayorQue,
                                                              @AuthenticationPrincipal Usuario usuario){
        try{
            return ResponseEntity.ok(conductorService.findByReputacionGreaterThanEqual(mayorQue));
        }catch (ConductorException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (ConductorNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // VEHICULO ADMIN

    // VEHICULO USER
    @GetMapping("/{idConductor}/vehiculos")
    public ResponseEntity<?> getVehiculosByIdConductor(@PathVariable int idConductor,
                                                       @AuthenticationPrincipal Usuario usuario){
        try{
            return ResponseEntity.ok(vehiculoService.getVehiculosByIdConductor(idConductor, usuario.getId()));
        }catch (ConductorException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{idConductor}/vehiculos/{idVehiculo}")
    public ResponseEntity<?> getVehiculoByIdAndIdConductor(@PathVariable int idConductor,
                                                           @PathVariable int idVehiculo,
                                                           @AuthenticationPrincipal Usuario usuario){
        try{
            return ResponseEntity.ok(vehiculoService.getVehiculoByIdAndIdConductor(idVehiculo, idConductor, usuario.getId()));
        }catch (ConductorException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (VehiculoNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/vehiculo")
    @PreAuthorize("hasRole('USER')")                // todo: GlobalExceptionHandler
    public ResponseEntity<?> createVehiculo(@Valid @RequestBody VehiculoRequest request,
                                                           @AuthenticationPrincipal Usuario usuario){
        try{
            return ResponseEntity.ok(vehiculoService.createVehiculo(request, usuario.getId()));
        }catch (UsuarioNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (DuplicateResourceException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

}
