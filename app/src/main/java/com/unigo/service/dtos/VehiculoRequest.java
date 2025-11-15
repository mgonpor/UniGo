package com.unigo.service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VehiculoRequest {

    private Integer id;
    private Integer idConductor;
    private String marca;
    private String modelo;
    private String color;
    private String matricula;

}
