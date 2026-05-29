package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

public interface PasswordEncryptPort {

    /** Hashea una contraseña en texto plano con BCrypt. */
    String encryptPassword(String password);

    /**
     * Verifica si una contraseña en texto plano coincide con su versión hasheada.
     *
     * @param rawPassword     Contraseña en texto plano proporcionada por el usuario.
     * @param encodedPassword Contraseña hasheada almacenada en base de datos.
     * @return {@code true} si las contraseñas coinciden.
     */
    boolean matches(String rawPassword, String encodedPassword);
}
