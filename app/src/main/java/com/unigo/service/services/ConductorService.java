package com.unigo.service.services;

import com.unigo.persistence.entities.Conductor;
import com.unigo.persistence.repositories.ConductorRepository;
import com.unigo.service.dtos.ConductorRequest;
import com.unigo.service.dtos.ConductorResponse;
import com.unigo.service.exceptions.ConductorException;
import com.unigo.service.exceptions.ConductorNotFoundException;
import com.unigo.service.exceptions.WrongPasswordException;
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
    public ConductorResponse create(ConductorRequest conductorRequest){
        if(conductorRepository.findByEmail(conductorRequest.getEmail()).isPresent()){
            throw new ConductorException("Ya existe un conductor con este email.");
        }
        if (conductorRequest.getEmail()==null || conductorRequest.getEmail().isBlank()){
            throw new ConductorException("Email del conductor no puede estar vacío.");
        }
        if(conductorRequest.getNombre()==null || conductorRequest.getNombre().isBlank()){
            throw new ConductorException("Nombre del conductor no puede estar vacío.");
        }
        if(conductorRequest.getNombreUsuario()==null || conductorRequest.getNombreUsuario().isBlank()){
            throw new ConductorException("Nombre de usuario del conductor no puede estar vacío.");
        }
        if(conductorRequest.getPassword()==null || conductorRequest.getPassword().isBlank()){
            throw new ConductorException("Password de conductor no puede estar vacía.");
        }

        Conductor conductor = ConductorMapper.mapDtoToConductor(conductorRequest);

        conductorRepository.save(conductor);

        // Recoge Entity y devuelve
        return ConductorMapper.mapConductorToDto(conductor);

    }

    //TODO: cambiar a todo lo que puede hacer el admin
    public ConductorResponse update(ConductorRequest conductorRequest){
        if(!conductorRepository.findByEmail(conductorRequest.getEmail()).isPresent()){
            throw new ConductorNotFoundException("No existe un conductor con este email.");
        }

        Conductor conductor = conductorRepository.findByEmail(conductorRequest.getEmail()).get();
        if(conductorRequest.getPassword() ==  null
                || conductorRequest.getPassword().isBlank()
                || !conductorRequest.getPassword().equals(conductor.getPassword()) ) {
            throw new WrongPasswordException("Contraseña incorrecta.");
        }
        if(conductorRequest.getNombre()==null || conductorRequest.getNombre().isBlank()
            || conductorRequest.getNombreUsuario()==null || conductorRequest.getNombreUsuario().isBlank() ){
            throw new ConductorException("Indique el nombre y el nombre de ususario.");
        }

        conductor.setNombre(conductorRequest.getNombre());
        conductor.setNombreUsuario(conductorRequest.getNombreUsuario());

        return ConductorMapper.mapConductorToDto(conductorRepository.save(conductor));
    }

    public void delete(int id){
        if(!conductorRepository.existsById(id)){
            throw new ConductorNotFoundException("Conductor " + id + " no encontrado.");
        }
        conductorRepository.deleteById(id);
    }

    // CRUDs Vehiculo
//    public List<VehiculoResponse> getVehiculosConductor(int id){
//        if(conductorRepository.existsById((id))) {
//            throw new ConductorNotFoundException("Conductor " + id + " no encontrado.");
//        }
//        List<VehiculoResponse> vehiculos = conductorRepository.findById();
//    }

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
