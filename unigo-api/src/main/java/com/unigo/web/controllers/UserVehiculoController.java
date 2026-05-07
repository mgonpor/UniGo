package com.unigo.web.controllers;

import com.unigo.service.VehiculoService;
import com.unigo.service.dtos.VehiculoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/vehiculos")
public class UserVehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping
    public ResponseEntity<?> getMisVehiculos() {
        return ResponseEntity.ok(vehiculoService.getMisVehiculos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMisVehiculoById(@PathVariable int id) {
        return ResponseEntity.ok(vehiculoService.getVehiculoById(id));
    }

    @PostMapping
    public ResponseEntity<?> createVehiculo(@RequestBody VehiculoRequest request) {
        return ResponseEntity.ok(vehiculoService.createVehiculo(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehiculo(@PathVariable int id, @RequestBody VehiculoRequest request){
        return ResponseEntity.ok(vehiculoService.updateVehiculo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehiculo(int id){
        return ResponseEntity.ok(vehiculoService.delete(id));
    }
}
