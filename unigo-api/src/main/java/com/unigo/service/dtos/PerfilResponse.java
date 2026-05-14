package com.unigo.service.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilResponse {

    // Datos base del usuario
    private int id;
    private String nombre;
    private String username;
    private String email;

    // Roles activos
    private boolean esConductor;
    private boolean esPasajero;

    // Estadísticas globales
    private int totalViajesComoPasajero;   // reservas CONFIRMADAS (activas o realizadas)
    private int totalViajesRealizados;     // reservas en viajes ya pasados / COMPLETADO
    private int totalViajesComoConductor;  // viajes publicados como conductor
    private float reputacion;             // -1 si no hay valoraciones o no es conductor
    private int numValoracionesRecibidas; // como conductor
    private int numValoracionesDadas;     // como pasajero

    // Info del conductor (null si no es conductor)
    private ConductorResponse conductor;

    // Actividad reciente (máx 5 items cada lista)
    private List<ReservaResponse> ultimasReservas;
    private List<ViajeResponse>   ultimosViajes;
}
