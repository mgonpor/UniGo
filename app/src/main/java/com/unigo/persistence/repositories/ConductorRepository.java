package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Conductor;

import java.util.List;
import java.util.Optional;

public interface ConductorRepository extends UsuarioBaseRepository<Conductor>{

    List<Conductor> findByReputacionGreaterThanEqual(float reputacion);

    Optional<Conductor> findByEmail(String email);
}
