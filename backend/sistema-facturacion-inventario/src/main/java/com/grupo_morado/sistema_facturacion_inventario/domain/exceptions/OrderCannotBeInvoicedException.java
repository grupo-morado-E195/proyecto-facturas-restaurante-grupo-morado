package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

/**
 * Excepción lanzada al intentar facturar una orden cuyo estado no es LISTO.
 * Resulta en HTTP 409 Conflict.
 */
public class OrderCannotBeInvoicedException extends RuntimeException {
    public OrderCannotBeInvoicedException(String message) {
        super(message);
    }
}
