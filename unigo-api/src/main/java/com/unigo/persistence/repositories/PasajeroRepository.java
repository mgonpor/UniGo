package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Pasajero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasajeroRepository extends JpaRepository<Pasajero,Integer> {
    boolean existsByIdUsuario(int idUsuario);

    Optional<Pasajero> findByIdUsuario(int idUsuario);
}
