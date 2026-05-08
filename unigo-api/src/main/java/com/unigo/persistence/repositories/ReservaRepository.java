package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Reserva;
import com.unigo.persistence.entities.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    List<Reserva> findAllByIdPasajero(int id);

    Optional<Reserva> findByIdAndIdPasajero(int id, int idPasajero);

    boolean existsByIdAndIdPasajero(int id, int id1);

    Optional<Reserva> findByIdAndIdViaje(int id, int idViaje);

    boolean existsByIdAndIdViaje(int id, int idViaje);

    List<Reserva> findAllByIdPasajeroAndEstadoReserva(int idPasajero, EstadoReserva estadoReserva);
}
