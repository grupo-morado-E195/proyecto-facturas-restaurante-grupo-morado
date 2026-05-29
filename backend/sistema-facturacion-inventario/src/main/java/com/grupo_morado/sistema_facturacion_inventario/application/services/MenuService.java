package com.grupo_morado.sistema_facturacion_inventario.application.services;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.MenuResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.in.MenuUseCase;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.DishEventPublisherPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.DishProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.MenuEventPublisherPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.MenuProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidFieldException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.MenuAlreadyActiveException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.MenuAlreadyInactiveException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.MenuNameAlreadyExistsException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.NotFoundException;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.MenuCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.MenuUpdateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Dish;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Menu;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers.MenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio que implementa la lógica de negocio del módulo de Gestión de Menús.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService implements MenuUseCase {

    private final MenuProviderPort menuProviderPort;
    private final MenuEventPublisherPort menuEventPublisherPort;
    private final MenuMapper menuMapper;
    private final DishProviderPort dishProviderPort;
    private final DishEventPublisherPort dishEventPublisherPort;

    @Override
    @Transactional
    public MenuResultDTO createMenu(MenuCreateDTO dto) {
        log.info("Iniciando creación de menú con nombre: {}", dto.name());

        // Validar que el nombre no sea nulo o vacío
        if (dto.name() == null || dto.name().trim().isEmpty()) {
            throw new InvalidFieldException("El nombre del menú es obligatorio.");
        }

        // Validar unicidad del nombre del menú
        if (menuProviderPort.findByName(dto.name().trim()).isPresent()) {
            throw new MenuNameAlreadyExistsException(
                    "El menú con el nombre '" + dto.name().trim() + "' ya está registrado en el sistema."
            );
        }

        // Crear la entidad con estado ACTIVO por defecto
        Menu menu = new Menu();
        menu.setName(dto.name().trim());
        menu.setStatus(StatusEnum.ACTIVO);

        Menu savedMenu = menuProviderPort.save(menu);
        MenuResultDTO result = menuMapper.entityToResult(savedMenu);

        // Publicar evento en tiempo real para refrescar tabla/listado
        menuEventPublisherPort.publishMenuRefreshEvent();

        log.info("Menú con nombre: {} creado exitosamente con ID: {}", dto.name(), savedMenu.getId());
        return result;
    }

    @Override
    @Transactional
    public MenuResultDTO updateMenu(Long id, MenuUpdateDTO dto) {
        log.info("Iniciando modificación de menú con ID: {} al nombre: {}", id, dto.name());

        // Validar existencia del menú
        Menu menu = menuProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("El menú con id '" + id + "' no fue encontrado."));

        // Validar que el nombre no sea nulo o vacío
        if (dto.name() == null || dto.name().trim().isEmpty()) {
            throw new InvalidFieldException("El nombre del menú es obligatorio.");
        }

        String trimmedNewName = dto.name().trim();

        // Validar duplicados si el nombre cambia
        if (!menu.getName().equalsIgnoreCase(trimmedNewName)) {
            if (menuProviderPort.findByName(trimmedNewName).isPresent()) {
                throw new MenuNameAlreadyExistsException(
                        "El menú con el nombre '" + trimmedNewName + "' ya está registrado en el sistema."
                );
            }
            menu.setName(trimmedNewName);
        }

        Menu updatedMenu = menuProviderPort.save(menu);
        MenuResultDTO result = menuMapper.entityToResult(updatedMenu);

        // Publicar evento en tiempo real para refrescar tabla/listado
        menuEventPublisherPort.publishMenuRefreshEvent();

        log.info("Menú con ID: {} modificado exitosamente al nombre: {}", id, trimmedNewName);
        return result;
    }

    @Override
    @Transactional
    public void deactivateMenu(Long id) {
        log.info("Iniciando desactivación del menú con ID: {}", id);

        // Buscar menú existente
        Menu menu = menuProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("El menú con id '" + id + "' no fue encontrado."));

        // Validar si ya está inactivo
        if (menu.getStatus() == StatusEnum.INACTIVO) {
            throw new MenuAlreadyInactiveException("El menú ya se encuentra inactivo.");
        }

        // Cambiar estado a INACTIVO
        menu.setStatus(StatusEnum.INACTIVO);
        menuProviderPort.save(menu);

        // Si el menú tiene platos asociados activos, pasarlos automáticamente a PAUSADO
        List<Dish> activeDishes = dishProviderPort.findByMenuIdAndStatus(id, StatusEnum.ACTIVO);
        if (!activeDishes.isEmpty()) {
            activeDishes.forEach(dish -> dish.setStatus(StatusEnum.PAUSADO));
            dishProviderPort.saveAll(activeDishes);
            // Emitir evento WebSocket para refrescar platos
            dishEventPublisherPort.publishDishRefreshEvent();
        }

        // Publicar evento en tiempo real para refrescar menús
        menuEventPublisherPort.publishMenuRefreshEvent();

        log.info("Menú con ID: {} desactivado exitosamente. Se pausaron {} platos asociados.", id, activeDishes.size());
    }

    @Override
    @Transactional
    public void reactivateMenu(Long id) {
        log.info("Iniciando reactivación del menú con ID: {}", id);

        // Buscar menú existente
        Menu menu = menuProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("El menú con id '" + id + "' no fue encontrado."));

        // Validar si ya está activo
        if (menu.getStatus() == StatusEnum.ACTIVO) {
            throw new MenuAlreadyActiveException("El menú ya se encuentra activo.");
        }

        // Cambiar estado a ACTIVO
        menu.setStatus(StatusEnum.ACTIVO);
        menuProviderPort.save(menu);

        // Reactivar automáticamente los platos asociados que estaban en estado PAUSADO
        List<Dish> pausedDishes = dishProviderPort.findByMenuIdAndStatus(id, StatusEnum.PAUSADO);
        if (!pausedDishes.isEmpty()) {
            pausedDishes.forEach(dish -> dish.setStatus(StatusEnum.ACTIVO));
            dishProviderPort.saveAll(pausedDishes);
            // Emitir evento WebSocket para refrescar platos
            dishEventPublisherPort.publishDishRefreshEvent();
        }

        // Publicar evento en tiempo real para refrescar menús
        menuEventPublisherPort.publishMenuRefreshEvent();

        log.info("Menú con ID: {} reactivado exitosamente. Se reactivaron {} platos asociados.", id, pausedDishes.size());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResultDTO<MenuResultDTO> getMenus(Pageable pageable) {
        log.info("Consultando menús paginados: {}", pageable);

        Page<Menu> page = menuProviderPort.findAll(pageable);
        List<MenuResultDTO> content = page.getContent().stream()
                .map(menuMapper::entityToResult)
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
