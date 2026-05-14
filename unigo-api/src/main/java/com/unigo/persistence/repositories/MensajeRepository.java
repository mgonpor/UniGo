package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {

    List<Mensaje> findByIdViajeOrderByFechaEnvioAsc(int idViaje);

    Optional<Mensaje> findFirstByIdViajeOrderByFechaEnvioDesc(int idViaje);
}
