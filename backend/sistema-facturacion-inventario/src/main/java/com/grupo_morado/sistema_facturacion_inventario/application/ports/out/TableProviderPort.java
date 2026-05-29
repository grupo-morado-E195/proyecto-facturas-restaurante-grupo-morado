package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Table;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Puerto de salida para persistencia de mesas.
 * Abstrae el acceso a datos de la capa de aplicación.
 */
public interface TableProviderPort {

    /**
     * Busca una mesa por su identificador.
     *
     * @param id Identificador de la mesa.
     * @return Mesa encontrada, o {@link Optional#empty()} si no existe.
     */
    Optional<Table> findById(Long id);

    /**
     * Busca una mesa por su número.
     * Se usa para validar unicidad al crear o modificar.
     *
     * @param number Número de mesa a buscar.
     * @return Mesa encontrada, o {@link Optional#empty()} si no existe.
     */
    Optional<Table> findByNumber(Integer number);

    /**
     * Persiste una mesa (creación o actualización).
     *
     * @param table Entidad de mesa a guardar.
     * @return Mesa persistida con ID generado si es nueva.
     */
    Table save(Table table);

    /**
     * Retorna una página de mesas.
     *
     * @param pageable Configuración de paginación y ordenación.
     * @return Página de mesas.
     */
    Page<Table> findAll(Pageable pageable);
}
