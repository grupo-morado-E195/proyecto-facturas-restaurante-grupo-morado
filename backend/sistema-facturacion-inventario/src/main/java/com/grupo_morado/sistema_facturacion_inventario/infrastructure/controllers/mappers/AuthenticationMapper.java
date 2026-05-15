package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.mappers;

import com.grupo_morado.sistema_facturacion_inventario.domain.models.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthRegisterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthenticationMapper {
    User dtoToModelLogin(AuthDTO user);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    User dtoToModelRegister(AuthRegisterDTO user);
}
