package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.adapter;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.TableProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Table;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository.TableDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador de persistencia para el módulo de mesas.
 * Implementa el puerto de salida TableProviderPort delegando en TableDAO.
 */
@Component
@RequiredArgsConstructor
public class TableAdapter implements TableProviderPort {

    private final TableDAO tableDAO;

    @Override
    public Optional<Table> findById(Long id) {
        return tableDAO.findById(id);
    }

    @Override
    public Optional<Table> findByNumber(Integer number) {
        return tableDAO.findByNumber(number);
    }

    @Override
    public Table save(Table table) {
        return tableDAO.save(table);
    }

    @Override
    public Page<Table> findAll(Pageable pageable) {
        return tableDAO.findAll(pageable);
    }
}
