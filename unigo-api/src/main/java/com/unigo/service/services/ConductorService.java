package com.unigo.service.services;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.service.dtos.ConductorResponse;
import com.unigo.service.exceptions.ConductorException;
import com.unigo.service.exceptions.ConductorNotFoundException;
import com.unigo.service.mappers.ConductorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConductorService {

    @Autowired
    private ConductorRepository conductorRepository;

    // CRUDs Conductor

    public List<ConductorResponse> findAll(){
        return conductorRepository.findAll()
                .stream()
                .map(ConductorMapper::mapConductorToDto)
                .collect(Collectors.toList());
    }

    public ConductorResponse findById(int id){
        if(!conductorRepository.existsById(id)){
            throw new ConductorNotFoundException("Conductor " + id + " no encontrado.");
        }

        Conductor conductor = conductorRepository.findById(id).get();

        return ConductorMapper.mapConductorToDto(conductor);
    }

    //TODO: DESPUÉS SE CREARÁN USUARIOS COMO PASAJEROS
    // Y SE CREARÁN LOS CONDUCTORES AL AÑADIR UN VEHÍCULO

    // CRUDs Vehiculo

    // POSIBLES MÉTODOS
    public List<ConductorResponse> findByReputacionGreaterThanEqual(float reputacionMayorQue){

        if(reputacionMayorQue > 5 || reputacionMayorQue < 0){
            throw new ConductorException("La reputación se mide entre 1 y 5.");
        }

        List<ConductorResponse> lista = conductorRepository.findByReputacionGreaterThanEqual(reputacionMayorQue)
                                        .stream()
                                        .map(ConductorMapper::mapConductorToDto)
                                        .toList();

        if(lista.isEmpty()){
            throw new ConductorNotFoundException("No existen conductores con reputación " + reputacionMayorQue + " o más.");
        }

        return lista;
    }
}
