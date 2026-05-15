package com.grupo_morado.sistema_facturacion_inventario.domain.exceptions;

public class InvalidPasswordException extends RuntimeException{

    public InvalidPasswordException(String message){
        super(message);
    }
}
