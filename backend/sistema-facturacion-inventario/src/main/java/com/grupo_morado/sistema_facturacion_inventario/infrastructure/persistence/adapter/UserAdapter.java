package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.adapter;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.UserProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository.UserDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador de persistencia que implementa {@link UserProviderPort}.
 * Delega las operaciones de búsqueda y guardado al repositorio JPA {@link UserDAO}.
 */
@Component
@RequiredArgsConstructor
public class UserAdapter implements UserProviderPort {

    private final UserDAO userDAO;

    @Override
    public Optional<User> findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userDAO.save(user);
    }
}
