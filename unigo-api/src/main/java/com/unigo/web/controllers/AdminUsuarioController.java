package com.unigo.web.controllers;

import com.unigo.service.UsuarioService;
import com.unigo.service.dtos.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/nuevo")
    public ResponseEntity<?> crearUsuario(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(usuarioService.crearUsuarioComoAdmin(request));
    }

    @PutMapping("/{id}/rol")
    public ResponseEntity<?> cambiarRolUsuario(@PathVariable int id, @RequestParam String rol){
        return ResponseEntity.ok(usuarioService.cambiarRolUsuario(id, rol));
    }

}
