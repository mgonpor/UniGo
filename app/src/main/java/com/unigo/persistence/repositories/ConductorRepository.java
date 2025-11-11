package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Conductor;

import java.util.List;

public interface ConductorRepository extends UsuarioBaseRepository<Conductor>{

    List<Conductor> findByReputacionGreaterThanEqual(float reputacion);

}
