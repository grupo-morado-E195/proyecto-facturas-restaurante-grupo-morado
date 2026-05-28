import { useState } from "react";
import DashboardLayout from "../../templates/DashboardLayout.jsx";
import PageHeader      from "../../global/components/PageHeader.jsx";
import Button          from "../../global/components/Button.jsx";
import Modal           from "../../global/components/Modal.jsx";
import Badge           from "../../global/components/Badge.jsx";
import BillingForm     from "./components/BillingForm.jsx";

const ORDENES_LISTAS = [
  { id: "#042", mesa: 3, mesero: "Laura M.",  total: "$85.000"  },
  { id: "#038", mesa: 9, mesero: "Carlos R.", total: "$120.000" },
];

const DETALLE_ITEMS = [
  { nombre: "Bandeja Paisa", cantidad: 1, precio: "$38.000", total: "$38.000" },
  { nombre: "Ajiaco",        cantidad: 1, precio: "$32.000", total: "$32.000" },
  { nombre: "Jugo de Lulo",  cantidad: 2, precio: "$8.000",  total: "$16.000" },
];

export default function Facturacion() {
  const [showModal, setShowModal] = useState(false);

  return (
    <DashboardLayout screenName="Facturación" activeItem="facturacion">
      <PageHeader title="Facturación" />

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
        <div className="bg-white rounded-xl p-5 shadow-sm">
          <h2 className="font-bold text-gray-800 text-sm mb-4">
            Órdenes Listas para Facturar
          </h2>
          <div className="divide-y divide-gray-100">
            {ORDENES_LISTAS.map((o) => (
              <div key={o.id} className="py-4 flex items-center justify-between gap-3">
                <div>
                  <p className="font-bold text-gray-900 text-sm">
                    {o.id} · Mesa {o.mesa}
                  </p>
                  <p className="text-xs text-gray-500 mt-0.5">Mesero: {o.mesero}</p>
                </div>
                <div className="flex items-center gap-3">
                  <span className="font-black text-[#E87722] text-sm">{o.total}</span>
                  <Button small onClick={() => setShowModal(true)}>Facturar</Button>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-xl p-5 shadow-sm">
          <div className="flex items-center justify-between mb-1">
            <h2 className="font-bold text-gray-800 text-sm">Detalle Orden #042</h2>
            <Badge variant="success">Lista</Badge>
          </div>
          <p className="text-xs text-gray-500 mb-4">Mesa 3 · Mesero: Laura Martínez</p>

          <div className="divide-y divide-gray-100 mb-4">
            {DETALLE_ITEMS.map((i) => (
              <div key={i.nombre} className="flex justify-between py-2.5 text-sm">
                <span className="text-gray-700">{i.cantidad}x {i.nombre}</span>
                <span className="font-semibold text-gray-800">{i.total}</span>
              </div>
            ))}
          </div>

          <div className="border-t-2 border-gray-200 pt-3 space-y-1.5 mb-5">
            {[
              ["Subtotal",       "$86.000", false],
              ["Impuesto (8%)",  "$6.880",  false],
              ["Total",          "$92.880", true ],
            ].map(([k, v, bold]) => (
              <div
                key={k}
                className={`flex justify-between text-sm ${
                  bold
                    ? "font-black text-[#E87722] text-base border-t border-gray-100 pt-2 mt-1"
                    : "text-gray-600"
                }`}
              >
                <span>{k}</span>
                <span>{v}</span>
              </div>
            ))}
          </div>

          <Button fullWidth onClick={() => setShowModal(true)}>
            Confirmar Facturación
          </Button>
        </div>
      </div>

      {showModal && (
        <Modal
          title="Confirmar Facturación — Orden #042"
          onClose={() => setShowModal(false)}
        >
          <BillingForm onCancel={() => setShowModal(false)} />
        </Modal>
      )}
    </DashboardLayout>
  );
}
