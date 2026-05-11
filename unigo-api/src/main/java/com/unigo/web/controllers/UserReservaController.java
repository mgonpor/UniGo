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

    @GetMapping("/estado")
    public ResponseEntity<?> getMisReservasByEstado(@RequestParam String estado) {
        return ResponseEntity.ok(reservaService.getMisReservasByEstado(estado));
    }

    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestParam int idViaje){
        return ResponseEntity.ok(reservaService.createReserva(idViaje));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> candelarReservaPasajero(@PathVariable int id){
        return ResponseEntity.ok(reservaService.candelarReservaPasajero(id));
    }

    @PutMapping("/{id}/valorar")
    public ResponseEntity<?> ponerValoraciones(@PathVariable int id,@RequestParam int valNum,
                                               @RequestParam String valText) {
        return ResponseEntity.ok(reservaService.ponerValoraciones(id, valNum, valText));
    }

}
