import { useState, useEffect, useCallback } from "react";
import Swal from "sweetalert2";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import Button          from "../../global/components/Button.jsx";
import Input           from "../../global/components/Input.jsx";
import DataTable       from "../../global/components/DataTable.jsx";
import Badge           from "../../global/components/Badge.jsx";
import { getDailySalesReport, downloadPdf } from "./reportService.js";
import { getOrders, getOrderById } from "../order/orderService.js";

const ESTADO_BADGE = {
  PENDIENTE:      <Badge variant="danger">Pendiente</Badge>,
  EN_PREPARACION: <Badge variant="warning">En preparación</Badge>,
  LISTO:          <Badge variant="success">Listo</Badge>,
  LISTA:          <Badge variant="success">Lista</Badge>,
  PAGADO:         <Badge variant="info">Pagado</Badge>,
  FACTURADA:      <Badge variant="info">Facturada</Badge>,
  CANCELADO:      <Badge>Cancelado</Badge>,
  CANCELADA:      <Badge>Cancelada</Badge>,
};

const fmt = (n) =>
  n !== undefined ? `$${Number(n).toLocaleString("es-CO")}` : "—";

export default function AdminInformes() {
  const today = new Date().toISOString().split("T")[0];
  const [fecha,       setFecha]       = useState(today);
  const [loading,     setLoading]     = useState(false);
  const [orders,      setOrders]      = useState([]);
  const [orderDetails, setOrderDetails] = useState([]);
  const [loadingData, setLoadingData] = useState(false);
  const [nombreGenerador, setNombreGenerador] = useState("Administrador");

  // Obtener nombre del administrador logueado
  useEffect(() => {
    try {
      const userStored = localStorage.getItem("sfr_user");
      if (userStored) {
        const adminUser = JSON.parse(userStored);
        if (adminUser?.name) {
          setNombreGenerador(`${adminUser.name} ${adminUser.lastname ?? ""}`.trim());
        }
      }
    } catch (e) {
      console.error("Error reading sfr_user", e);
    }
  }, []);

  const fetchReportData = useCallback(async (fechaSelected) => {
    if (!fechaSelected) return;
    setLoadingData(true);
    try {
      const res = await getOrders({ page: 0, size: 300 });
      const allOrders = res.content ?? [];
      
      // Filtrar órdenes por fecha seleccionada
      const filtered = allOrders.filter((o) => {
        const orderDate = o.fechaCreacion?.split("T")[0];
        return orderDate === fechaSelected;
      });
      setOrders(filtered);

      // Cargar detalles de cada orden de forma concurrente
      const details = await Promise.all(
        filtered.map(async (o) => {
          try {
            return await getOrderById(o.id);
          } catch {
            return { id: o.id, status: o.estado, details: [], total: 0 };
          }
        })
      );
      setOrderDetails(details);
    } catch (err) {
      console.error("Error al cargar datos del informe:", err);
    } finally {
      setLoadingData(false);
    }
  }, []);

  useEffect(() => {
    fetchReportData(fecha);
  }, [fecha, fetchReportData]);

  // Cálculo de estadísticas basadas estrictamente en órdenes facturadas (PAGADO / FACTURADA)
  const invoicedOrdersList = orderDetails.filter(
    (od) => od.status === "PAGADO" || od.status === "FACTURADA"
  );

  const totalSales = invoicedOrdersList.reduce((sum, od) => sum + (od.total ?? 0), 0);

  // Ventas por mesero
  const waiterSalesMap = {};
  invoicedOrdersList.forEach((od) => {
    const summary = orders.find((o) => o.id === od.id);
    const meseroName = summary?.nombreMesero ?? "Mesero Desconocido";
    waiterSalesMap[meseroName] = (waiterSalesMap[meseroName] ?? 0) + (od.total ?? 0);
  });

  const waiterSalesRows = Object.entries(waiterSalesMap).map(([name, total]) => [
    name,
    fmt(total)
  ]);

  // Platos más y menos vendidos
  const dishCounts = {};
  invoicedOrdersList.forEach((od) => {
    (od.details ?? []).forEach((item) => {
      const name = item.nombrePlato;
      dishCounts[name] = (dishCounts[name] ?? 0) + item.cantidad;
    });
  });

  let platoMasVendido = "—";
  let platoMenosVendido = "—";
  let maxQty = -1;
  let minQty = Infinity;

  Object.entries(dishCounts).forEach(([name, qty]) => {
    if (qty > maxQty) {
      maxQty = qty;
      platoMasVendido = `${name} (${qty} unidades)`;
    }
    if (qty < minQty) {
      minQty = qty;
      platoMenosVendido = `${name} (${qty} unidades)`;
    }
  });

  if (Object.keys(dishCounts).length === 0) {
    platoMasVendido = "—";
    platoMenosVendido = "—";
  }

  const handleGenerarInforme = async () => {
    if (!fecha) {
      Swal.fire({
        icon: "warning",
        title: "Selecciona una fecha",
        confirmButtonColor: "#E87722",
      });
      return;
    }

    // Condición: Si no hay ventas registradas para este día, bloquear la generación de PDF
    if (invoicedOrdersList.length === 0) {
      Swal.fire({
        icon: "error",
        title: "Informe Vacío",
        text: "No se registraron ventas facturadas para este día. No se puede generar un PDF sin datos de ventas.",
        confirmButtonColor: "#E87722",
      });
      return;
    }

    setLoading(true);
    try {
      const blob = await getDailySalesReport(fecha);
      downloadPdf(blob, `informe-ventas-${fecha}.pdf`);
      await Swal.fire({
        icon:             "success",
        title:            "Informe generado",
        text:             "El PDF se ha descargado correctamente.",
        confirmButtonColor: "#E87722",
        timer: 2000,
        showConfirmButton: false,
      });
    } catch (err) {
      Swal.fire({
        icon:  "error",
        title: "Error al generar informe",
        text:  err.response?.data?.message ?? "No se pudo generar el informe para la fecha seleccionada.",
        confirmButtonColor: "#E87722",
      });
    } finally {
      setLoading(false);
    }
  };

  const fechaGeneracion = new Date().toLocaleDateString("es-CO", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  });

  return (
    <DashboardLayout screenName="Informes de Ventas" activeItem="informes">
      <PageHeader
        title="Informes de Ventas"
        subtitle="Monitorea y descarga los reportes diarios de ventas del restaurante"
      />

      <div className="bg-white rounded-xl p-5 shadow-sm mb-6 border border-gray-100">
        <h2 className="font-bold text-gray-800 text-sm mb-4">Seleccionar Fecha</h2>
        <div className="flex flex-col sm:flex-row gap-4 items-end">
          <div className="flex-1">
            <Input
              label="Fecha del Informe"
              type="date"
              value={fecha}
              onChange={(e) => setFecha(e.target.value)}
            />
          </div>
          <div className="mb-4">
            <Button 
              onClick={handleGenerarInforme} 
              disabled={loading || loadingData || invoicedOrdersList.length === 0}
            >
              {loading ? "Generando..." : "Descargar Informe PDF"}
            </Button>
          </div>
        </div>
      </div>

      {loadingData ? (
        <p className="text-center text-gray-400 py-12 text-sm">Cargando vista previa del informe...</p>
      ) : invoicedOrdersList.length === 0 ? (
        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100 text-center py-12">
          <span className="text-3xl">☕</span>
          <p className="text-sm text-gray-500 mt-2 font-semibold">
            No se registraron ventas facturadas para este día.
          </p>
          <p className="text-xs text-gray-400 mt-1">
            Para generar un reporte en PDF, debe existir al menos una orden facturada.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-fade-in">
          {/* Vista Previa del Informe de Backlog */}
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
              <div className="flex items-center justify-between border-b border-gray-100 pb-3 mb-5">
                <div>
                  <h3 className="font-black text-gray-900 text-base">Vista Previa del Informe</h3>
                  <p className="text-xs text-gray-400">Información consolidada de la fecha seleccionada</p>
                </div>
                <Badge variant="success">Listo para descarga</Badge>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <p className="text-[10px] uppercase font-bold text-gray-400 tracking-wider">1. Ventas Totales del Día</p>
                  <p className="text-3xl font-black text-green-600 mt-1">{fmt(totalSales)}</p>
                  <p className="text-xs text-gray-400 mt-0.5">Órdenes facturadas: {invoicedOrdersList.length}</p>
                </div>

                <div className="space-y-4">
                  <div>
                    <p className="text-[10px] uppercase font-bold text-gray-400 tracking-wider">3. Plato Más Vendido</p>
                    <p className="text-sm font-semibold text-gray-800 mt-1">⭐ {platoMasVendido}</p>
                  </div>
                  <div>
                    <p className="text-[10px] uppercase font-bold text-gray-400 tracking-wider">4. Plato Menos Vendido</p>
                    <p className="text-sm font-semibold text-gray-800 mt-1">📉 {platoMenosVendido}</p>
                  </div>
                </div>
              </div>

              <div className="border-t border-gray-100 mt-6 pt-5 grid grid-cols-1 md:grid-cols-2 gap-4 text-xs text-gray-500">
                <div>
                  <span className="font-bold text-gray-400 block uppercase text-[9px] tracking-wider">5. Fecha de Generación</span>
                  <span className="text-gray-700 font-semibold">{fechaGeneracion}</span>
                </div>
                <div>
                  <span className="font-bold text-gray-400 block uppercase text-[9px] tracking-wider">6. Nombre del Usuario que Generó</span>
                  <span className="text-gray-700 font-semibold">{nombreGenerador}</span>
                </div>
              </div>
            </div>

            {/* Listado de todas las órdenes del día */}
            <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
              <h3 className="font-bold text-gray-900 text-sm mb-4">Órdenes de la Fecha</h3>
              <DataTable
                columns={["# Orden", "Mesa", "Mesero", "Hora", "Estado"]}
                rows={orders.map((o) => {
                  const timePart = o.fechaCreacion
                    ? new Date(o.fechaCreacion).toLocaleTimeString("es-CO", {
                        hour: "2-digit",
                        minute: "2-digit",
                      })
                    : "—";

                  return [
                    `#${String(o.id).padStart(3, "0")}`,
                    `Mesa ${o.numeroMesa}`,
                    o.nombreMesero ?? "—",
                    timePart,
                    ESTADO_BADGE[o.estado] ?? <Badge>{o.estado}</Badge>,
                  ];
                })}
              />
            </div>
          </div>

          {/* Ventas por Mesero */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
              <h3 className="font-bold text-gray-900 text-sm mb-4">2. Ventas por Mesero</h3>
              <DataTable
                columns={["Mesero", "Total Ventas"]}
                rows={waiterSalesRows.length > 0 ? waiterSalesRows : [["—", "Sin ventas"]]}
              />
            </div>
          </div>
        </div>
      )}
    </DashboardLayout>
  );
}
