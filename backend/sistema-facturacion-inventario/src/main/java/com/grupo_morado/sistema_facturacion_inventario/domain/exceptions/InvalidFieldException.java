package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

public class InvalidFieldException extends RuntimeException{
    public InvalidFieldException(String message){
        super(message);
    }
}
