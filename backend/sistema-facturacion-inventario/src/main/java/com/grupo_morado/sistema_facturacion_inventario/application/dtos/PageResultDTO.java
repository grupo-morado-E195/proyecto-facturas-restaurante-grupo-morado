package com.grupo_morado.sistema_facturacion_inventario.application.dtos;

import java.util.List;

/**
 * DTO genérico de respuesta paginada.
 * Encapsula el contenido de una página junto con la metadata de paginación.
 *
 * @param <T>           Tipo del contenido de la página.
 * @param content       Elementos de la página actual.
 * @param page          Número de página actual (0-indexed).
 * @param size          Tamaño de la página solicitada.
 * @param totalElements Total de elementos en toda la colección.
 * @param totalPages    Total de páginas disponibles.
 */
public record PageResultDTO<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
