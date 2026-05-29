package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository;

import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad Menu.
 */
@Repository
public interface MenuDAO extends JpaRepository<Menu, Long> {

    /**
     * Busca un menú por su nombre.
     *
     * @param name Nombre del menú.
     * @return Opcional con el menú encontrado.
     */
    Optional<Menu> findByName(String name);
}
