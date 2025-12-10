package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {
    boolean existsByIdAndIdConductor(int idVehiculo, int idConductor);

    List<Vehiculo> findAllByIdConductor(int idConductor);

    Optional<Vehiculo> findByIdAndIdConductor(int id, int idConductor);
}
