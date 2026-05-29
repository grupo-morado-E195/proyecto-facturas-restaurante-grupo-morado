package com.grupo_morado.sistema_facturacion_inventario.application.services;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.MenuResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuProviderPort menuProviderPort;

    @Mock
    private MenuEventPublisherPort menuEventPublisherPort;

    @Mock
    private MenuMapper menuMapper;

    @Mock
    private DishProviderPort dishProviderPort;

    @Mock
    private DishEventPublisherPort dishEventPublisherPort;

    @InjectMocks
    private MenuService menuService;

    private MenuCreateDTO menuCreateDTO;
    private Menu menuEntity;
    private MenuResultDTO menuResultDTO;

    @BeforeEach
    void setUp() {
        menuCreateDTO = new MenuCreateDTO("Menú Ejecutivo");

        menuEntity = new Menu();
        menuEntity.setName("Menú Ejecutivo");
        menuEntity.setStatus(StatusEnum.ACTIVO);

        menuResultDTO = new MenuResultDTO(1L, "Menú Ejecutivo", StatusEnum.ACTIVO);
    }

    @Test
    void createMenu_WhenValidDto_ShouldCreateAndReturnMenu() {
        // Arrange
        when(menuProviderPort.findByName(anyString())).thenReturn(Optional.empty());
        when(menuProviderPort.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(menuMapper.entityToResult(any(Menu.class))).thenReturn(menuResultDTO);

        // Act
        MenuResultDTO result = menuService.createMenu(menuCreateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Menú Ejecutivo", result.name());
        assertEquals(StatusEnum.ACTIVO, result.status());

        verify(menuProviderPort, times(1)).findByName("Menú Ejecutivo");
        verify(menuProviderPort, times(1)).save(any(Menu.class));
        verify(menuEventPublisherPort, times(1)).publishMenuRefreshEvent();
    }

    @Test
    void createMenu_WhenNameIsEmpty_ShouldThrowInvalidFieldException() {
        // Arrange
        MenuCreateDTO invalidDto = new MenuCreateDTO("  ");

        // Act & Assert
        InvalidFieldException exception = assertThrows(InvalidFieldException.class, () -> menuService.createMenu(invalidDto));

        assertEquals("El nombre del menú es obligatorio.", exception.getMessage());
        verifyNoInteractions(menuProviderPort);
        verifyNoInteractions(menuEventPublisherPort);
    }

    @Test
    void createMenu_WhenNameAlreadyExists_ShouldThrowMenuNameAlreadyExistsException() {
        // Arrange
        when(menuProviderPort.findByName("Menú Ejecutivo")).thenReturn(Optional.of(menuEntity));

        // Act & Assert
        MenuNameAlreadyExistsException exception = assertThrows(MenuNameAlreadyExistsException.class, () -> menuService.createMenu(menuCreateDTO));

        assertEquals("El menú con el nombre 'Menú Ejecutivo' ya está registrado en el sistema.", exception.getMessage());
        verify(menuProviderPort, times(1)).findByName("Menú Ejecutivo");
        verify(menuProviderPort, never()).save(any(Menu.class));
        verifyNoInteractions(menuEventPublisherPort);
    }

    @Test
    void updateMenu_WhenValidDto_ShouldUpdateAndReturnMenu() {
        // Arrange
        MenuUpdateDTO updateDTO = new MenuUpdateDTO("Menú Gourmet");
        MenuResultDTO updatedResultDTO = new MenuResultDTO(1L, "Menú Gourmet", StatusEnum.ACTIVO);

        when(menuProviderPort.findById(1L)).thenReturn(Optional.of(menuEntity));
        when(menuProviderPort.findByName("Menú Gourmet")).thenReturn(Optional.empty());
        when(menuProviderPort.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(menuMapper.entityToResult(any(Menu.class))).thenReturn(updatedResultDTO);

        // Act
        MenuResultDTO result = menuService.updateMenu(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Menú Gourmet", result.name());
        assertEquals(StatusEnum.ACTIVO, result.status());

        verify(menuProviderPort, times(1)).findById(1L);
        verify(menuProviderPort, times(1)).findByName("Menú Gourmet");
        verify(menuProviderPort, times(1)).save(any(Menu.class));
        verify(menuEventPublisherPort, times(1)).publishMenuRefreshEvent();
    }

    @Test
    void updateMenu_WhenMenuNotFound_ShouldThrowNotFoundException() {
        // Arrange
        MenuUpdateDTO updateDTO = new MenuUpdateDTO("Menú Gourmet");
        when(menuProviderPort.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> menuService.updateMenu(1L, updateDTO));

        assertEquals("El menú con id '1' no fue encontrado.", exception.getMessage());
        verify(menuProviderPort, times(1)).findById(1L);
        verify(menuProviderPort, never()).findByName(anyString());
        verify(menuProviderPort, never()).save(any(Menu.class));
        verifyNoInteractions(menuEventPublisherPort);
    }

    @Test
    void updateMenu_WhenNameIsEmpty_ShouldThrowInvalidFieldException() {
        // Arrange
        MenuUpdateDTO invalidDTO = new MenuUpdateDTO("  ");
        when(menuProviderPort.findById(1L)).thenReturn(Optional.of(menuEntity));

        // Act & Assert
        InvalidFieldException exception = assertThrows(InvalidFieldException.class, () -> menuService.updateMenu(1L, invalidDTO));

        assertEquals("El nombre del menú es obligatorio.", exception.getMessage());
        verify(menuProviderPort, times(1)).findById(1L);
        verify(menuProviderPort, never()).findByName(anyString());
        verify(menuProviderPort, never()).save(any(Menu.class));
        verifyNoInteractions(menuEventPublisherPort);
    }

    @Test
    void updateMenu_WhenNameAlreadyExists_ShouldThrowMenuNameAlreadyExistsException() {
        // Arrange
        MenuUpdateDTO updateDTO = new MenuUpdateDTO("Menú Gourmet");
        Menu conflictingMenu = new Menu();
        conflictingMenu.setName("Menú Gourmet");

        when(menuProviderPort.findById(1L)).thenReturn(Optional.of(menuEntity));
        when(menuProviderPort.findByName("Menú Gourmet")).thenReturn(Optional.of(conflictingMenu));

        // Act & Assert
        MenuNameAlreadyExistsException exception = assertThrows(MenuNameAlreadyExistsException.class, () -> menuService.updateMenu(1L, updateDTO));

        assertEquals("El menú con el nombre 'Menú Gourmet' ya está registrado en el sistema.", exception.getMessage());
        verify(menuProviderPort, times(1)).findById(1L);
        verify(menuProviderPort, times(1)).findByName("Menú Gourmet");
        verify(menuProviderPort, never()).save(any(Menu.class));
        verifyNoInteractions(menuEventPublisherPort);
    }

    @Test
    void deactivateMenu_WhenValid_ShouldDeactivateMenuAndPauseActiveDishes() {
        // Arrange
        menuEntity.setStatus(StatusEnum.ACTIVO);
        Dish activeDish = new Dish();
        activeDish.setName("Plato 1");
        activeDish.setStatus(StatusEnum.ACTIVO);

        when(menuProviderPort.findById(1L)).thenReturn(Optional.of(menuEntity));
        when(dishProviderPort.findByMenuIdAndStatus(1L, StatusEnum.ACTIVO)).thenReturn(List.of(activeDish));

        // Act
        menuService.deactivateMenu(1L);

        // Assert
        assertEquals(StatusEnum.INACTIVO, menuEntity.getStatus());
        assertEquals(StatusEnum.PAUSADO, activeDish.getStatus());

        verify(menuProviderPort, times(1)).findById(1L);
        verify(menuProviderPort, times(1)).save(menuEntity);
        verify(dishProviderPort, times(1)).findByMenuIdAndStatus(1L, StatusEnum.ACTIVO);
        verify(dishProviderPort, times(1)).saveAll(anyList());
        verify(dishEventPublisherPort, times(1)).publishDishRefreshEvent();
        verify(menuEventPublisherPort, times(1)).publishMenuRefreshEvent();
    }

    @Test
    void deactivateMenu_WhenAlreadyInactive_ShouldThrowMenuAlreadyInactiveException() {
        // Arrange
        menuEntity.setStatus(StatusEnum.INACTIVO);
        when(menuProviderPort.findById(1L)).thenReturn(Optional.of(menuEntity));

        // Act & Assert
        MenuAlreadyInactiveException exception = assertThrows(MenuAlreadyInactiveException.class, () -> menuService.deactivateMenu(1L));

        assertEquals("El menú ya se encuentra inactivo.", exception.getMessage());
        verify(menuProviderPort, times(1)).findById(1L);
        verify(menuProviderPort, never()).save(any(Menu.class));
        verifyNoInteractions(dishProviderPort);
        verifyNoInteractions(dishEventPublisherPort);
    }

    @Test
    void reactivateMenu_WhenValid_ShouldReactivateMenuAndActivatePausedDishes() {
        // Arrange
        menuEntity.setStatus(StatusEnum.INACTIVO);
        Dish pausedDish = new Dish();
        pausedDish.setName("Plato 1");
        pausedDish.setStatus(StatusEnum.PAUSADO);

        when(menuProviderPort.findById(1L)).thenReturn(Optional.of(menuEntity));
        when(dishProviderPort.findByMenuIdAndStatus(1L, StatusEnum.PAUSADO)).thenReturn(List.of(pausedDish));

        // Act
        menuService.reactivateMenu(1L);

        // Assert
        assertEquals(StatusEnum.ACTIVO, menuEntity.getStatus());
        assertEquals(StatusEnum.ACTIVO, pausedDish.getStatus());

        verify(menuProviderPort, times(1)).findById(1L);
        verify(menuProviderPort, times(1)).save(menuEntity);
        verify(dishProviderPort, times(1)).findByMenuIdAndStatus(1L, StatusEnum.PAUSADO);
        verify(dishProviderPort, times(1)).saveAll(anyList());
        verify(dishEventPublisherPort, times(1)).publishDishRefreshEvent();
        verify(menuEventPublisherPort, times(1)).publishMenuRefreshEvent();
    }

    @Test
    void reactivateMenu_WhenAlreadyActive_ShouldThrowMenuAlreadyActiveException() {
        // Arrange
        menuEntity.setStatus(StatusEnum.ACTIVO);
        when(menuProviderPort.findById(1L)).thenReturn(Optional.of(menuEntity));

        // Act & Assert
        MenuAlreadyActiveException exception = assertThrows(MenuAlreadyActiveException.class, () -> menuService.reactivateMenu(1L));

        assertEquals("El menú ya se encuentra activo.", exception.getMessage());
        verify(menuProviderPort, times(1)).findById(1L);
        verify(menuProviderPort, never()).save(any(Menu.class));
        verifyNoInteractions(dishProviderPort);
        verifyNoInteractions(dishEventPublisherPort);
    }

    @Test
    void getMenus_ShouldReturnPaginatedMenus() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Menu> page = new PageImpl<>(List.of(menuEntity));

        when(menuProviderPort.findAll(pageable)).thenReturn(page);
        when(menuMapper.entityToResult(menuEntity)).thenReturn(menuResultDTO);

        // Act
        PageResultDTO<MenuResultDTO> result = menuService.getMenus(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals("Menú Ejecutivo", result.content().getFirst().name());
        assertEquals(0, result.page());
        assertEquals(1, result.totalPages());

        verify(menuProviderPort, times(1)).findAll(pageable);
        verify(menuMapper, times(1)).entityToResult(menuEntity);
    }
}
