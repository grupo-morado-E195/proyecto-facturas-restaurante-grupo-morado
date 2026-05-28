const variantClasses = {
  success: "bg-green-100 text-green-800",
  danger:  "bg-red-100 text-red-800",
  warning: "bg-yellow-100 text-yellow-800",
  info:    "bg-blue-100 text-blue-800",
  default: "bg-gray-100 text-gray-600",
};

export default function Badge({ variant = "default", children }) {
  return (
    <span
      className={`inline-block px-2.5 py-0.5 rounded-full text-xs font-bold ${variantClasses[variant] ?? variantClasses.default}`}
    >
      {children}
    </span>
  );
}
