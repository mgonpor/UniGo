package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Viaje;
import com.unigo.persistence.entities.enums.EstadoViaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ViajeRepository extends JpaRepository<Viaje, Integer> {

    boolean existsByIdAndIdConductor(int id, int idConductor);

    List<Viaje> findAllByIdConductor(int id);

    Optional<Viaje> findByIdAndIdConductor(int id, int idConductor);

    List<Viaje> findByEstadoViaje(EstadoViaje estadoViaje);

    List<Viaje> findByEstadoViajeAndFechaSalidaAfter(EstadoViaje estadoViaje, LocalDate fechaSalidaAfter);
}
