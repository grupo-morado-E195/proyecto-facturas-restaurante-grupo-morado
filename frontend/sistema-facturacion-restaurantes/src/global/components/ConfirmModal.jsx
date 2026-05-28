import Modal  from "./Modal.jsx";
import Button from "./Button.jsx";

export default function ConfirmModal({
  title   = "¿Estás seguro?",
  message = "Esta acción no se puede deshacer.",
  confirmLabel = "Confirmar",
  confirmVariant = "danger",
  onConfirm,
  onClose,
}) {
  return (
    <Modal title={title} onClose={onClose} size="sm">
      <p className="text-sm text-gray-600 mb-6">{message}</p>
      <div className="flex gap-3">
        <Button variant={confirmVariant} fullWidth onClick={onConfirm}>
          {confirmLabel}
        </Button>
        <Button variant="secondary" fullWidth onClick={onClose}>
          Cancelar
        </Button>
      </div>
    </Modal>
  );
}
