package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar crear o modificar una mesa con un número
 * que ya está registrado en el sistema.
 * Resulta en HTTP 409 Conflict.
 */
public class TableNumberAlreadyExistsException extends RuntimeException {
    public TableNumberAlreadyExistsException(String message) {
        super(message);
    }
}
