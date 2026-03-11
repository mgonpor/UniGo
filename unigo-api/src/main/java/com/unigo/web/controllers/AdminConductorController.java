package com.unigo.web.controllers;

import com.unigo.service.dtos.ConductorResponse;
import com.unigo.service.exceptions.*;
import com.unigo.service.ConductorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<ConductorResponse>> findAll(){
        return ResponseEntity.ok(conductorService.findAll());
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> getConductorById(@PathVariable int idUsuario){
        return ResponseEntity.ok(conductorService.findByIdUsuario(idUsuario));
    }

    @PostMapping
    public ResponseEntity<?> createConductor(@RequestParam int idUsuario){
        return ResponseEntity.ok(conductorService.create(idUsuario));
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<?> updateConductor(@PathVariable int idUsuario, @RequestParam int idConductor, @RequestParam float reputacion){
        return ResponseEntity.ok(conductorService.updateReputacion(idUsuario, idConductor, reputacion));
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<?> deleteConductor(@PathVariable int idUsuario){
        return ResponseEntity.ok(conductorService.delete(idUsuario));
    }

    // ADMIN Y USER
    @GetMapping("/searchByReputacion")
    public ResponseEntity<?> findByReputacionGreaterThanEqual(@RequestParam float mayorQue){
        try{
            return ResponseEntity.ok(conductorService.findByReputacionGreaterThanEqual(mayorQue));
        }catch (ConductorException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (ConductorNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /*@GetMapping("/{idConductor}/vehiculos")
    public ResponseEntity<?> getVehiculosByIdConductor(@PathVariable int idConductor){
        try{
            return ResponseEntity.ok(vehiculoService.getVehiculosByIdConductor(idConductor, usuario.getId()));
        }catch (ConductorException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{idConductor}/vehiculos/{idVehiculo}")
    public ResponseEntity<?> getVehiculoByIdAndIdConductor(@PathVariable int idConductor,
                                                           @PathVariable int idVehiculo){
        try{
            return ResponseEntity.ok(vehiculoService.getVehiculoByIdAndIdConductor(idVehiculo, idConductor, usuario.getId()));
        }catch (ConductorException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (VehiculoNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/vehiculo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createVehiculo(@Valid @RequestBody VehiculoRequest request){
        try{
            return ResponseEntity.ok(vehiculoService.createVehiculo(request, usuario.getId()));
        }catch (UsuarioNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (DuplicateResourceException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }*/

}
