package com.unigo.service;

import com.unigo.persistence.entities.Pasajero;
import com.unigo.persistence.repositories.PasajeroRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.service.dtos.PasajeroResponse;
import com.unigo.service.exceptions.DuplicateResourceException;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import com.unigo.service.mappers.PasajeroMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasajeroService {

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ADMIN
    public List<PasajeroResponse> getPasajeros() {
        return pasajeroRepository.findAll().stream()
                .map(PasajeroMapper::mapPasajeroToDto)
                .toList();
    }

    public PasajeroResponse createPasajero(int idUsuario) {
        if(!usuarioRepository.existsById(idUsuario)){
            throw new UsuarioNotFoundException("No se ha encontrado el usuario con id " + idUsuario);
        }
        if(pasajeroRepository.existsByIdUsuario(idUsuario)){
            throw new DuplicateResourceException("Pasajero ya existente");
        }
        Pasajero p = new Pasajero();
        p.setIdUsuario(idUsuario);
        Pasajero savedP = pasajeroRepository.save(p);
        return PasajeroMapper.mapPasajeroToDto(savedP);
    }

    // NO ENDPOINT, se llama desde AuthService.register()
    public void autoCreate(int idUsuario) {
        if(!usuarioRepository.existsById(idUsuario)){
            throw new UsuarioNotFoundException("No se ha encontrado el usuario con id " + idUsuario);
        }
        if(pasajeroRepository.existsByIdUsuario(idUsuario)){
            throw new DuplicateResourceException("Pasajero ya existente");
        }
        Pasajero p = new Pasajero();
        p.setIdUsuario(idUsuario);
        pasajeroRepository.save(p);
    }

    // METHODS

    // AUX
    public boolean isUsuario(int idPasajero, int idUsuario) {
        return pasajeroRepository.existsByIdAndIdUsuario(idPasajero, idUsuario);
    }

}
