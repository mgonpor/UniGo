package com.unigo.service;

import com.unigo.persistence.entities.Pasajero;
import com.unigo.persistence.repositories.PasajeroRepository;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.service.dtos.PasajeroResponse;
import com.unigo.service.exceptions.DuplicateResourceException;
import com.unigo.service.exceptions.PasajeroNotFoundException;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import com.unigo.service.mappers.PasajeroMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new UsuarioNotFoundException("No se ha encontrado el usuario con id " + idUsuario);
        }
        if (pasajeroRepository.existsByIdUsuario(idUsuario)) {
            throw new DuplicateResourceException("Pasajero ya existente");
        }
        Pasajero p = new Pasajero();
        p.setIdUsuario(idUsuario);
        Pasajero savedP = pasajeroRepository.save(p);
        return PasajeroMapper.mapPasajeroToDto(savedP);
    }

    // USER Y ADMIN
    public PasajeroResponse getPasajeroByIdUsuario(int idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new UsuarioNotFoundException("No se ha encontrado el usuario con id " + idUsuario);
        }
        Optional<Pasajero> p = this.pasajeroRepository.findByIdUsuario(idUsuario);
        if (p.isEmpty()) {
            throw new PasajeroNotFoundException("El id de usuario no corresponde a ningún pasajero.");
        }
        return PasajeroMapper.mapPasajeroToDto(p.get());
    }

    // NO ENDPOINT, se llama desde AuthService.register()
    public PasajeroResponse autoCreate(int idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new UsuarioNotFoundException("No se ha encontrado el usuario con id " + idUsuario);
        }
        if (pasajeroRepository.existsByIdUsuario(idUsuario)) {
            throw new DuplicateResourceException("Pasajero ya existente");
        }
        Pasajero p = new Pasajero();
        p.setIdUsuario(idUsuario);
        pasajeroRepository.save(p);
        // para los test
        return PasajeroMapper.mapPasajeroToDto(p);
    }
}
