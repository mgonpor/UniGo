package com.unigo.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "conductor")
@Getter
@Setter
@NoArgsConstructor
public class Conductor extends Usuario {

    private float reputacion;

    @OneToMany(mappedBy = "conductor")
    private List<Vehiculo> vehiculos;

    @OneToMany(mappedBy = "conductor")
    @JsonIgnore
    private List<Viaje> viajes;

}
