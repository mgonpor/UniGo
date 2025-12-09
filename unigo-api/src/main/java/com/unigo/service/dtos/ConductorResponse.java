package com.unigo.service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ConductorResponse {

    private Integer id;
    private Integer idUsuario;
    private String nombre;
    private String username;
    private List<VehiculoResponse> vehiculos;
    private Float reputacion;

}
