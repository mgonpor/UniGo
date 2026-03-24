package com.unigo.web.controllers;

import com.unigo.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/reservas")
public class UserReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public ResponseEntity<?> getMisReservas() {
        return ResponseEntity.ok(reservaService.getMisReservas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMiReservaById(@PathVariable int id) {
        return ResponseEntity.ok(reservaService.getMiReservaById(id));
    }

    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestParam int idViaje){
        return ResponseEntity.ok(reservaService.createReserva(idViaje));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReserva(@PathVariable int id){
        return ResponseEntity.ok(reservaService.deleteReserva(id));
    }

}
