package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthLoginResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.domain.models.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Role;

public interface AuthProviderPort {
    AuthLoginResultDTO authenticate(String email, String password);
    com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User register(User user, Role role, String passwordEncoded);
}
