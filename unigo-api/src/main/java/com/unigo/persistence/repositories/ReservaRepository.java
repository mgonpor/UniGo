package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    List<Reserva> findAllByIdPasajero(int id);

    Optional<Reserva> findByIdAndIdPasajero(int id, int idPasajero);

    boolean existsByIdAndIdPasajero(int id, int id1);
}
