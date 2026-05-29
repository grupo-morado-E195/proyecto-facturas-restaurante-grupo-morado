package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Dish.
 */
@Repository
public interface DishDAO extends JpaRepository<Dish, Long> {

    /**
     * Busca platos asociados a un menú por su identificador y su estado.
     *
     * @param menuId Identificador del menú.
     * @param status Estado del plato.
     * @return Lista de platos que cumplen los criterios.
     */
    List<Dish> findByMenuIdAndStatus(Long menuId, StatusEnum status);

    /**
     * Busca platos paginados filtrando opcionalmente por nombre (LIKE caso-insensible) y estado.
     * Utiliza FETCH JOIN para cargar el menú asociado en una única consulta y evitar el problema de N+1 consultas.
     *
     * @param name     Nombre del plato a buscar (parcial).
     * @param status   Estado del plato.
     * @param pageable Configuración de paginación.
     * @return Página de platos.
     */
    @Query(
            value = "SELECT d FROM Dish d LEFT JOIN FETCH d.menu " +
                    "WHERE (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                    "AND (:status IS NULL OR d.status = :status)",
            countQuery = "SELECT COUNT(d) FROM Dish d " +
                         "WHERE (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                         "AND (:status IS NULL OR d.status = :status)"
    )
    Page<Dish> findByNameAndStatus(
            @Param("name") String name,
            @Param("status") StatusEnum status,
            Pageable pageable
    );

    /**
     * Retorna todos los platos en estado ACTIVO con stock mayor a 0.
     * Usado para el dropdown de selección de platos al registrar o modificar una orden (SFR-006).
     */
    @Query("SELECT d FROM Dish d LEFT JOIN FETCH d.menu WHERE d.status = 'ACTIVO' AND d.stock > 0 ORDER BY d.name ASC")
    List<Dish> findAllActiveWithStock();
}
