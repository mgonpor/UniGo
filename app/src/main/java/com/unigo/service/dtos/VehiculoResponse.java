package com.unigo.service.dtos;

import lombok.Builder;

@Builder
public record VehiculoResponse(int id,
                               String marca,
                               String modelo,
                               String color) {
}
