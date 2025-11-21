package com.unigo.service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VehiculoResponse {

    private Integer id;
    private String marca;
    private String modelo;
    private String color;

}
