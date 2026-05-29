import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import DashboardLayout from "../templates/DashboardLayout.jsx";
import StatCard        from "../global/components/StatCard.jsx";
import Button          from "../global/components/Button.jsx";
import { getOrders, getOrderById } from "../modules/order/orderService.js";
import { ROUTES } from "../global/constants/routes.js";
import { useWebSocket } from "../global/hooks/useWebSocket.js";

const fmt = (n) =>
  n !== undefined ? `$${Number(n).toLocaleString("es-CO")}` : "—";

export default function CajeroDashboard() {
  const navigate = useNavigate();
  const today = new Date().toLocaleDateString("sv-SE");

  const [ordenesListas, setOrdenesListas] = useState([]);
  const [loading, setLoading] = useState(false);
  const [stats, setStats] = useState({
    ordenesFacturadas: 0,
    totalVentas: 0,
    platoMasVendido: "—",
  });

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      // 1. Obtener órdenes listas para facturar
      const listasData = await getOrders({ status: "LISTO", page: 0, size: 20 });
      setOrdenesListas(listasData.content ?? []);

      // 2. Obtener estadísticas del día
      const allOrdersData = await getOrders({ page: 0, size: 200 });
      const dailySummaryOrders = (allOrdersData.content ?? []).filter((o) => {
        const orderDate = o.fechaCreacion ? new Date(o.fechaCreacion).toLocaleDateString("sv-SE") : "";
        return orderDate === today;
      });

      // Obtener detalles de forma concurrente para sumar totales y platos
      const details = await Promise.all(
        dailySummaryOrders.map(async (o) => {
          try {
            return await getOrderById(o.id);
          } catch {
            return { id: o.id, status: o.estado, details: [], total: 0 };
          }
        })
      );

      const invoicedOrders = details.filter(
        (od) => od.status === "PAGADO" || od.status === "FACTURADA"
      );

      const totalVentas = invoicedOrders.reduce((sum, od) => sum + (od.total ?? 0), 0);

      // Plato más vendido
      const dishCounts = {};
      invoicedOrders.forEach((od) => {
        (od.details ?? []).forEach((item) => {
          const name = item.nombrePlato;
          dishCounts[name] = (dishCounts[name] ?? 0) + item.cantidad;
        });
      });

      let platoMasVendido = "—";
      let maxQty = -1;
      Object.entries(dishCounts).forEach(([name, qty]) => {
        if (qty > maxQty) {
          maxQty = qty;
          platoMasVendido = `${name} (${qty})`;
        }
      });

      setStats({
        ordenesFacturadas: invoicedOrders.length,
        totalVentas,
        platoMasVendido,
      });

    } catch (err) {
      console.error("Error al cargar datos del cajero:", err);
    } finally {
      setLoading(false);
    }
  }, [today]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // WebSocket: escucha cambios en tiempo real para refrescar estadísticas y listado de órdenes listas
  useWebSocket("/topic/ordenes",     () => { fetchData(); });
  useWebSocket("/topic/facturacion", () => { fetchData(); });

  const STATS = [
    { label: "Órdenes Facturadas", value: String(stats.ordenesFacturadas), subtitle: "durante el día", accentColor: "#2E9E5B" },
    { label: "Total Ventas", value: fmt(stats.totalVentas), subtitle: "caja del día", accentColor: "#E87722" },
  ];

  return (
    <DashboardLayout screenName="Panel Cajero" activeItem="home">
      <h1 className="text-xl font-black text-gray-900 mb-5">Panel Cajero</h1>

      {loading && ordenesListas.length === 0 && stats.totalVentas === 0 ? (
        <p className="text-center text-gray-400 py-12 text-sm animate-pulse">Cargando panel de cajero...</p>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-5 animate-fade-in">
            {STATS.map((s) => (
              <StatCard key={s.label} {...s} />
            ))}
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-5 animate-fade-in">
            {/* Órdenes listas para facturar */}
            <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
              <h2 className="font-bold text-gray-800 text-sm mb-4">
                Órdenes Listas para Facturar
              </h2>
              {ordenesListas.length === 0 ? (
                <p className="text-center text-gray-400 py-8 text-xs">No hay órdenes listas para facturar.</p>
              ) : (
                <div className="divide-y divide-gray-100">
                  {ordenesListas.slice(0, 5).map((o) => (
                    <div key={o.id} className="flex justify-between items-center py-3">
                      <div>
                        <p className="font-bold text-gray-900 text-sm">
                          #{String(o.id).padStart(3, "0")} · Mesa {o.numeroMesa}
                        </p>
                        <p className="text-xs text-gray-500 mt-0.5">
                          Mesero: {o.nombreMesero ?? "—"}
                        </p>
                      </div>
                      <Button small onClick={() => navigate(ROUTES.CAJERO_FACTURACION)}>
                        Facturar
                      </Button>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Resumen del día */}
            <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100 flex flex-col justify-between">
              <div>
                <h2 className="font-bold text-gray-800 text-sm mb-4">Resumen del Día</h2>
                <div className="divide-y divide-gray-100">
                  {[
                    { label: "Órdenes facturadas", val: String(stats.ordenesFacturadas) },
                    { label: "Total ventas", val: fmt(stats.totalVentas) },
                    { label: "Plato más vendido", val: stats.platoMasVendido },
                  ].map((r) => (
                    <div key={r.label} className="flex justify-between py-3 text-sm">
                      <span className="text-gray-500">{r.label}</span>
                      <span className="font-black text-gray-800">{r.val}</span>
                    </div>
                  ))}
                </div>
              </div>
              <div className="mt-6">
                <Button variant="success" fullWidth onClick={() => navigate(ROUTES.CAJERO_INFORMES)}>
                  Cerrar Caja y Generar Informe
                </Button>
              </div>
            </div>
          </div>
        </>
      )}
    </DashboardLayout>
  );
}
