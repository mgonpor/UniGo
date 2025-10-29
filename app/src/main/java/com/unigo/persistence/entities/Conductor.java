package com.unigo.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "conductor")
@Getter
@Setter
@NoArgsConstructor
public class Conductor extends Usuario {

    private float reputacion;

}
