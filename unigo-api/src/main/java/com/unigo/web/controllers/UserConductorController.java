package com.unigo.web.controllers;

import com.unigo.service.ConductorService;
import com.unigo.service.exceptions.ConductorException;
import com.unigo.service.exceptions.ConductorNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/conductores")
public class UserConductorController {

    @Autowired
    private ConductorService conductorService;

    // no hay getAll
    // get el usuario
    @GetMapping("/me")
    public ResponseEntity<?> getMe(){
        return ResponseEntity.ok(this.conductorService.getMeConductor());
    }
    // no hay create
    // update no tiene sentido
    // delete tampoco ya que tendria que quitar los coches

    @GetMapping("/searchByReputacion")
    public ResponseEntity<?> findByReputacionGreaterThanEqual(@RequestParam float mayorQue){
        try{
            return ResponseEntity.ok(conductorService.findByReputacionGreaterThanEqual(mayorQue));
        }catch (ConductorException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (ConductorNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
