package com.unigo.auth;

import com.unigo.auth.dtos.RegisterRequest;
import com.unigo.security.user.Role;
import com.unigo.security.user.Usuario;

public class AuthMapper {

    public static Usuario registerToUser(RegisterRequest registerRequest) {
        Usuario usuario = new Usuario();

        usuario.setNombre(registerRequest.getNombre());
        usuario.setUsername(registerRequest.getUsername());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(registerRequest.getPassword());
        usuario.setRole(Role.USER);

        return usuario;
    }
}
