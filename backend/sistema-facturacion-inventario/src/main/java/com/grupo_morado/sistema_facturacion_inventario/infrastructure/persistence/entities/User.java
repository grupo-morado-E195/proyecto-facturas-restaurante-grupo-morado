package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
public class User extends BaseEntity {

    @Column(name = "nombre")
    private String name;

    @Column(name = "apellidos")
    private String lastname;

    @Column(name = "email")
    private String email;

    @Column(name = "contrasena")
    private String password;

    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Role role;

    // ─── Campos para recuperación de contraseña temporal ─────────────────────

    /**
     * Contraseña temporal hasheada con BCrypt.
     * Null cuando no hay proceso de recuperación activo.
     */
    @Column(name = "contrasena_temporal")
    private String temporaryPassword;

    /**
     * Fecha y hora de expiración de la contraseña temporal.
     * Null cuando no hay proceso de recuperación activo.
     */
    @Column(name = "expiracion_contrasena_temporal")
    private LocalDateTime temporaryPasswordExpiration;

    /**
     * Indica que el usuario debe cambiar su contraseña obligatoriamente.
     * Se activa al generar contraseña temporal y se limpia al actualizar la contraseña definitiva.
     */
    @Column(name = "debe_cambiar_contrasena", nullable = false)
    private boolean mustChangePassword = false;

    // ─── Campo para invalidación de JWT (logout / cambio de contraseña) ───────

    /**
     * Versión del token del usuario.
     * Se incrementa en cada logout, cambio de contraseña o recuperación de contraseña,
     * invalidando automáticamente todos los JWT emitidos con versiones anteriores.
     * El JWT incluye este valor en sus claims; el filtro lo compara en cada request.
     */
    @Column(name = "token_version", nullable = false)
    private Long tokenVersion = 0L;
}
