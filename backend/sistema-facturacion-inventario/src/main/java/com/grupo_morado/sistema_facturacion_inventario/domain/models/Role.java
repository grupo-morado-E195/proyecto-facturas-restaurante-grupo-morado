package com.grupo_morado.sistema_facturacion_inventario.domain.models;

import lombok.Getter;

@Getter
public class Role {
    private final Long id;
    private final String name;

    public Role(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
