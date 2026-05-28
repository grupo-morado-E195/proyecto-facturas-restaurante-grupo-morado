package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@jakarta.persistence.Table(name = "orden")
@Getter
@Setter
public class Order extends BaseEntity{

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private OrderStatusEnum status;

    @Column(name = "subtotal")
    private BigDecimal subtotal;

    @Column(name = "impuesto_consumo")
    private BigDecimal consumptionTax;

    @Column(name = "total")
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "id_mesa")
    private Table table;

    @ManyToOne
    @JoinColumn(name = "id_mesero")
    private User waiter;
}