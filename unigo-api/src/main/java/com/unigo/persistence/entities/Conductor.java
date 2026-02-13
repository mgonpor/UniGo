package com.unigo.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "conductor")
@Getter
@Setter
@NoArgsConstructor
public class Conductor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_usuario")
    private int idUsuario;

    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Usuario usuario;

    private float reputacion;

    @OneToMany(mappedBy = "conductor")
    private List<Vehiculo> vehiculos;

    @OneToMany(mappedBy = "conductor")
    @JsonIgnore
    private List<Viaje> viajes;

}
