package com.unigo.web.controllers;

import com.unigo.service.ViajeService;
import com.unigo.service.dtos.ViajeRequest;
import com.unigo.service.dtos.ViajeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/viajes")
public class UserViajeController {

    @Autowired
    private ViajeService viajeService;

    // CONDUCTOR
    @GetMapping
    public ResponseEntity<?> getMisViajes(){
        return ResponseEntity.ok(viajeService.getMisViajes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMiViajeById(@PathVariable int id){
        return ResponseEntity.ok(viajeService.getMiViajeById(id));
    }

    @PostMapping
    public ResponseEntity<?> createViaje(@RequestBody ViajeRequest request){
        return ResponseEntity.ok(viajeService.createViaje(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateViaje(@PathVariable int id, @RequestBody ViajeRequest request){
        return ResponseEntity.ok(viajeService.updateViaje(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteViaje(@PathVariable int id){
        return ResponseEntity.ok(viajeService.deleteViaje(id));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable int id, @RequestParam String estado){
        return ResponseEntity.ok(viajeService.cambiarEstadoUser(id, estado));
    }

    @PutMapping("/{idViaje}/reserva/{idReserva}/confirmar")
    public ResponseEntity<?> confirmarReserva(@PathVariable int idViaje, @PathVariable int idReserva){
        return ResponseEntity.ok(viajeService.confirmarReserva(idViaje, idReserva));
    }

    @PutMapping("/{idViaje}/reserva/{idReserva}/cancelar")
    public ResponseEntity<?> cancelarReserva(@PathVariable int idViaje, @PathVariable int idReserva){
        return ResponseEntity.ok(viajeService.cancelarReserva(idViaje, idReserva));
    }

    // CONDUCTOR
    @GetMapping("/{idViaje}/reservas")
    public ResponseEntity<?> getReservasByIdViaje(@PathVariable int idViaje){
        return ResponseEntity.ok(viajeService.getReservasByIdViaje(idViaje));
    }

    // PASAJERO
    @GetMapping("/{idViaje}/pasajero")
    public ResponseEntity<?> getViajePasajero(@PathVariable int idViaje){
        return ResponseEntity.ok(viajeService.getViajeByIdPasajero(idViaje));
    }

    @GetMapping("/historial")
    public ResponseEntity<?> getMisViajesPasajero(){
        return ResponseEntity.ok(viajeService.getMisViajesPasajero());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ViajeResponse>> viajesDisponibles(){
        return ResponseEntity.ok(viajeService.getViajesDisponibles());
    }

}
