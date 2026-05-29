package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository;

import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad OrderDetail (Detalle de Orden).
 */
@Repository
public interface OrderDetailDAO extends JpaRepository<OrderDetail, Long> {

    /**
     * Busca los detalles asociados a una orden por su identificador.
     *
     * @param orderId Identificador de la orden.
     * @return Lista de detalles.
     */
    List<OrderDetail> findByOrderId(Long orderId);
}
