package com.unigo.service;

import com.unigo.persistence.entities.Usuario;
import com.unigo.persistence.repositories.UsuarioRepository;
import com.unigo.service.dtos.RegisterRequest;
import com.unigo.service.dtos.UsuarioResponse;
import com.unigo.service.exceptions.UsuarioException;
import com.unigo.service.exceptions.UsuarioNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no se ha encontrado"));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol())
                .build();
    }

    public Usuario create(RegisterRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
        usuario.setRol("USER");
        return usuarioRepository.save(usuario);
    }

    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no se ha encontrado"));
    }

    public List<UsuarioResponse> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(u -> new UsuarioResponse(u.getId(), u.getNombre(), u.getUsername(), u.getEmail(), u.getRol()))
                .toList();
    }

    // Acciones especiales de ADMIN
    public String crearUsuarioComoAdmin(RegisterRequest request) {
        try{
            this.create(request);
        }catch(Exception e){
            throw new UsuarioException("No se ha podido crear el usuario.");
        }
        return "Usuario " +  request.getUsername() + " ha sido creado correctamente.";
    }

    public String cambiarRolUsuario(int idUsuario, String rol) {
        Optional<Usuario> optU = usuarioRepository.findById(idUsuario);
         if(optU.isEmpty()){
             throw new UsuarioNotFoundException("Usuario no encontrado");
         }
         Usuario usuario = optU.get();
         usuario.setRol(rol);
         usuarioRepository.save(usuario);
         return "Nuevo rol: " + usuario.getRol();
    }
}
