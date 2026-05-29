package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.adapter;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.DishProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Dish;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository.DishDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia para el módulo de platos.
 * Implementa el puerto de salida DishProviderPort delegando en DishDAO.
 */
@Component
@RequiredArgsConstructor
public class DishAdapter implements DishProviderPort {

    private final DishDAO dishDAO;

    @Override
    public Optional<Dish> findById(Long id) {
        return dishDAO.findById(id);
    }

    @Override
    public Dish save(Dish dish) {
        return dishDAO.save(dish);
    }

    @Override
    public List<Dish> findByMenuIdAndStatus(Long menuId, StatusEnum status) {
        return dishDAO.findByMenuIdAndStatus(menuId, status);
    }

    @Override
    public List<Dish> saveAll(List<Dish> dishes) {
        return dishDAO.saveAll(dishes);
    }

    @Override
    public org.springframework.data.domain.Page<Dish> findByNameAndStatus(String name, StatusEnum status, org.springframework.data.domain.Pageable pageable) {
        return dishDAO.findByNameAndStatus(name, status, pageable);
    }

    @Override
    public List<Dish> findAllActiveWithStock() {
        return dishDAO.findAllActiveWithStock();
    }
}
