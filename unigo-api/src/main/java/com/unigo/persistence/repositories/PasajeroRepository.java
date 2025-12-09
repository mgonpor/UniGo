package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Pasajero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasajeroRepository extends JpaRepository<Pasajero,Integer> {
}
