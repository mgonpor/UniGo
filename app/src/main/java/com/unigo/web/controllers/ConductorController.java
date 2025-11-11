package com.unigo.web.controllers;

import com.unigo.service.dtos.ConductorResponse;
import com.unigo.service.services.ConductorService;
import com.unigo.service.exceptions.ConductorNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conductores")
public class ConductorController {

    @Autowired
    private ConductorService conductorService;

    @GetMapping
    public ResponseEntity<List<ConductorResponse>> findAll(){
        return ResponseEntity.ok(conductorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getConductorById(@PathVariable int id){
        try {
            return ResponseEntity.ok(conductorService.findById(id));
        }catch (ConductorNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    // POSIBLES MÉTODOS (YA EN EL SERVICIO)

    @GetMapping("/search/reputacion")
    public ResponseEntity<?> findByReputacionGreaterThanEqual(@RequestParam float mayorQue){
        try{
            return ResponseEntity.ok(conductorService.findByReputacionGreaterThanEqual(mayorQue));
        }catch (ConductorNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
