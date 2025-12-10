package com.unigo.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unigo.security.user.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "pasajero")
@Getter
@Setter
@NoArgsConstructor
public class Pasajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_usuario")
    private int idUsuario;

    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Usuario usuario;

    @OneToMany(mappedBy = "pasajero")
    @JsonIgnore
    List<Reserva> reservas;

}
