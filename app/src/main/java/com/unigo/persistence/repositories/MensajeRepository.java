package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {
}
