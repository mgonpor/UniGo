package com.unigo.web.controllers;

import com.unigo.service.ReservaService;
import com.unigo.service.dtos.ReservaRequest;
import com.unigo.service.dtos.ReservaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reservas")
public class AdminReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> findAll() {
        return ResponseEntity.ok(reservaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        return ResponseEntity.ok(reservaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> createAdmin(@RequestParam int idPasajero, @RequestParam int idViaje){
        return ResponseEntity.ok(reservaService.createAdmin(idPasajero, idViaje));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable int id, @RequestParam int idPasajero,
                                         @RequestParam int idViaje, @RequestBody ReservaRequest request){
        return ResponseEntity.ok(reservaService.updateAdmin(id, idPasajero, idViaje, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable int id){
        return ResponseEntity.ok(reservaService.deleteAdmin(id));
    }

}
