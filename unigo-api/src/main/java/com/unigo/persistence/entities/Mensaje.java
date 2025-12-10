package com.unigo.persistence.entities;

import com.unigo.security.user.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "mensaje")
@Getter
@Setter
@NoArgsConstructor
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_remitente")
    private int idRemitente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_remitente", referencedColumnName = "id", insertable = false, updatable = false)
    private Usuario remitente;

    @Column(name = "id_destinatario")
    private int idDestinatario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_destinatario", referencedColumnName = "id", insertable = false, updatable = false)
    private Usuario destinatario;

    @Column(name = "id_viaje")
    private int idViaje;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_viaje", referencedColumnName = "id", insertable = false, updatable = false)
    private Viaje viaje;

    @Column(nullable = false)
    private String texto;

    @Column(name = "fecha_envio",  nullable = false)
    private LocalDate fechaEnvio;

}
