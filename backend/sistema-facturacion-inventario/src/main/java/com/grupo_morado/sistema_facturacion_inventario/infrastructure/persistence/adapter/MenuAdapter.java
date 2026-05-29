package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.adapter;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.MenuProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Menu;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository.MenuDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador de persistencia para el módulo de menús.
 * Implementa el puerto de salida MenuProviderPort delegando en MenuDAO.
 */
@Component
@RequiredArgsConstructor
public class MenuAdapter implements MenuProviderPort {

    private final MenuDAO menuDAO;

    @Override
    public Optional<Menu> findById(Long id) {
        return menuDAO.findById(id);
    }

    @Override
    public Optional<Menu> findByName(String name) {
        return menuDAO.findByName(name);
    }

    @Override
    public Menu save(Menu menu) {
        return menuDAO.save(menu);
    }

    @Override
    public Page<Menu> findAll(Pageable pageable) {
        return menuDAO.findAll(pageable);
    }
}
