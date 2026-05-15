package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthRegisterResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true)
    User modelToEntity(com.grupo_morado.sistema_facturacion_inventario.domain.models.User user);

    @Mapping(target = "role", source = "role.name")
    AuthRegisterResultDTO entityToResult(User user);

}
