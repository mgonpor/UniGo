package com.unigo.persistence.repositories;

import com.unigo.persistence.entities.Usuario;
import org.springframework.data.repository.ListCrudRepository;

public interface UsuarioBaseRepository<T extends Usuario> extends ListCrudRepository<T, Integer> {
}
