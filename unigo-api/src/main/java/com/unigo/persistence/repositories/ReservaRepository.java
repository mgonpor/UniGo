package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
}
