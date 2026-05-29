package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.MenuResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.services.MenuService;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.MenuCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.MenuUpdateDTO;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el módulo de Gestión de Menús.
 * Expone endpoints para la administración de menús.
 */
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Slf4j
public class MenuController {

    private final MenuService menuService;

    /**
     * Crea un nuevo menú.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PostMapping
    public ResponseEntity<MenuResultDTO> createMenu(@RequestBody @Valid MenuCreateDTO dto) {
        log.info("REST request para crear un nuevo menú.");
        MenuResultDTO result = menuService.createMenu(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Modifica el nombre de un menú existente.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuResultDTO> updateMenu(@PathVariable Long id, @RequestBody @Valid MenuUpdateDTO dto) {
        log.info("REST request para modificar el menú con ID: {}.", id);
        MenuResultDTO result = menuService.updateMenu(id, dto);
        return ResponseEntity.ok(result);
    }

    /**
     * Desactiva un menú (soft delete).
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<java.util.Map<String, String>> deactivateMenu(@PathVariable Long id) {
        log.info("REST request para desactivar el menú con ID: {}.", id);
        menuService.deactivateMenu(id);
        return ResponseEntity.ok(java.util.Map.of("message", "Menú desactivado correctamente."));
    }

    /**
     * Reactiva un menú desactivado.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<java.util.Map<String, String>> reactivateMenu(@PathVariable Long id) {
        log.info("REST request para reactivar el menú con ID: {}.", id);
        menuService.reactivateMenu(id);
        return ResponseEntity.ok(java.util.Map.of("message", "Menú reactivado correctamente."));
    }

    /**
     * Retorna una página de menús registrados en el sistema.
     * Disponible para todos los usuarios autenticados.
     */
    @GetMapping
    public ResponseEntity<PageResultDTO<MenuResultDTO>> getMenus(Pageable pageable) {
        log.info("REST request para obtener listado de menús paginado: {}.", pageable);
        PageResultDTO<MenuResultDTO> result = menuService.getMenus(pageable);
        return ResponseEntity.ok(result);
    }
}
