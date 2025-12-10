package com.unigo.service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasajeroResponse {

    private Integer id;
    private Integer idUsuario;
    private String nombre;
    private String username;

}
