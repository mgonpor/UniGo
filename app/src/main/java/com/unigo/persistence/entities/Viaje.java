package com.unigo.persistence.entities;

import com.unigo.persistence.entities.enums.EstadoViaje;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "viaje")
@Getter
@Setter
@NoArgsConstructor
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conductor", nullable = false)
    private Conductor conductor;

    private double[] origen;
    private double[] destino;

    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;

    @Column(name = "plazas_disponibles")
    private int plazasDisponibles;

    @Column(name="precio_plaza")
    private double precioPlaza;

    private EstadoViaje estadoViaje;

}
