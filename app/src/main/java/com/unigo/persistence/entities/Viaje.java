package com.unigo.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unigo.persistence.entities.enums.EstadoViaje;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "viaje")
@Getter
@Setter
@NoArgsConstructor
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_conductor")
    private int idConductor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_conductor", referencedColumnName = "id", insertable = false, updatable = false)
    private Conductor conductor;

    @Column(nullable = false)
    private String origen;
    @Column(nullable = false)
    private String destino;

    @Column(name = "fecha_salida",  nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "plazas_disponibles",   nullable = false)
    private int plazasDisponibles;

    @Column(name="precio_plaza", nullable = false)
    private double precioPlaza;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_reserva",  nullable = false)
    private EstadoViaje estadoViaje;

    @OneToMany(mappedBy = "viaje")
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "viaje")
    @JsonIgnore
    private List<Mensaje> mensajes;

}
