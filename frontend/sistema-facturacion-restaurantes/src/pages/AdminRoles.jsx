import { useState, useEffect, useCallback } from "react";
import DashboardLayout from "../templates/DashboardLayout.jsx";
import PageHeader      from "../global/components/PageHeader.jsx";
import Badge           from "../global/components/Badge.jsx";
import DataTable       from "../global/components/DataTable.jsx";
import { getRoles, getUsers, createRole, updateRole, deleteRole } from "../modules/user/userService.js";
import Swal from "sweetalert2";

const ROLE_DETAILS = {
  administrador: {
    description: "Acceso total al sistema. Responsable de la gestión global de usuarios, mesas, inventario de platos, configuración de menús y generación de informes de ventas diarias.",
    color: "#E87722",
    permissions: ["Gestión Completa de Usuarios", "Gestión de Mesas", "Control de Inventario", "Creación y Activación de Menús", "Acceso a Informes Diarios de Ventas"]
  },
  mesero: {
    description: "Personal encargado del servicio directo al cliente. Responsable de la visualización y estado de mesas asignadas, la toma y registro de nuevas órdenes, y la edición o cancelación de pedidos pendientes.",
    color: "#2E9E5B",
    permissions: ["Ver Disponibilidad de Mesas", "Registrar Órdenes", "Modificar Pedidos Pendientes", "Cancelar Órdenes Pendientes"]
  },
  chef: {
    description: "Encargado de la cocina y preparación. Visualiza la cola activa de órdenes y actualiza su estado en tiempo real para agilizar la entrega del pedido.",
    color: "#2E7DB5",
    permissions: ["Ver Cola de Cocina", "Aceptar Órdenes (En Preparación)", "Marcar Órdenes Listas para Servir"]
  },
  cajero: {
    description: "Responsable del área de cobranza. Encargado de registrar la facturación de las órdenes servidas, aplicar los métodos de pago (efectivo, tarjeta, transferencia) y emitir el cierre de caja diario.",
    color: "#E8A020",
    permissions: ["Visualizar Cuentas Activas", "Facturar Órdenes Servidas", "Registrar Pagos de Clientes", "Generar Informe de Cierre de Caja"]
  }
};

