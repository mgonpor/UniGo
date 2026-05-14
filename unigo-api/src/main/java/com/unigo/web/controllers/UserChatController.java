package com.unigo.web.controllers;

import com.unigo.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserChatController {

    @Autowired
    private MensajeService mensajeService;

    // Lista de chats accesibles para el usuario actual (conductor o pasajero con reserva CONFIRMADA).
    @GetMapping("/chats")
    public ResponseEntity<?> getMisChats() {
        return ResponseEntity.ok(mensajeService.getMisChats());
    }

    // Historial de mensajes de un viaje (chat de grupo).
    @GetMapping("/viajes/{idViaje}/mensajes")
    public ResponseEntity<?> getHistorial(@PathVariable int idViaje) {
        return ResponseEntity.ok(mensajeService.getHistorial(idViaje));
    }
}
