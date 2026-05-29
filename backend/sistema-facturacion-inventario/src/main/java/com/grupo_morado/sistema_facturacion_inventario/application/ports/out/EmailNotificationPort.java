package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

/**
 * Puerto de salida para el envío de notificaciones por correo electrónico.
 * Desacopla la capa de aplicación de la implementación SMTP concreta.
 */
public interface EmailNotificationPort {

    /**
     * Envía un correo electrónico con la contraseña temporal generada.
     *
     * @param toEmail          Dirección de correo del destinatario.
     * @param temporaryPassword Contraseña temporal en texto plano (antes de hashear).
     */
    void sendTemporaryPasswordEmail(String toEmail, String temporaryPassword);
}
