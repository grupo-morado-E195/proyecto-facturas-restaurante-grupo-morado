package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.TableResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Table;
import org.mapstruct.Mapper;

/**
 * Mapper MapStruct para la entidad Table.
 */
@Mapper(componentModel = "spring")
public interface TableMapper {

    /**
     * Convierte una entidad Table a su DTO de resultado TableResultDTO.
     *
     * @param table Entidad mesa a convertir.
     * @return DTO de resultado.
     */
    TableResultDTO entityToResult(Table table);
}
