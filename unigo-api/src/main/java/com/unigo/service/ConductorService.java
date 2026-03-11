package com.unigo.service;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.service.dtos.ConductorResponse;
import com.unigo.service.dtos.VehiculoRequest;
import com.unigo.service.dtos.VehiculoResponse;
import com.unigo.service.exceptions.*;
import com.unigo.service.mappers.ConductorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConductorService {

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ADMIN
    public List<ConductorResponse> findAll(){
        return conductorRepository.findAll()
                .stream()
                .map(ConductorMapper::mapConductorToDto)
                .collect(Collectors.toList());
    }

    // ADMIN Y USER
    public ConductorResponse findByIdUsuario(int idUsuario){
        if(!usuarioRepository.existsById(idUsuario)){
            throw new UsuarioNotFoundException("No se ha encontrado el usuario con id " + idUsuario);
        }
        Optional<Conductor> c = this.conductorRepository.findByIdUsuario(idUsuario);
        if(c.isEmpty()){
            throw new ConductorNotFoundException("El id de usuario no corresponde a ningún conductor.");
        }
        return ConductorMapper.mapConductorToDto(c.get());
    }

    public ConductorResponse create(int idUsuario){
        return ConductorMapper.mapConductorToDto(this.autoCreate(idUsuario));
    }

    public ConductorResponse updateReputacion(int idUsuario, int idConductor, float reputacion){
        if(!conductorRepository.existsById(idConductor)){
            throw new ConductorNotFoundException("Conductor " + idConductor + " no encontrado.");
        }
        if(!this.conductorRepository.existsByIdAndIdUsuario(idConductor, idUsuario)){
            throw new ConductorNotFoundException("El id de usuario no corresponde con el del conductor.");
        }
        Conductor cDB = conductorRepository.findById(idConductor).get();
        if(reputacion < 0 || reputacion > 5){
            throw new ConductorException("La reputación debe estar entre 0 y 5.");
        }
        cDB.setReputacion(reputacion);
        conductorRepository.save(cDB);
        return ConductorMapper.mapConductorToDto(cDB);
    }

    public String delete(int idUsuario){
        if(!conductorRepository.existsByIdUsuario(idUsuario)){
            throw new ConductorNotFoundException("Conductor con id de usuario" + idUsuario + " no encontrado.");
        }
        conductorRepository.deleteByIdUsuario((idUsuario));
        return "Conductor con id de usuario " + idUsuario + " eliminado.";
    }

    // CRUDs normales
    // por el usuario
    public ConductorResponse getMeConductor(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Conductor> c = conductorRepository.findByUsuario_Username(username);
        if (c.isEmpty()){
            throw new ConductorNotFoundException("No eres conductor aún.");
        }
        return ConductorMapper.mapConductorToDto(c.get());
    }

    // Se llama desde CreateConductorAdmin y CreateVehiculoUser (en este servicio)
    private Conductor autoCreate(int idUsuario){
        if(!usuarioRepository.existsById(idUsuario)){
            throw new UsuarioNotFoundException("No se ha encontrado el usuario con id " + idUsuario);
        }
        if(conductorRepository.existsByIdUsuario(idUsuario)){
            throw new DuplicateResourceException("Conductor ya existente");
        }
        Conductor c = new Conductor();
        c.setIdUsuario(idUsuario);
        c.setReputacion(0);
        return conductorRepository.save(c);
    }

    // ADMIN Y USER
    public List<ConductorResponse> findByReputacionGreaterThanEqual(float reputacionMayorQue){

        if(reputacionMayorQue > 5 || reputacionMayorQue < 0){
            throw new ConductorException("La reputación se mide entre 0 y 5.");
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

    // aux CRUDs Vehiculo admin
    // create admin
    public VehiculoResponse createVehiculoAdmin(int idConductor, VehiculoRequest vehiculoRequest){
        if (!conductorRepository.existsById(idConductor)){
            throw new ConductorNotFoundException("Conductor no encontrado");
        }
        return vehiculoService.createAdmin(idConductor, vehiculoRequest);
    }

    // update admin
    public VehiculoResponse updateVehiculoAdmin(int idVehiculo, int idConductor, VehiculoRequest vehiculoRequest){
        if (!conductorRepository.existsById(idConductor)){
            throw new ConductorNotFoundException("Conductor no encontrado");
        }
        return vehiculoService.updateAdmin(idVehiculo, idConductor, vehiculoRequest);
    }

    // TODO: aux CRUDs Vehiculo USER (comprobaciones usuario)
}
