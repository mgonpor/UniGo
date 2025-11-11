package com.unigo.web.controllers;

import com.unigo.persistence.entities.Conductor;
import com.unigo.service.ConductorService;
import com.unigo.service.exceptions.ConductorNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conductores")
public class ConductorController {

    @Autowired
    private ConductorService conductorService;

    @GetMapping
    public ResponseEntity<List<Conductor>> findAll(){
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

}
