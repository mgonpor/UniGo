package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioBaseRepository<T extends Usuario> extends JpaRepository<T, Integer> {
}
