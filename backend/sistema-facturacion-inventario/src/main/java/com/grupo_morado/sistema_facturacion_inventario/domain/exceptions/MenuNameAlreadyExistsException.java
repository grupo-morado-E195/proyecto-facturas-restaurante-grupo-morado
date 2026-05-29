package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar registrar un menú con un nombre que ya está registrado.
 * Resulta en HTTP 409 Conflict.
 */
public class MenuNameAlreadyExistsException extends RuntimeException {
    public MenuNameAlreadyExistsException(String message) {
        super(message);
    }
}
