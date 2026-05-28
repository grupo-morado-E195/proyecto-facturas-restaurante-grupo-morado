package com.grupo_morado.sistema_facturacion_inventario.infrastructure.security;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthLoginResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthRegisterResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.AuthProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Role;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers.UserMapper;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository.UserDAO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthAdapter implements AuthProviderPort {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final UserDAO userDAO;
    private final JwtService jwtService;

    @Override
    public AuthLoginResultDTO authenticate(String email, String password) {
        try{
            UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authUser = authenticationManager.authenticate(userToken);
            SecurityUser userDetails = (SecurityUser) authUser.getPrincipal();
            User user = userDetails.getUser();
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null);

            String token = jwtService.generateToken(user.getId(), userDetails.getUsername(), role);
            return new AuthLoginResultDTO(token);
        }catch (BadCredentialsException e){
            throw new BadCredentialsException("Las credenciales son incorrectas");
        }
    }

    @Override
    public User register(com.grupo_morado.sistema_facturacion_inventario.domain.models.User user, Role role, String passwordEncoded) {
        User userEntity = userMapper.modelToEntity(user);
        userEntity.setPassword(passwordEncoded);
        userEntity.setStatus(StatusEnum.ACTIVO);
        userEntity.setRole(role);
        return userDAO.save(userEntity);
    }
}
