package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User;

import java.util.Optional;

/**
 * Puerto de salida para operaciones de persistencia de usuarios.
 * Desacopla la capa de aplicación de la implementación JPA concreta.
 */
public interface UserProviderPort {

    Optional<User> findByEmail(String email);

    User save(User user);
}
