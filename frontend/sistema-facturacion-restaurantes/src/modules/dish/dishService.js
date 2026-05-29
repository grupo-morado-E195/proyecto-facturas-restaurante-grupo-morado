import apiClient from "../../global/services/apiClient.js";

/**
 * Service para el módulo de Gestión de Platos (Inventario).
 * Endpoint base: /api/platos
 */

/** Lista platos con paginación y filtros opcionales */
export async function getDishes({ name, status, page = 0, size = 50 } = {}) {
  const params = { page, size, sort: "id,asc" };
  if (name)   params.name   = name;
  if (status) params.status = status;
  const { data } = await apiClient.get("platos", { params });
  return data; // PageResultDTO<DishResultDTO>
}

/** Obtiene el detalle de un plato por ID */
export async function getDishById(id) {
  const { data } = await apiClient.get(`platos/${id}`);
  return data; // DishResultDTO
}

/** Lista platos disponibles para órdenes (ACTIVOS con stock > 0) */
export async function getAvailableDishes() {
  const { data } = await apiClient.get("platos/disponibles");
  return data; // List<DishResultDTO>
}

/** Registra un nuevo plato */
export async function createDish({ name, description, price, stock, menuId }) {
  const { data } = await apiClient.post("platos", {
    name,
    description,
    price: Number(price),
    stock: Number(stock),
    menuId: Number(menuId),
  });
  return data; // DishResultDTO
}

/** Modifica los datos de un plato */
export async function updateDish(id, { name, description, price, stock, menuId, status }) {
  const { data } = await apiClient.put(`platos/${id}`, {
    name,
    description,
    price:  price  !== undefined ? Number(price)  : undefined,
    stock:  stock  !== undefined ? Number(stock)  : undefined,
    menuId: menuId !== undefined ? Number(menuId) : undefined,
    status,
  });
  return data; // DishResultDTO
}

/** Desactiva un plato */
export async function deactivateDish(id) {
  const { data } = await apiClient.put(`platos/${id}/desactivar`);
  return data;
}

/** Reactiva un plato */
export async function reactivateDish(id) {
  const { data } = await apiClient.put(`platos/${id}/reactivar`);
  return data;
}
