package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConductorRepository extends JpaRepository<Conductor,Integer> {

    List<Conductor> findByReputacionGreaterThanEqual(float reputacion);

}
