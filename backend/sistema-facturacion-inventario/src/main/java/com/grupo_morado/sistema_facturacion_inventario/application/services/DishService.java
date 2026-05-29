package com.grupo_morado.sistema_facturacion_inventario.application.services;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.DishResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.in.DishUseCase;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.DishEventPublisherPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.DishProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.MenuProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.DishAlreadyActiveException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.DishAlreadyInactiveException;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidFieldException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.DishCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.DishUpdateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Dish;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Menu;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers.DishMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Servicio que implementa la lógica de negocio del módulo de Gestión de Platos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DishService implements DishUseCase {

    private final DishProviderPort dishProviderPort;
    private final MenuProviderPort menuProviderPort;
    private final DishEventPublisherPort dishEventPublisherPort;
    private final DishMapper dishMapper;

    @Override
    @Transactional
    public DishResultDTO createDish(DishCreateDTO dto) {
        log.info("Iniciando registro de plato con nombre: {}", dto.name());

        // Validar nombre obligatorio
        if (dto.name() == null || dto.name().trim().isEmpty()) {
            throw new InvalidFieldException("El nombre del plato es obligatorio.");
        }

        // Validar precio obligatorio
        if (dto.price() == null) {
            throw new InvalidFieldException("El precio del plato es obligatorio.");
        }

        // Validar precio no negativo
        if (dto.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidFieldException("El precio no puede ser negativo.");
        }

        // Validar stock no negativo (si se envía)
        if (dto.stock() != null && dto.stock() < 0) {
            throw new InvalidFieldException("El stock inicial no puede ser negativo.");
        }

        // Si stock no se envía, usar valor por defecto 0
        Integer finalStock = dto.stock() == null ? 0 : dto.stock();

        // Validar existencia del menú
        if (dto.menuId() == null) {
            throw new InvalidFieldException("El menú asociado es obligatorio.");
        }
        Menu menu = menuProviderPort.findById(dto.menuId())
                .orElseThrow(() -> new NotFoundException("El menú con id '" + dto.menuId() + "' no fue encontrado."));

        // Validar que el menú esté ACTIVO. Evitar registrar platos asociados a menús INACTIVOS.
        if (menu.getStatus() != StatusEnum.ACTIVO) {
            throw new InvalidFieldException("No se pueden registrar platos asociados a menús INACTIVOS.");
        }

        // Crear plato en estado ACTIVO por defecto
        Dish dish = new Dish();
        dish.setName(dto.name().trim());
        dish.setDescription(dto.description() != null ? dto.description().trim() : null);
        dish.setPrice(dto.price());
        dish.setStock(finalStock);
        dish.setStatus(StatusEnum.ACTIVO);
        dish.setMenu(menu);

        // Persistir usando JPA
        Dish savedDish = dishProviderPort.save(dish);
        DishResultDTO result = dishMapper.entityToResult(savedDish);

        // Emitir actualización realtime
        dishEventPublisherPort.publishDishRefreshEvent();

        log.info("Plato con nombre: {} registrado exitosamente con ID: {}", dto.name(), savedDish.getId());
        return result;
    }

    @Override
    @Transactional
    public DishResultDTO updateDish(Long id, DishUpdateDTO dto) {
        log.info("Iniciando modificación de plato con ID: {}", id);

        // Validar existencia del plato
        Dish dish = dishProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("El plato con id '" + id + "' no fue encontrado."));

        // Validar nombre obligatorio
        if (dto.name() == null || dto.name().trim().isEmpty()) {
            throw new InvalidFieldException("El nombre del plato es obligatorio.");
        }

        // Validar precio obligatorio
        if (dto.price() == null) {
            throw new InvalidFieldException("El precio del plato es obligatorio.");
        }

        // Validar precio no negativo
        if (dto.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidFieldException("El precio no puede ser negativo.");
        }

        // Validar stock obligatorio y no negativo
        if (dto.stock() == null) {
            throw new InvalidFieldException("El stock es obligatorio.");
        }
        if (dto.stock() < 0) {
            throw new InvalidFieldException("El stock no puede ser negativo.");
        }

        // Validar menú existente
        if (dto.menuId() == null) {
            throw new InvalidFieldException("El menú asociado es obligatorio.");
        }
        Menu menu = menuProviderPort.findById(dto.menuId())
                .orElseThrow(() -> new NotFoundException("El menú con id '" + dto.menuId() + "' no fue encontrado."));

        // Impedir asociar a menú INACTIVO
        if (menu.getStatus() != StatusEnum.ACTIVO) {
            throw new InvalidFieldException("No se puede asociar un plato a un menú INACTIVO.");
        }

        // Modificar plato
        dish.setName(dto.name().trim());
        dish.setDescription(dto.description() != null ? dto.description().trim() : null);
        dish.setPrice(dto.price());
        dish.setStock(dto.stock());
        dish.setMenu(menu);

        // Persistir cambios
        Dish updatedDish = dishProviderPort.save(dish);
        DishResultDTO result = dishMapper.entityToResult(updatedDish);

        // Emitir actualización realtime
        dishEventPublisherPort.publishDishRefreshEvent();

        log.info("Plato con ID: {} modificado exitosamente.", id);
        return result;
    }

    @Override
    @Transactional
    public void deactivateDish(Long id) {
        log.info("Iniciando desactivación de plato con ID: {}", id);

        // Buscar plato existente
        Dish dish = dishProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("El plato con id '" + id + "' no fue encontrado."));

        // Validar si ya está inactivo
        if (dish.getStatus() == StatusEnum.INACTIVO) {
            throw new DishAlreadyInactiveException("El plato ya se encuentra inactivo.");
        }

        // Cambiar estado a INACTIVO
        dish.setStatus(StatusEnum.INACTIVO);
        dishProviderPort.save(dish);

        // Emitir actualización realtime
        dishEventPublisherPort.publishDishRefreshEvent();

        log.info("Plato con ID: {} desactivado exitosamente (soft delete).", id);
    }

    @Override
    @Transactional
    public void reactivateDish(Long id) {
        log.info("Iniciando reactivación de plato con ID: {}", id);

        // Buscar plato existente
        Dish dish = dishProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("El plato con id '" + id + "' no fue encontrado."));

        // Validar si ya está activo
        if (dish.getStatus() == StatusEnum.ACTIVO) {
            throw new DishAlreadyActiveException("El plato ya se encuentra activo.");
        }

        // Validar que el menú asociado no esté inactivo
        Menu menu = dish.getMenu();
        if (menu != null && menu.getStatus() == StatusEnum.INACTIVO) {
            throw new InvalidFieldException("No se puede reactivar el plato porque el menú asociado está INACTIVO.");
        }

        // Cambiar estado a ACTIVO
        dish.setStatus(StatusEnum.ACTIVO);
        dishProviderPort.save(dish);

        // Emitir actualización realtime
        dishEventPublisherPort.publishDishRefreshEvent();

        log.info("Plato con ID: {} reactivado exitosamente.", id);
    }

    @Override
    @Transactional(readOnly = true)
    public DishResultDTO getDishById(Long id) {
        log.info("Consultando detalle de plato con ID: {}", id);

        // Buscar plato existente
        Dish dish = dishProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("El plato con id '" + id + "' no fue encontrado."));

        return dishMapper.entityToResult(dish);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResultDTO<DishResultDTO> getDishes(String name, StatusEnum status, Pageable pageable) {
        log.info("Consultando platos paginados con filtros - Nombre: {}, Estado: {}, Paginación: {}", name, status, pageable);

        // Limpiar el filtro de nombre (si es vacío, usar null para omitir el filtro en la consulta)
        String cleanName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;

        Page<Dish> page = dishProviderPort.findByNameAndStatus(cleanName, status, pageable);
        java.util.List<DishResultDTO> content = page.getContent().stream()
                .map(dishMapper::entityToResult)
                .toList();

        return new PageResultDTO<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<DishResultDTO> getAvailableDishes() {
        log.info("Consultando platos disponibles para órdenes (ACTIVOS con stock > 0).");
        return dishProviderPort.findAllActiveWithStock().stream()
                .map(dishMapper::entityToResult)
                .toList();
    }
}
