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

    @GetMapping("/{idVehiculo}")
    public ResponseEntity<?> getMisVehiculos(@PathVariable int idVehiculo) {
        return ResponseEntity.ok(vehiculoService.getVehiculoById(idVehiculo));
    }

    @PostMapping
    public ResponseEntity<?> createVehiculo(@RequestBody VehiculoRequest request) {
        return ResponseEntity.ok(vehiculoService.createVehiculo(request));
    }

    @PutMapping("/{idVehiculo}")
    public ResponseEntity<?> updateVehiculo(@PathVariable int idVehiculo, @RequestBody VehiculoRequest request){
        return ResponseEntity.ok(vehiculoService.updateVehiculo(idVehiculo, request));
    }

    @DeleteMapping("/{idVehiculo}")
    public ResponseEntity<?> deleteVehiculo(int idVehiculo){
        return ResponseEntity.ok(vehiculoService.delete(idVehiculo));
    }
}
