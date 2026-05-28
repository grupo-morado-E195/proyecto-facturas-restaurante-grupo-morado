package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

public class InvalidEmailException extends RuntimeException{
    public InvalidEmailException(String message){
        super(message);
    }
}
