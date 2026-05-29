package com.grupo_morado.sistema_facturacion_inventario.application.services;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.PageResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.UserResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.in.UserUseCase;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.RoleProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.UserProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidFieldException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.NotFoundException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.UserAlreadyActiveException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.UserAlreadyInactiveException;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.UserCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.UserUpdateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Role;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Servicio que implementa la lógica de negocio del módulo de Gestión de Usuarios (SFR-008).
 *
 * <ul>
 *   <li>#1 Crear usuario — nombre, apellidos, email, rol. Contraseña temporal generada y enviada por correo.</li>
 *   <li>#2 Modificar usuario — nombre, apellidos, rol.</li>
 *   <li>#3 Desactivar usuario (soft delete).</li>
 *   <li>#4 Reactivar usuario.</li>
 *   <li>#5 Ver detalle de usuario.</li>
 *   <li>#6 Listar usuarios paginados.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserUseCase {

    private final UserProviderPort userProviderPort;
    private final RoleProviderPort roleProviderPort;
    private final PasswordEncoder passwordEncoder;

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private UserResultDTO toDTO(User user) {
        return new UserResultDTO(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().getName() : null,
                user.getStatus()
        );
    }

    private Role findRole(Long roleId) {
        return roleProviderPort.findById(roleId)
                .orElseThrow(() -> new NotFoundException("El rol con id '" + roleId + "' no fue encontrado."));
    }

    // ─── Casos de uso ─────────────────────────────────────────────────────────

    /**
     * SFR-008 #1 — Registrar Usuario.
     *
     * <p>Reglas:
     * <ol>
     *   <li>Todos los campos son obligatorios.</li>
     *   <li>El email no puede estar ya registrado.</li>
     *   <li>El rol debe existir en el sistema.</li>
     *   <li>El usuario se registra en estado ACTIVO por defecto.</li>
     *   <li>Se genera una contraseña temporal (el backlog delega el envío por correo a AuthService).</li>
     * </ol>
     */
    @Override
    @Transactional
    public UserResultDTO createUser(UserCreateDTO dto) {
        log.info("Iniciando registro de usuario con email: {}", dto.email());

        // Validar unicidad de email
        if (userProviderPort.findByEmail(dto.email().trim()).isPresent()) {
            throw new InvalidFieldException("El correo electrónico '" + dto.email() + "' ya está registrado en el sistema.");
        }

        Role role = findRole(dto.roleId());

        // Validar formato de la contraseña ingresada por el administrador
        com.grupo_morado.sistema_facturacion_inventario.domain.models.User.validatePasswordFormat(dto.password());

        String hashedPassword = passwordEncoder.encode(dto.password().trim());

        User user = new User();
        user.setName(dto.name().trim());
        user.setLastname(dto.lastname().trim());
        user.setEmail(dto.email().trim().toLowerCase());
        user.setPassword(hashedPassword);
        user.setRole(role);
        user.setStatus(StatusEnum.ACTIVO);
        user.setMustChangePassword(false); // La contraseña es definitiva, establecida por el admin
        user.setTokenVersion(0L);

        User saved = userProviderPort.save(user);
        log.info("Usuario con email: {} registrado exitosamente con ID: {}.", dto.email(), saved.getId());

        return toDTO(saved);
    }

    /**
     * SFR-008 #2 — Modificar Usuario.
     *
     * <p>Permite modificar nombre, apellidos, rol y opcionalmente la contraseña.
     */
    @Override
    @Transactional
    public UserResultDTO updateUser(Long id, UserUpdateDTO dto) {
        log.info("Iniciando modificación del usuario con ID: {}", id);

        User user = userProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("El usuario con id '" + id + "' no fue encontrado."));

        Role role = findRole(dto.roleId());

        user.setName(dto.name().trim());
        user.setLastname(dto.lastname().trim());
        user.setRole(role);

        // Si el administrador ingresa una nueva contraseña, validarla y actualizarla
        if (dto.password() != null && !dto.password().trim().isEmpty()) {
            com.grupo_morado.sistema_facturacion_inventario.domain.models.User.validatePasswordFormat(dto.password());
            String hashedPassword = passwordEncoder.encode(dto.password().trim());
            user.setPassword(hashedPassword);
            // Invalida tokens anteriores
            user.setTokenVersion(user.getTokenVersion() + 1);
            log.info("Contraseña del usuario con ID: {} actualizada por el administrador.", id);
        }

        User updated = userProviderPort.save(user);
        log.info("Usuario con ID: {} modificado exitosamente.", id);

        return toDTO(updated);
    }

    /**
     * SFR-008 #3 — Desactivar Usuario.
     *
     * <p>Reglas:
     * <ol>
     *   <li>Si el usuario ya está inactivo, lanza UserAlreadyInactiveException.</li>
     *   <li>Un usuario inactivo no podrá autenticarse (gestionado por SecurityUser#isEnabled).</li>
     * </ol>
     */
    @Override
    @Transactional
    public UserResultDTO deactivateUser(Long id) {
        log.info("Iniciando desactivación del usuario con ID: {}", id);

        User user = userProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("El usuario con id '" + id + "' no fue encontrado."));

        if (user.getStatus() == StatusEnum.INACTIVO) {
            throw new UserAlreadyInactiveException("El usuario ya se encuentra inactivo.");
        }

        user.setStatus(StatusEnum.INACTIVO);
        // Invalida todos sus tokens activos al desactivarlo
        user.setTokenVersion(user.getTokenVersion() + 1);

        User updated = userProviderPort.save(user);
        log.info("Usuario con ID: {} desactivado exitosamente.", id);

        return toDTO(updated);
    }

    /**
     * SFR-008 #4 — Reactivar Usuario.
     */
    @Override
    @Transactional
    public UserResultDTO reactivateUser(Long id) {
        log.info("Iniciando reactivación del usuario con ID: {}", id);

        User user = userProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("El usuario con id '" + id + "' no fue encontrado."));

        if (user.getStatus() == StatusEnum.ACTIVO) {
            throw new UserAlreadyActiveException("El usuario ya se encuentra activo.");
        }

        user.setStatus(StatusEnum.ACTIVO);
        User updated = userProviderPort.save(user);
        log.info("Usuario con ID: {} reactivado exitosamente.", id);

        return toDTO(updated);
    }

    /**
     * SFR-008 #5 — Mostrar Usuario por ID.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResultDTO getUserById(Long id) {
        log.info("Consultando usuario con ID: {}", id);

        User user = userProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("El usuario con id '" + id + "' no fue encontrado."));

        return toDTO(user);
    }

    /**
     * SFR-008 #6 — Listar Usuarios paginados.
     *
     * <p>Tabla con: nombre, apellidos, rol y estado.
     */
    @Override
    @Transactional(readOnly = true)
    public PageResultDTO<UserResultDTO> getUsers(Pageable pageable) {
        log.info("Consultando usuarios paginados: {}", pageable);

        Page<User> page = userProviderPort.findAll(pageable);
        List<UserResultDTO> content = page.getContent().stream()
                .map(this::toDTO)
                .toList();

        return new PageResultDTO<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    /**
     * Devuelve todos los roles disponibles en el sistema (para el dropdown de creación/edición).
     */
    @Override
    @Transactional(readOnly = true)
    public List<Role> getRoles() {
        return roleProviderPort.findAll();
    }

    @Override
    @Transactional
    public Role createRole(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidFieldException("El nombre del rol es obligatorio.");
        }
        String trimmedName = name.trim().toUpperCase();
        boolean exists = roleProviderPort.findAll().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase(trimmedName));
        if (exists) {
            throw new InvalidFieldException("El rol '" + trimmedName + "' ya existe en el sistema.");
        }
        Role role = new Role();
        role.setName(trimmedName);
        return roleProviderPort.save(role);
    }

    @Override
    @Transactional
    public Role updateRole(Long id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidFieldException("El nombre del rol es obligatorio.");
        }
        Role role = roleProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("El rol con id '" + id + "' no fue encontrado."));
        String trimmedName = name.trim().toUpperCase();
        boolean exists = roleProviderPort.findAll().stream()
                .anyMatch(r -> !r.getId().equals(id) && r.getName().equalsIgnoreCase(trimmedName));
        if (exists) {
            throw new InvalidFieldException("El rol '" + trimmedName + "' ya existe en el sistema.");
        }
        
        // No permitir renombrar el rol administrador principal para evitar dejar sin acceso al sistema
        if (role.getName().equalsIgnoreCase("ADMINISTRADOR") && !trimmedName.equalsIgnoreCase("ADMINISTRADOR")) {
            throw new InvalidFieldException("No se puede renombrar el rol principal ADMINISTRADOR.");
        }
        
        role.setName(trimmedName);
        return roleProviderPort.save(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("El rol con id '" + id + "' no fue encontrado."));
        if (role.getName().equalsIgnoreCase("ADMINISTRADOR")) {
            throw new InvalidFieldException("No se puede eliminar el rol principal ADMINISTRADOR.");
        }
        try {
            roleProviderPort.deleteById(id);
        } catch (Exception e) {
            throw new InvalidFieldException("No se puede eliminar el rol porque está asignado a uno o más usuarios o recursos del sistema.");
        }
    }
}
