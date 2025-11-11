package com.unigo.service;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.service.exceptions.ConductorNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConductorService {

    @Autowired
    private ConductorRepository conductorRepository;

    public List<Conductor> findAll(){
        return conductorRepository.findAll();
    }

    public Conductor findById(int id){
        if(!conductorRepository.existsById(id)){
            throw new ConductorNotFoundException("Conductor " + id + " no encontrado.");
        }
        return conductorRepository.findById(id).get();
    }

}
