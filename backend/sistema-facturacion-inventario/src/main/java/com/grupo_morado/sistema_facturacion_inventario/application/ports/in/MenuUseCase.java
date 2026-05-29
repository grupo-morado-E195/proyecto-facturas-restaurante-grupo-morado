package com.grupo_morado.sistema_facturacion_inventario.application.ports.in;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.MenuResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.MenuCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.MenuUpdateDTO;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de entrada para el módulo de Gestión de Menús.
 * Define las operaciones disponibles sobre los menús del restaurante.
 */
public interface MenuUseCase {

    /**
     * Crea un nuevo menú con estado ACTIVO por defecto.
     *
     * @param dto DTO con los datos del menú a crear.
     * @return DTO con los datos del menú creado.
     */
    MenuResultDTO createMenu(MenuCreateDTO dto);

    /**
     * Modifica el nombre de un menú existente.
     *
     * @param id  Identificador del menú a modificar.
     * @param dto DTO con el nuevo nombre del menú.
     * @return DTO con los datos actualizados.
     */
    MenuResultDTO updateMenu(Long id, MenuUpdateDTO dto);

    /**
     * Desactiva un menú cambiando su estado a INACTIVO.
     * También pausa automáticamente los platos activos asociados.
     *
     * @param id Identificador del menú a desactivar.
     */
    void deactivateMenu(Long id);

    /**
     * Reactiva un menú cambiando su estado a ACTIVO.
     * También reactiva automáticamente los platos asociados que fueron pausados.
     *
     * @param id Identificador del menú a reactivar.
     */
    void reactivateMenu(Long id);

    /**
     * Retorna una página de menús con sus datos.
     *
     * @param pageable Configuración de paginación y ordenación.
     * @return Página de menús como DTO paginado.
     */
    PageResultDTO<MenuResultDTO> getMenus(Pageable pageable);
}
