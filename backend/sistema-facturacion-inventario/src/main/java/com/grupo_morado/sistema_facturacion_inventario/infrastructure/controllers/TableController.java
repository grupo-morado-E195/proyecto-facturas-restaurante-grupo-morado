package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.TableResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.services.TableService;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.TableCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.TableUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para el módulo de Gestión de Mesas.
 * Expone endpoints administrativos para crear, modificar, desactivar y reactivar mesas,
 * así como consultar el listado paginado.
 */
@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
@Slf4j
public class TableController {

    private final TableService tableService;

    /**
     * Crea una nueva mesa.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PostMapping
    public ResponseEntity<TableResultDTO> createTable(@RequestBody @Valid TableCreateDTO dto) {
        log.info("REST request para crear una nueva mesa.");
        TableResultDTO result = tableService.createTable(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Modifica el número de una mesa existente.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TableResultDTO> updateTable(@PathVariable Long id, @RequestBody @Valid TableUpdateDTO dto) {
        log.info("REST request para modificar la mesa con ID: {}.", id);
        TableResultDTO result = tableService.updateTable(id, dto);
        return ResponseEntity.ok(result);
    }

    /**
     * Desactiva una mesa (soft delete).
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Map<String, String>> deactivateTable(@PathVariable Long id) {
        log.info("REST request para desactivar la mesa con ID: {}.", id);
        tableService.deactivateTable(id);
        return ResponseEntity.ok(Map.of("message", "Mesa desactivada correctamente."));
    }

    /**
     * Reactiva una mesa desactivada.
     * Endpoint restringido a ADMINISTRADOR.
     */
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Map<String, String>> reactivateTable(@PathVariable Long id) {
        log.info("REST request para reactivar la mesa con ID: {}.", id);
        tableService.reactivateTable(id);
        return ResponseEntity.ok(Map.of("message", "Mesa reactivada correctamente."));
    }

    /**
     * Retorna una página de mesas registradas en el sistema.
     * Disponible para todos los usuarios autenticados.
     */
    @GetMapping
    public ResponseEntity<PageResultDTO<TableResultDTO>> getTables(Pageable pageable) {
        log.info("REST request para obtener listado de mesas paginado: {}.", pageable);
        PageResultDTO<TableResultDTO> result = tableService.getTables(pageable);
        return ResponseEntity.ok(result);
    }
}
