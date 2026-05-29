package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.DishResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para la entidad Dish.
 */
@Mapper(componentModel = "spring")
public interface DishMapper {

    /**
     * Convierte una entidad Dish a su DTO de resultado DishResultDTO.
     *
     * @param dish Entidad plato a convertir.
     * @return DTO de resultado.
     */
    @Mapping(target = "menuId", source = "menu.id")
    DishResultDTO entityToResult(Dish dish);
}
