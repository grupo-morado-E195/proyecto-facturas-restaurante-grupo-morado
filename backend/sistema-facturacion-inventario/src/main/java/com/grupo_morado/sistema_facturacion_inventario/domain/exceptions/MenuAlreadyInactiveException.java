package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar desactivar un menú que ya está inactivo.
 * Resulta en HTTP 409 Conflict.
 */
public class MenuAlreadyInactiveException extends RuntimeException {
    public MenuAlreadyInactiveException(String message) {
        super(message);
    }
}
