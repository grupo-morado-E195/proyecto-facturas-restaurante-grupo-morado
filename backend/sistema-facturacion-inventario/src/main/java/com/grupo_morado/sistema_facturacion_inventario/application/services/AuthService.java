package com.grupo_morado.sistema_facturacion_inventario.application.services;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthLoginResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthRegisterResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.in.AuthUseCase;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.AuthProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.PasswordEncryptPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.RoleProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.NotFoundException;
import com.grupo_morado.sistema_facturacion_inventario.domain.models.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthRegisterDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.mappers.AuthenticationMapper;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Role;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final AuthProviderPort authProviderPort;
    private final PasswordEncryptPort passwordEncryptPort;
    private final RoleProviderPort roleProviderPort;
    private final AuthenticationMapper authenticationMapper;
    private final UserMapper userMapper;

    @Override
    public AuthLoginResultDTO login(User user) {
        return authProviderPort.authenticate(user.getEmail(),user.getPassword());
    }

    @Override
    public AuthRegisterResultDTO register(AuthRegisterDTO user) {
        Optional<Role> role = roleProviderPort.findById(user.roleID());
        if (role.isEmpty()){
            throw new NotFoundException("el rol con id '" + user.roleID() + "' no fue encontrado.");
        }
        String passwordEncoded = passwordEncryptPort.encryptPassword(user.password());
        User userModel = authenticationMapper.dtoToModelRegister(user);
        com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User userEntity = authProviderPort.register(userModel, role.get(), passwordEncoded);
        return userMapper.entityToResult(userEntity);
    }

}
