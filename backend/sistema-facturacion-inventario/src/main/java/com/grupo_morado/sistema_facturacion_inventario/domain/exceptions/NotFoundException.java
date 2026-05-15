package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
