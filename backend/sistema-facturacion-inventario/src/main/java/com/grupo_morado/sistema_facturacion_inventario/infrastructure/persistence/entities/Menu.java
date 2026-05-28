package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "menu")
@Getter
@Setter
public class Menu extends BaseEntity{

    @Column(name = "nombre")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private StatusEnum status;

}
