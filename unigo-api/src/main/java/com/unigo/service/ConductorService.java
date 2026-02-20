package com.unigo.service;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.service.dtos.ConductorResponse;
import com.unigo.service.dtos.VehiculoRequest;
import com.unigo.service.dtos.VehiculoResponse;
import com.unigo.service.exceptions.ConductorException;
import com.unigo.service.exceptions.ConductorNotFoundException;
import com.unigo.service.exceptions.DuplicateResourceException;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import com.unigo.service.mappers.ConductorMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private VehiculoService vehiculoService;

    // ADMIN
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

    public ConductorResponse create(int idUsuario){
        return ConductorMapper.mapConductorToDto(this.autoCreate(idUsuario));
    }

    public ConductorResponse update(int idConductor, Conductor conductor){
        if(!conductorRepository.existsById(idConductor)){
            throw new ConductorNotFoundException("Conductor " + idConductor + " no encontrado.");
        }
        if (idConductor != conductor.getId()){
            throw new ConductorException("Inconsistencia con identificador " + idConductor);
        }
        if(!this.isUsuario(idConductor, conductor.getIdUsuario())){
            throw new ConductorException("Id conductor incorrecto");
        }
        Conductor cDB = conductorRepository.findById(idConductor).get();
        if (conductor.getReputacion() > 5){
            throw new ConductorException("Reputación no puede ser mayor a 5.");
        }
        cDB.setReputacion(conductor.getReputacion());
        conductorRepository.save(cDB);
        return ConductorMapper.mapConductorToDto(cDB);
    }

    public String delete(int idConductor){
        if(!conductorRepository.existsById(idConductor)){
            throw new ConductorNotFoundException("Conductor " + idConductor + " no encontrado.");
        }
        conductorRepository.deleteById(idConductor);
        return "Conductor " + idConductor + " eliminado.";
    }

    // CRUDs normales
    // por el usuario
    public ConductorResponse getMeConductor(int idUsuario){
        Optional<Conductor> c = conductorRepository.findByIdUsuario(idUsuario);
        if (c.isEmpty()){
            throw new ConductorNotFoundException("No eres conductor aún");
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

    // otro
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


    // AUX
    public boolean isUsuario(int idConductor, int idUsuario) {
        return conductorRepository.existsByIdAndIdUsuario(idConductor, idUsuario);
    }

}
