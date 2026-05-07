package com.unigo.web.controllers;

import com.unigo.service.VehiculoService;
import com.unigo.service.dtos.VehiculoRequest;
import com.unigo.service.dtos.VehiculoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/vehiculos")
public class AdminVehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping
    public ResponseEntity<List<VehiculoResponse>> findAll(){
        return ResponseEntity.ok(vehiculoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id){
        return ResponseEntity.ok(vehiculoService.findById(id));
    }

    @PostMapping("/{idConductor}")
    public ResponseEntity<?> create(@PathVariable int idConductor, @RequestBody VehiculoRequest request){
        return ResponseEntity.ok(this.vehiculoService.createAdmin(idConductor, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestParam int idConductor,
                                    @RequestBody VehiculoRequest request){
        return ResponseEntity.ok(this.vehiculoService.updateAdmin(id, idConductor, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id){
        return ResponseEntity.ok(vehiculoService.deleteAdmin(id));
    }

}
