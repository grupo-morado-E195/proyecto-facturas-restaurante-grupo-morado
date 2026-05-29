package com.grupo_morado.sistema_facturacion_inventario.application.ports.in;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.UserResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.UserCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.UserUpdateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Role;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Puerto de entrada para los casos de uso del módulo de Gestión de Usuarios (SFR-008).
 */
public interface UserUseCase {

    /** SFR-008 #1 — Crear usuario (nombre, apellidos, email, rol). */
    UserResultDTO createUser(UserCreateDTO dto);

    /** SFR-008 #2 — Modificar nombre, apellidos y rol de un usuario existente. */
    UserResultDTO updateUser(Long id, UserUpdateDTO dto);

    /** SFR-008 #3 — Desactivar usuario (soft delete). */
    UserResultDTO deactivateUser(Long id);

    /** SFR-008 #4 — Reactivar usuario desactivado. */
    UserResultDTO reactivateUser(Long id);

    /** SFR-008 #5 — Mostrar usuario por ID. */
    UserResultDTO getUserById(Long id);

    /** SFR-008 #6 — Listar usuarios paginados. */
    PageResultDTO<UserResultDTO> getUsers(Pageable pageable);

    /** Listar todos los roles disponibles (para dropdown). */
    List<Role> getRoles();

    /** Crear un nuevo rol. */
    Role createRole(String name);

    /** Modificar un rol existente. */
    Role updateRole(Long id, String name);

    /** Eliminar un rol existente. */
    void deleteRole(Long id);
}
