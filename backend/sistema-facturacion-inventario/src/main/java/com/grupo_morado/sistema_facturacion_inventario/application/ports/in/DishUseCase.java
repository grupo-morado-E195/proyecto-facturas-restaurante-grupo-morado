package com.grupo_morado.sistema_facturacion_inventario.application.ports.in;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.DishResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.DishCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.DishUpdateDTO;

/**
 * Puerto de entrada para el módulo de Gestión de Platos.
 * Define las operaciones disponibles para registrar y modificar platos.
 */
public interface DishUseCase {

    /**
     * Registra un nuevo plato con estado ACTIVO por defecto.
     *
     * @param dto DTO con los datos del plato a registrar.
     * @return DTO con los datos del plato registrado.
     */
    DishResultDTO createDish(DishCreateDTO dto);

    /**
     * Modifica los datos de un plato existente.
     *
     * @param id  Identificador del plato a modificar.
     * @param dto DTO con los nuevos datos del plato.
     * @return DTO con los datos del plato actualizados.
     */
    DishResultDTO updateDish(Long id, DishUpdateDTO dto);

    /**
     * Desactiva un plato (soft delete) cambiando su estado a INACTIVO.
     *
     * @param id Identificador del plato a desactivar.
     */
    void deactivateDish(Long id);

    /**
     * Reactiva un plato cambiando su estado a ACTIVO.
     * Revalida que el menú asociado no esté inactivo.
     *
     * @param id Identificador del plato a reactivar.
     */
    void reactivateDish(Long id);

    /**
     * Obtiene el detalle de un plato por su identificador.
     *
     * @param id Identificador del plato.
     * @return DTO con los datos del plato.
     */
    DishResultDTO getDishById(Long id);

    /**
     * Retorna una página de platos filtrada opcionalmente por nombre y estado.
     *
     * @param name     Nombre del plato para búsqueda parcial (opcional).
     * @param status   Estado del plato (opcional).
     * @param pageable Configuración de paginación.
     * @return Página de platos como DTO paginado.
     */
    com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO<DishResultDTO> getDishes(
            String name,
            com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum status,
            org.springframework.data.domain.Pageable pageable
    );

    /**
     * Retorna todos los platos disponibles para selección en órdenes:
     * estado ACTIVO y stock mayor a 0.
     * Usado para poblar el dropdown de platos al crear o modificar una orden (SFR-006).
     *
     * @return Lista de platos disponibles.
     */
    java.util.List<DishResultDTO> getAvailableDishes();
}
