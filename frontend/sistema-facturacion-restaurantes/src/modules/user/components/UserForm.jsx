import { useState, useEffect } from "react";
import Swal   from "sweetalert2";
import Input  from "../../../global/components/Input.jsx";
import Select from "../../../global/components/Select.jsx";
import Button from "../../../global/components/Button.jsx";
import { getRoles, createUser, updateUser } from "../userService.js";

export default function UserForm({ onCancel, onSuccess, isEdit = false, initialData = null }) {
  const [name,     setName]     = useState(initialData?.name     ?? "");
  const [lastname, setLastname] = useState(initialData?.lastname ?? "");
  const [email,    setEmail]    = useState(initialData?.email    ?? "");
  const [roleId,   setRoleId]   = useState("");
  const [password, setPassword] = useState("");
  const [roles,    setRoles]    = useState([]);
  const [loading,  setLoading]  = useState(false);
  const [error,    setError]    = useState(null);

  useEffect(() => {
    getRoles()
      .then((data) => {
        setRoles(data);
        // Si es edición, pre-selecciona el rol actual
        if (isEdit && initialData?.role) {
          const match = data.find(
            (r) => r.name.toLowerCase() === initialData.role.toLowerCase()
          );
          if (match) setRoleId(String(match.id));
        }
      })
      .catch(() => setError("No se pudieron cargar los roles."));
  }, [isEdit, initialData]);

  const roleOptions = [
    { value: "", label: "Seleccionar rol..." },
    ...roles.map((r) => ({ value: String(r.id), label: r.name })),
  ];

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    if (!roleId) { setError("Selecciona un rol."); return; }
    setLoading(true);
    try {
      if (isEdit) {
        await updateUser(initialData.id, { 
          name, 
          lastname, 
          roleId: Number(roleId), 
          password: password.trim() ? password : null 
        });
        await Swal.fire({
          icon: "success",
          title: "Usuario actualizado",
          text: "El usuario ha sido actualizado correctamente.",
          timer: 1500,
          showConfirmButton: false,
        });
      } else {
        await createUser({ 
          name, 
          lastname, 
          email, 
          roleId: Number(roleId), 
          password: password.trim() 
        });
        await Swal.fire({
          icon: "success",
          title: "Usuario registrado",
          text: "El usuario ha sido registrado exitosamente con la contraseña indicada.",
          confirmButtonColor: "#E87722",
        });
      }
      onSuccess?.();
    } catch (err) {
      setError(
        err.response?.data?.message ??
        err.response?.data?.error ??
        err.message ??
        "Error al guardar el usuario."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-0">
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-4">
        <Input
          label="Nombre"
          placeholder="Ej: Juan"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
        <Input
          label="Apellidos"
          placeholder="Ej: García López"
          value={lastname}
          onChange={(e) => setLastname(e.target.value)}
          required
        />
      </div>
      {!isEdit && (
        <Input
          label="Correo electrónico"
          type="email"
          placeholder="correo@restaurante.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
      )}
      <Select
        label="Rol"
        options={roleOptions}
        value={roleId}
        onChange={(e) => setRoleId(e.target.value)}
        required
      />
      <Input
        label="Contraseña"
        type="password"
        placeholder={isEdit ? "•••••••• (Dejar vacío para no cambiar)" : "Mínimo 8 caracteres, 1 número y 1 símbolo"}
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required={!isEdit}
      />

      {error && (
        <p className="text-xs text-red-500 px-0.5 pb-2">{error}</p>
      )}

      <div className="flex gap-3 pt-2">
        <Button type="submit" fullWidth disabled={loading}>
          {loading
            ? (isEdit ? "Guardando..." : "Registrando...")
            : (isEdit ? "Guardar cambios" : "Registrar usuario")}
        </Button>
        <Button variant="secondary" fullWidth onClick={onCancel} disabled={loading}>
          Cancelar
        </Button>
      </div>
    </form>
  );
}
