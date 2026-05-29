package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.MenuResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.services.MenuService;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.MenuCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.MenuUpdateDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuController menuController;

    @Test
    void createMenu_WhenValidRequest_ShouldReturn201Created() {
        // Arrange
        MenuCreateDTO dto = new MenuCreateDTO("Menú Infantil");
        MenuResultDTO result = new MenuResultDTO(1L, "Menú Infantil", StatusEnum.ACTIVO);

        when(menuService.createMenu(any(MenuCreateDTO.class))).thenReturn(result);

        // Act
        ResponseEntity<MenuResultDTO> response = menuController.createMenu(dto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Menú Infantil", response.getBody().name());
        assertEquals(StatusEnum.ACTIVO, response.getBody().status());

        verify(menuService, times(1)).createMenu(dto);
    }

    @Test
    void updateMenu_WhenValidRequest_ShouldReturn200Ok() {
        // Arrange
        MenuUpdateDTO dto = new MenuUpdateDTO("Menú Infantil Modificado");
        MenuResultDTO result = new MenuResultDTO(1L, "Menú Infantil Modificado", StatusEnum.ACTIVO);

        when(menuService.updateMenu(eq(1L), any(MenuUpdateDTO.class))).thenReturn(result);

        // Act
        ResponseEntity<MenuResultDTO> response = menuController.updateMenu(1L, dto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Menú Infantil Modificado", response.getBody().name());
        assertEquals(StatusEnum.ACTIVO, response.getBody().status());

        verify(menuService, times(1)).updateMenu(eq(1L), any(MenuUpdateDTO.class));
    }

    @Test
    void deactivateMenu_ShouldReturn200Ok() {
        // Act
        ResponseEntity<Map<String, String>> response = menuController.deactivateMenu(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Menú desactivado correctamente.", response.getBody().get("message"));

        verify(menuService, times(1)).deactivateMenu(1L);
    }

    @Test
    void reactivateMenu_ShouldReturn200Ok() {
        // Act
        ResponseEntity<Map<String, String>> response = menuController.reactivateMenu(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Menú reactivado correctamente.", response.getBody().get("message"));

        verify(menuService, times(1)).reactivateMenu(1L);
    }

    @Test
    void getMenus_ShouldReturn200Ok() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        MenuResultDTO resultDTO = new MenuResultDTO(1L, "Menú Infantil", StatusEnum.ACTIVO);
        PageResultDTO<MenuResultDTO> pageResult = new PageResultDTO<>(
                List.of(resultDTO), 0, 10, 1L, 1
        );

        when(menuService.getMenus(pageable)).thenReturn(pageResult);

        // Act
        ResponseEntity<PageResultDTO<MenuResultDTO>> response = menuController.getMenus(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().content().size());
        assertEquals("Menú Infantil", response.getBody().content().getFirst().name());

        verify(menuService, times(1)).getMenus(pageable);
    }
}
