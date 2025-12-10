package com.unigo.security.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String Username);

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
