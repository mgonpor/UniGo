package com.unigo.service.services;

import com.unigo.security.user.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // AUX
    public boolean existsById(int idUsuario) {
        return usuarioRepository.existsById(idUsuario);
    }

}
