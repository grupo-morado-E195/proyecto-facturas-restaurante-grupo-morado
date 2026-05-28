package com.grupo_morado.sistema_facturacion_inventario.application.ports.in;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthLoginResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthRegisterResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.domain.models.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthRegisterDTO;

public interface AuthUseCase {

    AuthLoginResultDTO login(User user);
    AuthRegisterResultDTO register(AuthRegisterDTO user);

}
