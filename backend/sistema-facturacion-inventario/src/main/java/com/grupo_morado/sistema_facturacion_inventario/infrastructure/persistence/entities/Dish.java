package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "plato")
public class Dish extends BaseEntity{

    @Column(name = "nombre")
    private String name;

    @Column(name = "descripcion")
    private String description;

    @Column(name = "precio")
    private BigDecimal price;

    @Column(name = "stock")
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private StatusEnum status;

    @ManyToOne
    @JoinColumn(name = "id_menu")
    private Menu menu;
}
