package com.unigo.persistence.entities;

import com.unigo.persistence.entities.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "reserva")
@Getter
@Setter
@NoArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_viaje", nullable = false)
    private Viaje viaje;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pasajero", nullable = false)
    private Pasajero pasajero;

    @Column(name="fecha_reserva")
    private LocalDate fechaReserva;

    @Column(name="valoracion_numerica")
    private int valoracionNumerica;

    @Column(name="valoracion_texto")
    private String valoracionTexto;

    private boolean pagado;

    @Column(name="estado_reserva")
    private EstadoReserva estadoReserva;

}
