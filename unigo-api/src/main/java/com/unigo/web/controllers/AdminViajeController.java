package com.unigo.web.controllers;

import com.unigo.service.ViajeService;
import com.unigo.service.dtos.ViajeRequest;
import com.unigo.service.dtos.ViajeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/viajes")
public class AdminViajeController {

    @Autowired
    private ViajeService viajeService;

    @GetMapping
    public ResponseEntity<List<ViajeResponse>> findAllViajes() {
        return ResponseEntity.ok(viajeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        return ResponseEntity.ok(viajeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestParam int idConductor, @RequestBody ViajeRequest viajeRequest) {
        return ResponseEntity.ok(viajeService.createAdmin(idConductor, viajeRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestParam int idConductor,
                                    @RequestBody ViajeRequest viajeRequest) {
        return ResponseEntity.ok(viajeService.updateAdmin(id, idConductor, viajeRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        return ResponseEntity.ok(viajeService.deleteAdmin(id));
    }

    @GetMapping("/estado")
    public ResponseEntity<?> buscarPorEstado(@RequestParam String estado){
        return ResponseEntity.ok(viajeService.searchByEstado(estado));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable int id, @RequestParam String estado){
        return ResponseEntity.ok(viajeService.cambiarEstadoAdmin(id, estado));
    }

}
