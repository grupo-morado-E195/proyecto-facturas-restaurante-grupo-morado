package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.MenuResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Menu;
import org.mapstruct.Mapper;

/**
 * Mapper MapStruct para la entidad Menu.
 */
@Mapper(componentModel = "spring")
public interface MenuMapper {

    /**
     * Convierte una entidad Menu a su DTO de resultado MenuResultDTO.
     *
     * @param menu Entidad menú a convertir.
     * @return DTO de resultado.
     */
    MenuResultDTO entityToResult(Menu menu);
}
