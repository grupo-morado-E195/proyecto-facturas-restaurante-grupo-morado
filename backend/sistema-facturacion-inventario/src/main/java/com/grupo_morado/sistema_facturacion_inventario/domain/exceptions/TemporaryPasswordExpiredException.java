package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

public class TemporaryPasswordExpiredException extends RuntimeException {
    public TemporaryPasswordExpiredException(String message) {
        super(message);
    }
}