export default function AdminRoles() {
  const [roles, setRoles] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedRole, setSelectedRole] = useState("administrador");

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const [rolesData, usersData] = await Promise.all([
        getRoles(),
        getUsers(0, 100),
      ]);
      setRoles(rolesData);
      setUsers(usersData.content ?? []);
    } catch {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "No se pudieron cargar los datos de roles del sistema.",
        confirmButtonColor: "#E87722",
      });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleCreateRole = async () => {
    const { value: roleName } = await Swal.fire({
      title: 'Crear Nuevo Rol',
      input: 'text',
      inputLabel: 'Nombre del Rol',
      inputPlaceholder: 'Ej: REPARTIDOR',
      showCancelButton: true,
      confirmButtonColor: '#E87722',
      cancelButtonColor: '#6B7280',
      confirmButtonText: 'Crear',
      cancelButtonText: 'Cancelar',
      inputValidator: (value) => {
        if (!value || !value.trim()) {
          return '¡El nombre del rol es requerido!';
        }
      }
    });

    if (roleName) {
      try {
        await createRole(roleName.trim());
        await Swal.fire({
          icon: 'success',
          title: '¡Creado!',
          text: 'El rol ha sido creado con éxito.',
          confirmButtonColor: '#E87722',
          timer: 1500,
          showConfirmButton: false
        });
        fetchData();
      } catch (err) {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: err.response?.data?.message ?? 'No se pudo crear el rol.',
          confirmButtonColor: '#E87722'
        });
      }
    }
  };

  const handleEditRole = async (e, r) => {
    e.stopPropagation();
    const { value: newName } = await Swal.fire({
      title: 'Modificar Rol',
      input: 'text',
      inputLabel: 'Nuevo Nombre',
      inputValue: r.name,
      showCancelButton: true,
      confirmButtonColor: '#E87722',
      cancelButtonColor: '#6B7280',
      confirmButtonText: 'Guardar',
      cancelButtonText: 'Cancelar',
      inputValidator: (value) => {
        if (!value || !value.trim()) {
          return '¡El nombre no puede estar vacío!';
        }
      }
    });

    if (newName && newName.trim().toUpperCase() !== r.name.toUpperCase()) {
      try {
        await updateRole(r.id, newName.trim());
        await Swal.fire({
          icon: 'success',
          title: '¡Modificado!',
          text: 'El rol ha sido modificado con éxito.',
          confirmButtonColor: '#E87722',
          timer: 1500,
          showConfirmButton: false
        });
        if (selectedRole.toLowerCase() === r.name.toLowerCase()) {
          setSelectedRole(newName.trim());
        }
        fetchData();
      } catch (err) {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: err.response?.data?.message ?? 'No se pudo modificar el rol.',
          confirmButtonColor: '#E87722'
        });
      }
    }
  };

  const handleDeleteRole = async (e, r) => {
    e.stopPropagation();
    const result = await Swal.fire({
      title: '¿Eliminar Rol?',
      text: `¿Estás seguro de que deseas eliminar el rol "${r.name}"? Esta acción no se puede deshacer.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#6B7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    });

    if (result.isConfirmed) {
      try {
        await deleteRole(r.id);
        await Swal.fire({
          icon: 'success',
          title: '¡Eliminado!',
          text: 'El rol ha sido eliminado correctamente.',
          confirmButtonColor: '#E87722',
          timer: 1500,
          showConfirmButton: false
        });
        setSelectedRole("administrador");
        fetchData();
      } catch (err) {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: err.response?.data?.message ?? 'No se pudo eliminar el rol.',
          confirmButtonColor: '#E87722'
        });
      }
    }
  };

  const activeRoleDetails = ROLE_DETAILS[selectedRole.toLowerCase()] ?? {
    description: "Rol personalizado configurado por el administrador.",
    color: "#6B7280",
    permissions: ["Acceso Básico al Sistema", "Visualizar Menús"]
  };

  const usersInRole = users.filter(
    (u) => u.role?.toLowerCase() === selectedRole.toLowerCase()
  );

  const userRows = usersInRole.map((u) => [
    u.id,
    `${u.name} ${u.lastname}`,
    u.email,
    u.status === "ACTIVO" ? <Badge variant="success">Activo</Badge> : <Badge>Inactivo</Badge>
  ]);

  return (
    <DashboardLayout screenName="Roles del Sistema" activeItem="roles">
      <PageHeader
        title="Roles del Sistema"
        subtitle="Crea, modifica y elimina los roles de los usuarios del restaurante"
      />

      {loading && roles.length === 0 ? (
        <p className="text-center text-gray-400 py-12 text-sm">Cargando roles...</p>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-fade-in">
          {/* Listado de Roles */}
          <div className="space-y-3 lg:col-span-1">
            <div className="flex justify-between items-center mb-3">
              <h2 className="font-bold text-gray-700 text-xs uppercase tracking-wider">Roles</h2>
              <button
                onClick={handleCreateRole}
                className="px-2 py-1 bg-[#E87722] hover:bg-[#D66611] text-white text-[10px] font-black rounded-lg transition-colors flex items-center gap-1 shadow-sm"
              >
                + Nuevo Rol
              </button>
            </div>
            {roles.map((r) => {
              const details = ROLE_DETAILS[r.name.toLowerCase()] ?? {
                description: "Rol personalizado creado por el administrador.",
                color: "#6B7280"
              };
              const isSelected = selectedRole.toLowerCase() === r.name.toLowerCase();
              const isBaseAdmin = r.name.toLowerCase() === "administrador";

              return (
                <div
                  key={r.id}
                  onClick={() => setSelectedRole(r.name)}
                  className={`p-4 rounded-xl border-2 cursor-pointer transition-all duration-150 shadow-sm group
                    ${isSelected 
                      ? "bg-white border-[#E87722] shadow-orange-100/50" 
                      : "bg-white hover:bg-gray-50 border-gray-200"
                    }`}
                >
                  <div className="flex items-center justify-between mb-2">
                    <span className="font-black text-gray-900 text-sm capitalize">{r.name}</span>
                    <div className="flex items-center gap-2">
                      {!isBaseAdmin && (
                        <div className="opacity-0 group-hover:opacity-100 transition-opacity flex items-center gap-1.5 mr-1">
                          <button
                            title="Editar rol"
                            onClick={(e) => handleEditRole(e, r)}
                            className="p-1 text-gray-400 hover:text-blue-500 hover:bg-blue-50 rounded"
                          >
                            ✎
                          </button>
                          <button
                            title="Eliminar rol"
                            onClick={(e) => handleDeleteRole(e, r)}
                            className="p-1 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded"
                          >
                            🗑
                          </button>
                        </div>
                      )}
                      <span className="px-2 py-0.5 bg-gray-100 text-gray-400 text-[10px] font-bold rounded">ID #{r.id}</span>
                    </div>
                  </div>
                  <p className="text-xs text-gray-500 line-clamp-2 leading-relaxed">{details.description}</p>
                </div>
              );
            })}
          </div>

          {/* Detalle y Permisos */}
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
              <div className="flex items-center gap-3 mb-4">
                <div 
                  className="w-3 h-8 rounded" 
                  style={{ backgroundColor: activeRoleDetails.color }}
                />
                <div>
                  <h2 className="text-lg font-black text-gray-900 capitalize">{selectedRole}</h2>
                  <p className="text-xs text-gray-400">Resumen y descripción de atribuciones</p>
                </div>
              </div>

              <p className="text-sm text-gray-600 mb-6 leading-relaxed">
                {activeRoleDetails.description}
              </p>

              <h3 className="font-bold text-gray-800 text-xs uppercase tracking-wider mb-3">Permisos del Sistema</h3>
              <div className="flex flex-wrap gap-2">
                {activeRoleDetails.permissions.map((p) => (
                  <span 
                    key={p} 
                    className="px-3 py-1.5 bg-orange-50/40 text-gray-700 font-semibold rounded-lg text-xs border border-orange-100"
                  >
                    ✓ {p}
                  </span>
                ))}
              </div>
            </div>

            {/* Listado de Usuarios */}
            <div className="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
              <h3 className="font-bold text-gray-900 text-sm mb-4">Usuarios asignados a este rol ({usersInRole.length})</h3>
              <DataTable
                columns={["ID", "Nombre", "Correo", "Estado"]}
                rows={userRows.length > 0 ? userRows : [["—", "Ningún usuario asignado", "—", "—"]]}
              />
            </div>
          </div>
        </div>
      )}
    </DashboardLayout>
  );
}
