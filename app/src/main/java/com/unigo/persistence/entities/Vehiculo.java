package com.unigo.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehiculo")
@Getter
@Setter
@NoArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_conductor")
    private int idConductor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_conductor", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Conductor conductor;

    private String marca;
    private String modelo;
    private String matricula;

}
