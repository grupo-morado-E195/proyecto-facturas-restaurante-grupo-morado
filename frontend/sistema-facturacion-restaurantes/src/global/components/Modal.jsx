import { useEffect } from "react";

export default function Modal({ title, onClose, children, size = "md" }) {
  useEffect(() => {
    const handleKey = (e) => {
      if (e.key === "Escape") onClose?.();
    };
    document.addEventListener("keydown", handleKey);
    return () => document.removeEventListener("keydown", handleKey);
  }, [onClose]);

  const sizeClass =
    size === "lg" ? "max-w-2xl" : size === "sm" ? "max-w-sm" : "max-w-md";

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose?.();
      }}
    >
      <div
        className={`bg-white rounded-2xl shadow-2xl w-full ${sizeClass} max-h-[90vh] flex flex-col`}
      >
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <h2 className="font-bold text-gray-900 text-base">{title}</h2>
          <button
            onClick={onClose}
            className="w-8 h-8 flex items-center justify-center rounded-full text-gray-400
              hover:bg-gray-100 hover:text-gray-600 transition-colors text-lg leading-none"
            aria-label="Cerrar"
          >
            ×
          </button>
        </div>
        <div className="overflow-y-auto px-6 py-5 flex-1">{children}</div>
      </div>
    </div>
  );
}
