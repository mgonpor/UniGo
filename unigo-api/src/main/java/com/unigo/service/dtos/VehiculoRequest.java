package com.unigo.service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehiculoRequest {

    private Integer id;

    @NotBlank(message = "Marca obligatoria")
    private String marca;

    @NotBlank(message = "Modelo obligatorio")
    private String modelo;

    private String color;

    @NotBlank(message = "Matrícula obligatoria")
    @Size(min = 7, message = "Mínimo 8 caracteres")
    private String matricula;

}
