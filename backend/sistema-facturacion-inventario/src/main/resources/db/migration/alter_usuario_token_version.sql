-- ============================================================
-- Migración: Campo para invalidación de JWT por versión de token
-- Tabla: usuario
-- Fecha: 2026-05-29
-- ============================================================
-- INSTRUCCIÓN: Ejecutar este script manualmente en la base de datos
-- antes de reiniciar el servidor con los cambios de la entidad User.
-- El proyecto usa ddl-auto=none por lo que JPA no lo aplica automáticamente.
-- ============================================================

ALTER TABLE usuario
    ADD COLUMN token_version BIGINT NOT NULL DEFAULT 0
        COMMENT 'Versión del token. Se incrementa en logout/cambio de contraseña para invalidar JWTs anteriores.';
