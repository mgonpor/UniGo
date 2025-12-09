package com.unigo.service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConductorRequest {

    private String nombre;
    private String nombreUsuario;
    private String email;
    private String password;

}
