package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository;

import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Table.
 */
@Repository
public interface TableDAO extends JpaRepository<Table, Long> {

    /**
     * Busca una mesa por su número.
     *
     * @param number Número de la mesa.
     * @return Opcional con la mesa encontrada.
     */
    Optional<Table> findByNumber(Integer number);
}
