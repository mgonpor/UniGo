package com.unigo.service.mappers;

import com.unigo.persistence.entities.Pasajero;
import com.unigo.security.user.Usuario;
import com.unigo.service.dtos.PasajeroResponse;

public class PasajeroMapper {

    public static PasajeroResponse mapPasajeroToDto(Pasajero pasajero) {
        PasajeroResponse dto = new PasajeroResponse();

        dto.setId(pasajero.getId());
        dto.setIdUsuario(pasajero.getIdUsuario());

        Usuario u = pasajero.getUsuario();
        if (u != null) {
            dto.setNombre(u.getNombre());
            dto.setUsername(u.getUsername());
        }

        return dto;
    }

}
