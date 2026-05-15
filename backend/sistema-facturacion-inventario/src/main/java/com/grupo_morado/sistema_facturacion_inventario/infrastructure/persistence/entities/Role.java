package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rol")
@Getter
@Setter
public class Role extends BaseEntity{

    @Column(name = "nombre")
    private String name;

}
