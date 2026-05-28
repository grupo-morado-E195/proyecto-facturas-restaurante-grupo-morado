package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.DisponibilityStateEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@jakarta.persistence.Table(name = "mesa")
@Getter
@Setter
public class Table extends  BaseEntity{

    @Column(name = "numero_mesa")
    private Integer number;

    @Column(name = "estado_disponibilidad")
    @Enumerated(EnumType.STRING)
    private DisponibilityStateEnum disponibility;

    @Column(name = "estado_registro")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

}
