package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar reactivar un plato que ya está activo.
 * Resulta en HTTP 409 Conflict.
 */
public class DishAlreadyActiveException extends RuntimeException {
    public DishAlreadyActiveException(String message) {
        super(message);
    }
}
