package com.unigo.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String nombre;

    @Column(name = "nombre_usuario", unique = true,  nullable = false)
    private String nombreUsuario;

    @Column(unique = true,  nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "remitente")
    @JsonIgnore
    private List<Mensaje> enviados;

    @OneToMany(mappedBy = "destinatario")
    @JsonIgnore
    private List<Mensaje> recibidos;

}
