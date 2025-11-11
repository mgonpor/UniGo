package com.unigo.service.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record ConductorResponse(int id,
                                String nombre,
                                String nombreUsuario,
                                List<VehiculoResponse> vehiculos,
                                float reputacion) {
}
