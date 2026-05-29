-- ============================================================
-- Migración: Campos para recuperación de contraseña temporal
-- Tabla: usuario
-- Fecha: 2026-05-29
-- ============================================================
-- INSTRUCCIÓN: Ejecutar este script manualmente en la base de datos
-- antes de iniciar el servidor con los cambios de la entidad User.
-- El proyecto usa ddl-auto=none por lo que JPA no lo aplica automáticamente.
-- ============================================================

ALTER TABLE usuario
    ADD COLUMN contrasena_temporal         VARCHAR(255)  NULL          COMMENT 'Contraseña temporal hasheada con BCrypt. Null si no hay recuperacion activa.',
    ADD COLUMN expiracion_contrasena_temporal DATETIME   NULL          COMMENT 'Fecha y hora de expiracion de la contraseña temporal.',
    ADD COLUMN debe_cambiar_contrasena     BOOLEAN       NOT NULL DEFAULT FALSE COMMENT 'Indica que el usuario debe cambiar su contraseña en el proximo login.';
