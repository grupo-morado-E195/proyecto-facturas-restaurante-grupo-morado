package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Dish;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de platos.
 * Abstrae el acceso a datos para la entidad Dish.
 */
public interface DishProviderPort {

    /**
     * Busca un plato por su identificador.
     *
     * @param id Identificador del plato.
     * @return Plato encontrado, o {@link Optional#empty()} si no existe.
     */
    Optional<Dish> findById(Long id);

    /**
     * Persiste un plato (creación o actualización).
     *
     * @param dish Entidad de plato a guardar.
     * @return Plato persistido.
     */
    Dish save(Dish dish);

    /**
     * Busca los platos asociados a un menú por su identificador y estado.
     *
     * @param menuId Identificador del menú.
     * @param status Estado de los platos a buscar.
     * @return Lista de platos encontrados.
     */
    List<Dish> findByMenuIdAndStatus(Long menuId, StatusEnum status);

    /**
     * Guarda una lista de platos en el sistema.
     *
     * @param dishes Lista de platos a guardar.
     * @return Lista de platos guardados.
     */
    List<Dish> saveAll(List<Dish> dishes);

    /**
     * Busca platos paginados filtrando opcionalmente por nombre y estado.
     *
     * @param name     Nombre del plato para búsqueda parcial (opcional).
     * @param status   Estado del plato (opcional).
     * @param pageable Configuración de paginación.
     * @return Página de platos.
     */
    org.springframework.data.domain.Page<Dish> findByNameAndStatus(String name, StatusEnum status, org.springframework.data.domain.Pageable pageable);

    /**
     * Retorna todos los platos que estén en estado ACTIVO y con stock mayor a 0.
     * Utilizado para construir la lista desplegable de platos al crear o modificar una orden (SFR-006).
     *
     * @return Lista de platos disponibles para selección en órdenes.
     */
    List<Dish> findAllActiveWithStock();
}
