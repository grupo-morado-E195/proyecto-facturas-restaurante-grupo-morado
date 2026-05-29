package com.grupo_morado.sistema_facturacion_inventario.application.ports.in;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.TableResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.TableCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.TableUpdateDTO;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de entrada para el módulo de Gestión de Mesas.
 * Define las operaciones disponibles sobre las mesas del restaurante.
 */
public interface TableUseCase {

    /**
     * Crea una nueva mesa con estado ACTIVA y disponibilidad DISPONIBLE.
     *
     * @param dto DTO con el número de mesa a crear.
     * @return DTO con los datos de la mesa creada.
     */
    TableResultDTO createTable(TableCreateDTO dto);

    /**
     * Modifica el número de una mesa existente.
     *
     * @param id  Identificador de la mesa a modificar.
     * @param dto DTO con el nuevo número de mesa.
     * @return DTO con los datos actualizados.
     */
    TableResultDTO updateTable(Long id, TableUpdateDTO dto);

    /**
     * Desactiva una mesa cambiando su estado a INACTIVA.
     * No se puede desactivar una mesa OCUPADA.
     *
     * @param id Identificador de la mesa a desactivar.
     */
    void deactivateTable(Long id);

    /**
     * Reactiva una mesa cambiando su estado a ACTIVA.
     *
     * @param id Identificador de la mesa a reactivar.
     */
    void reactivateTable(Long id);

    /**
     * Retorna una página de mesas con sus datos.
     *
     * @param pageable Configuración de paginación y ordenación.
     * @return Página de mesas como DTO paginado.
     */
    PageResultDTO<TableResultDTO> getTables(Pageable pageable);
}
