package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.UserResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.services.UserService;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.UserCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.UserUpdateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el módulo de Gestión de Usuarios (SFR-008).
 * Todos los endpoints están restringidos a ADMINISTRADOR, configurado en SecurityConfig.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * SFR-008 #1 — Registrar usuario.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PostMapping
    public ResponseEntity<UserResultDTO> createUser(@RequestBody @Valid UserCreateDTO dto) {
        log.info("REST request para registrar un nuevo usuario.");
        UserResultDTO result = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * SFR-008 #2 — Modificar usuario (nombre, apellidos, rol).
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResultDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDTO dto
    ) {
        log.info("REST request para modificar el usuario con ID: {}.", id);
        UserResultDTO result = userService.updateUser(id, dto);
        return ResponseEntity.ok(result);
    }

    /**
     * SFR-008 #3 — Desactivar usuario.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<UserResultDTO> deactivateUser(@PathVariable Long id) {
        log.info("REST request para desactivar el usuario con ID: {}.", id);
        UserResultDTO result = userService.deactivateUser(id);
        return ResponseEntity.ok(result);
    }

    /**
     * SFR-008 #4 — Reactivar usuario.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<UserResultDTO> reactivateUser(@PathVariable Long id) {
        log.info("REST request para reactivar el usuario con ID: {}.", id);
        UserResultDTO result = userService.reactivateUser(id);
        return ResponseEntity.ok(result);
    }

    /**
     * SFR-008 #5 — Obtener detalle de un usuario.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResultDTO> getUserById(@PathVariable Long id) {
        log.info("REST request para obtener el detalle del usuario con ID: {}.", id);
        UserResultDTO result = userService.getUserById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * SFR-008 #6 — Listar todos los usuarios paginados.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @GetMapping
    public ResponseEntity<PageResultDTO<UserResultDTO>> getUsers(Pageable pageable) {
        log.info("REST request para obtener listado de usuarios paginado: {}.", pageable);
        PageResultDTO<UserResultDTO> result = userService.getUsers(pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * Listar todos los roles disponibles (para el dropdown de creación/edición de usuarios).
     * Endpoint restringido a ADMINISTRADOR.
     */
    @GetMapping("/roles")
    public ResponseEntity<List<Map<String, Object>>> getRoles() {
        log.info("REST request para listar todos los roles disponibles.");
        List<Role> roles = userService.getRoles();
        List<Map<String, Object>> result = roles.stream()
                .map(r -> Map.<String, Object>of("id", r.getId(), "name", r.getName()))
                .toList();
        return ResponseEntity.ok(result);
    }

    /**
     * Registrar un nuevo rol en el sistema.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PostMapping("/roles")
    public ResponseEntity<Map<String, Object>> createRole(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        log.info("REST request para crear un nuevo rol con nombre: {}.", name);
        Role created = userService.createRole(name);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", created.getId(), "name", created.getName()));
    }

    /**
     * Modificar un rol existente por ID.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PutMapping("/roles/{id}")
    public ResponseEntity<Map<String, Object>> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String name = body.get("name");
        log.info("REST request para modificar el rol con ID: {} a nuevo nombre: {}.", id, name);
        Role updated = userService.updateRole(id, name);
        return ResponseEntity.ok(Map.of("id", updated.getId(), "name", updated.getName()));
    }

    /**
     * Eliminar un rol por ID.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Map<String, String>> deleteRole(@PathVariable Long id) {
        log.info("REST request para eliminar el rol con ID: {}.", id);
        userService.deleteRole(id);
        return ResponseEntity.ok(Map.of("message", "Rol eliminado correctamente."));
    }
}
