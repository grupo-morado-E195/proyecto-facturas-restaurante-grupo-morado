package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Puerto de salida para persistencia de menús.
 * Abstrae el acceso a datos de la capa de aplicación.
 */
public interface MenuProviderPort {

    /**
     * Busca un menú por su identificador.
     *
     * @param id Identificador del menú.
     * @return Menú encontrado, o {@link Optional#empty()} si no existe.
     */
    Optional<Menu> findById(Long id);

    /**
     * Busca un menú por su nombre.
     * Se usa para validar unicidad.
     *
     * @param name Nombre del menú a buscar.
     * @return Menú encontrado, o {@link Optional#empty()} si no existe.
     */
    Optional<Menu> findByName(String name);

    /**
     * Persiste un menú (creación o actualización).
     *
     * @param menu Entidad de menú a guardar.
     * @return Menú persistido con ID generado si es nuevo.
     */
    Menu save(Menu menu);

    /**
     * Retorna una página de menús.
     *
     * @param pageable Configuración de paginación y ordenación.
     * @return Página de menús.
     */
    Page<Menu> findAll(Pageable pageable);
}
