package com.unigo.web.controllers;

import com.unigo.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/conductor")
public class UserConductorReservasController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/reservas")
    public ResponseEntity<?> getReservasConductor() {
        return ResponseEntity.ok(reservaService.getReservasConductor());
    }

}
