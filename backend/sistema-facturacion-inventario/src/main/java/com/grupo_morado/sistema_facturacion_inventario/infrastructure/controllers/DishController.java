package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.DishResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.services.DishService;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import org.springframework.data.domain.Pageable;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.DishCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.DishUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el módulo de Gestión de Platos.
 * Expone endpoints para el registro y modificación de platos.
 */
@RestController
@RequestMapping("/api/platos")
@RequiredArgsConstructor
@Slf4j
public class DishController {

    private final DishService dishService;

    /**
     * Registra un nuevo plato.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PostMapping
    public ResponseEntity<DishResultDTO> createDish(@RequestBody @Valid DishCreateDTO dto) {
        log.info("REST request para registrar un nuevo plato.");
        DishResultDTO result = dishService.createDish(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Modifica los datos de un plato existente.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DishResultDTO> updateDish(@PathVariable Long id, @RequestBody @Valid DishUpdateDTO dto) {
        log.info("REST request para modificar el plato con ID: {}.", id);
        DishResultDTO result = dishService.updateDish(id, dto);
        return ResponseEntity.ok(result);
    }

    /**
     * Desactiva un plato (soft delete).
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<java.util.Map<String, String>> deactivateDish(@PathVariable Long id) {
        log.info("REST request para desactivar el plato con ID: {}.", id);
        dishService.deactivateDish(id);
        return ResponseEntity.ok(java.util.Map.of("message", "Plato desactivado correctamente."));
    }

    /**
     * Reactiva un plato inactivo.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<java.util.Map<String, String>> reactivateDish(@PathVariable Long id) {
        log.info("REST request para reactivar el plato con ID: {}.", id);
        dishService.reactivateDish(id);
        return ResponseEntity.ok(java.util.Map.of("message", "Plato reactivado correctamente."));
    }

    /**
     * Retorna el detalle de un plato por su identificador.
     * Disponible para todos los usuarios autenticados.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DishResultDTO> getDishById(@PathVariable Long id) {
        log.info("REST request para obtener el detalle del plato con ID: {}.", id);
        DishResultDTO result = dishService.getDishById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * Retorna una página de platos filtrada opcionalmente por nombre y estado.
     * Disponible para todos los usuarios autenticados.
     */
    @GetMapping
    public ResponseEntity<PageResultDTO<DishResultDTO>> getDishes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) StatusEnum status,
            Pageable pageable
    ) {
        log.info("REST request para obtener listado de platos paginado y filtrado. Nombre: {}, Estado: {}, Paginación: {}.", name, status, pageable);
        PageResultDTO<DishResultDTO> result = dishService.getDishes(name, status, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * Retorna todos los platos disponibles para selección en órdenes: ACTIVOS con stock mayor a 0.
     * Usado por MESERO para poblar el dropdown al crear o modificar una orden (SFR-006).
     */
    @GetMapping("/disponibles")
    public ResponseEntity<java.util.List<DishResultDTO>> getAvailableDishes() {
        log.info("REST request para obtener platos disponibles para órdenes (ACTIVOS con stock > 0).");
        java.util.List<DishResultDTO> result = dishService.getAvailableDishes();
        return ResponseEntity.ok(result);
    }
}
