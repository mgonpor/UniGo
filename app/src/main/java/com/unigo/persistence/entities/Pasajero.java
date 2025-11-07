package com.unigo.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Pasajero extends Usuario {

    @OneToMany(mappedBy = "pasajero")
    @JsonIgnore
    List<Reserva> reservas;

}
