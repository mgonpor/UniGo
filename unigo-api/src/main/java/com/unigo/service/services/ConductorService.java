package com.unigo.service.services;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.service.dtos.ConductorResponse;
import com.unigo.service.exceptions.ConductorException;
import com.unigo.service.exceptions.ConductorNotFoundException;
import com.unigo.service.exceptions.DuplicateResourceException;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import com.unigo.service.mappers.ConductorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConductorService {

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private UsuarioService usuarioService;

    // TODO: ADMIN check Controller
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

    //TODO: NO ENDPOINT, se llama desde VehiculoService.create()
    public void autoCreate(int idUsuario){
        if(!usuarioService.existsById(idUsuario)){
            throw new UsuarioNotFoundException("No se ha encontrado el usuario con id " + idUsuario);
        }
        if(conductorRepository.existsByIdUsuario(idUsuario)){
            throw new DuplicateResourceException("Conductor ya existente");
        }
        Conductor c = new Conductor();
        c.setIdUsuario(idUsuario);
        c.setReputacion(0);
        conductorRepository.save(c);
    }

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

    // AUX
    public boolean isUsuario(int idConductor, int idUsuario) {
        return conductorRepository.existsByIdAndIdUsuario(idConductor, idUsuario);
    }

}
