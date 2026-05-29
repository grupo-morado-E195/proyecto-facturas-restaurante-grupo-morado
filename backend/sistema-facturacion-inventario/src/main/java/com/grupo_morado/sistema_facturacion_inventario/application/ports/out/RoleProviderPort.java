package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Role;

import java.util.List;
import java.util.Optional;

public interface RoleProviderPort {
    Optional<Role> findById(Long id);
    List<Role> findAll();
    Role save(Role role);
    void deleteById(Long id);
}
