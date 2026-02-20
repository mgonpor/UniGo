package com.unigo.web.controllers;

import com.unigo.persistence.entities.Usuario;
import com.unigo.service.dtos.VehiculoRequest;
import com.unigo.service.dtos.VehiculoResponse;
import com.unigo.service.exceptions.ConductorNotFoundException;
import com.unigo.service.exceptions.VehiculoNotFoundException;
import com.unigo.service.ConductorService;
import com.unigo.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/conductores")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    // comprobaciones user
    @Autowired
    private ConductorService conductorService;

    // ADMIN
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VehiculoResponse>> findAll(@AuthenticationPrincipal Usuario usuario){
        return ResponseEntity.ok(this.vehiculoService.findAll());
    }

    @GetMapping("/{idVehiculo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findById(@PathVariable int idVehiculo, @AuthenticationPrincipal Usuario usuario){
        try{
            return ResponseEntity.ok(this.vehiculoService.findById(idVehiculo));
        }catch (VehiculoNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestParam int idConductor, @Valid @RequestBody VehiculoRequest vehiculoRequest,
                                    @AuthenticationPrincipal Usuario usuario){
        try{
            return ResponseEntity.ok(conductorService.createVehiculoAdmin(idConductor, vehiculoRequest));
        }catch (ConductorNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{idVehiculo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable int idVehiculo, @RequestParam int idConductor,
                                    @Valid @RequestBody VehiculoRequest vehiculoRequest,
                                    @AuthenticationPrincipal Usuario usuario){
        try{
            return ResponseEntity.ok(conductorService.updateVehiculoAdmin(idVehiculo, idConductor, vehiculoRequest));
        }catch (ConductorNotFoundException | VehiculoNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{idVehiculo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable int idVehiculo, @AuthenticationPrincipal Usuario usuario){
        try{
            return ResponseEntity.ok(vehiculoService.deleteAdmin(idVehiculo));
        }catch (VehiculoNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //todo: Vehiculo USER CRUDs

}
