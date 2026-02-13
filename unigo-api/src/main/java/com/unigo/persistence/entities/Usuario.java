package com.unigo.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "username")
})
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true,  nullable = false)
    private String username;

    @Column(unique = true,  nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String rol;

    @OneToMany(mappedBy = "remitente")
    @JsonIgnore
    private List<Mensaje> enviados;

    @OneToMany(mappedBy = "destinatario")
    @JsonIgnore
    private List<Mensaje> recibidos;
}
