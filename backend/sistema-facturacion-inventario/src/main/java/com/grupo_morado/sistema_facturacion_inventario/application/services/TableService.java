package com.grupo_morado.sistema_facturacion_inventario.application.services;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.TableResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.in.TableUseCase;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.TableEventPublisherPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.TableProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.DisponibilityStateEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.*;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.TableCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.TableUpdateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Table;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers.TableMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio que implementa la lógica de negocio del módulo de Gestión de Mesas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TableService implements TableUseCase {

    private final TableProviderPort tableProviderPort;
    private final TableEventPublisherPort tableEventPublisherPort;
    private final TableMapper tableMapper;

    @Override
    @Transactional
    public TableResultDTO createTable(TableCreateDTO dto) {
        log.info("Iniciando creación de mesa número: {}", dto.number());

        // Validar que el número no sea negativo
        if (dto.number() < 0) {
            throw new InvalidFieldException("El número de mesa no puede ser negativo.");
        }

        // Validar unicidad del número de mesa
        if (tableProviderPort.findByNumber(dto.number()).isPresent()) {
            throw new TableNumberAlreadyExistsException(
                    "La mesa con el número " + dto.number() + " ya está registrada en el sistema."
            );
        }

        // Crear la entidad con estado ACTIVO y disponibilidad DISPONIBLE por defecto
        Table table = new Table();
        table.setNumber(dto.number());
        table.setStatus(StatusEnum.ACTIVO);
        table.setDisponibility(DisponibilityStateEnum.DISPONIBLE);

        Table savedTable = tableProviderPort.save(table);
        TableResultDTO result = tableMapper.entityToResult(savedTable);

        // Publicar evento en tiempo real
        tableEventPublisherPort.publishTableEvent(result);

        log.info("Mesa número: {} creada exitosamente con ID: {}", dto.number(), savedTable.getId());
        return result;
    }

    @Override
    @Transactional
    public TableResultDTO updateTable(Long id, TableUpdateDTO dto) {
        log.info("Iniciando modificación de la mesa con ID: {} al número: {}", id, dto.number());

        // Validar que el número no sea negativo
        if (dto.number() < 0) {
            throw new InvalidFieldException("El número de mesa no puede ser negativo.");
        }

        // Buscar mesa existente
        Table table = tableProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("La mesa con id '" + id + "' no fue encontrada."));

        // Validar duplicados si el número cambia
        if (!table.getNumber().equals(dto.number())) {
            if (tableProviderPort.findByNumber(dto.number()).isPresent()) {
                throw new TableNumberAlreadyExistsException(
                        "La mesa con el número " + dto.number() + " ya está registrada en el sistema."
                );
            }
            table.setNumber(dto.number());
        }

        Table updatedTable = tableProviderPort.save(table);
        TableResultDTO result = tableMapper.entityToResult(updatedTable);

        // Publicar evento en tiempo real
        tableEventPublisherPort.publishTableEvent(result);

        log.info("Mesa con ID: {} modificada exitosamente al número: {}", id, dto.number());
        return result;
    }

    @Override
    @Transactional
    public void deactivateTable(Long id) {
        log.info("Iniciando desactivación de la mesa con ID: {}", id);

        // Buscar mesa existente
        Table table = tableProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("La mesa con id '" + id + "' no fue encontrada."));

        // Validar si ya está inactiva
        if (table.getStatus() == StatusEnum.INACTIVO) {
            throw new TableAlreadyInactiveException("La mesa ya se encuentra inactiva.");
        }

        // Validar que no esté ocupada
        if (table.getDisponibility() == DisponibilityStateEnum.OCUPADA) {
            throw new TableOccupiedException("No se puede desactivar la mesa porque está ocupada.");
        }

        // Cambiar estado a INACTIVO
        table.setStatus(StatusEnum.INACTIVO);

        Table updatedTable = tableProviderPort.save(table);
        TableResultDTO result = tableMapper.entityToResult(updatedTable);

        // Publicar evento en tiempo real
        tableEventPublisherPort.publishTableEvent(result);

        log.info("Mesa con ID: {} desactivada exitosamente.", id);
    }

    @Override
    @Transactional
    public void reactivateTable(Long id) {
        log.info("Iniciando reactivación de la mesa con ID: {}", id);

        // Buscar mesa existente
        Table table = tableProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("La mesa con id '" + id + "' no fue encontrada."));

        // Validar si ya está activa
        if (table.getStatus() == StatusEnum.ACTIVO) {
            throw new TableAlreadyActiveException("La mesa ya se encuentra activa.");
        }

        // Cambiar estado a ACTIVO
        table.setStatus(StatusEnum.ACTIVO);

        Table updatedTable = tableProviderPort.save(table);
        TableResultDTO result = tableMapper.entityToResult(updatedTable);

        // Publicar evento en tiempo real
        tableEventPublisherPort.publishTableEvent(result);

        log.info("Mesa con ID: {} reactivada exitosamente.", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResultDTO<TableResultDTO> getTables(Pageable pageable) {
        log.info("Consultando mesas paginadas: {}", pageable);

        Page<Table> page = tableProviderPort.findAll(pageable);
        List<TableResultDTO> content = page.getContent().stream()
                .map(tableMapper::entityToResult)
                .toList();

        return new PageResultDTO<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
