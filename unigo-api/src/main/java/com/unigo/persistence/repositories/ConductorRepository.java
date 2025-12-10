package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConductorRepository extends JpaRepository<Conductor,Integer> {

    boolean existsByIdUsuario(int idUsuario);

    boolean existsByIdAndIdUsuario(int id, int idUsuario);

    List<Conductor> findByReputacionGreaterThanEqual(float reputacion);

    Optional<Conductor> findByIdUsuario(int idUsuario);
}
