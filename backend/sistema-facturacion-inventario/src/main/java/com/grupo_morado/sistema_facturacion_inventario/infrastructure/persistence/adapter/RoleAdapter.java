package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.adapter;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.RoleProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Role;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository.RoleDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoleAdapter implements RoleProviderPort {

    private final RoleDAO roleDAO;

    @Override
    public Optional<Role> findById(Long id) {
        return roleDAO.findById(id);
    }

    @Override
    public List<Role> findAll() {
        return roleDAO.findAll();
    }

    @Override
    public Role save(Role role) {
        return roleDAO.save(role);
    }

    @Override
    public void deleteById(Long id) {
        roleDAO.deleteById(id);
    }
}
