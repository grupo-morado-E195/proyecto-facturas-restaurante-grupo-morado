package com.grupo_morado.sistema_facturacion_inventario.infrastructure.security;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.PasswordEncryptPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PasswordEncryptAdapter implements PasswordEncryptPort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
