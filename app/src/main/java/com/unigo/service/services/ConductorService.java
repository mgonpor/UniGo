package com.unigo.service.services;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.service.dtos.ConductorResponse;
import com.unigo.service.exceptions.ConductorNotFoundException;
import com.unigo.service.mappers.ConductorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConductorService {

    @Autowired
    private ConductorRepository conductorRepository;

    public List<ConductorResponse> findAll(){
        return conductorRepository.findAll()
                .stream()
                .map(ConductorMapper.INSTANCE::mapConductorToDto)
                .toList();
    }

    public ConductorResponse findById(int id){ // TODO: Usar DTOs
        if(!conductorRepository.existsById(id)){
            throw new ConductorNotFoundException("Conductor " + id + " no encontrado.");
        }

        Conductor conductor = conductorRepository.findById(id).get();

        return ConductorMapper.INSTANCE.mapConductorToDto(conductor);
    }

    // TODO: AVERIGUAR COMO MAPEAR ConductorRequest DESDE ConductorMapper (mapDtoToConductor)
    // ANTES DE VEHICULO
    // ESTO PA ENTIDADES QUE NO DEVUELVAN LISTAS (Mapper INTERFACE también)
//    public ConductorResponse create(ConductorRequest conductorRequest){
//
//        // Recoge Request y guarda Entity
//        Conductor conductor = ConductorMapper.INSTANCE.mapDtoToConductor(conductorRequest);
//        conductorRepository.save(conductor);
//
//        // Recoge Entity y devuelve
//        return ConductorMapper.INSTANCE.mapConductorToDto(conductor);
//
//    }


    // POSIBLES MÉTODOS
    public List<ConductorResponse> findByReputacionGreaterThanEqual(float reputacionMayorQue){

        List<ConductorResponse> lista = conductorRepository.findByReputacionGreaterThanEqual(reputacionMayorQue)
                                        .stream()
                                        .map(ConductorMapper.INSTANCE::mapConductorToDto)
                                        .toList();

        if(lista.isEmpty()){
            throw new ConductorNotFoundException("No existen conductores con reputación " + reputacionMayorQue + " o más.");
        }

        return lista;
    }
}
