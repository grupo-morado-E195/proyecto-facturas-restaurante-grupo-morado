export default function StatCard({
  label,
  value,
  subtitle,
  accentColor = "#E87722",
  icon,
}) {
  return (
    <div
      className="bg-white rounded-xl p-5 shadow-sm border-l-4 flex items-start justify-between gap-4"
      style={{ borderLeftColor: accentColor }}
    >
      <div className="flex-1 min-w-0">
        <p className="text-xs font-bold text-gray-500 uppercase tracking-wide truncate">
          {label}
        </p>
        <p className="text-2xl font-black text-gray-900 mt-1 leading-tight">
          {value}
        </p>
        {subtitle && <p className="text-xs text-gray-400 mt-1">{subtitle}</p>}
      </div>
      {icon && (
        <div
          className="w-11 h-11 rounded-xl flex items-center justify-center flex-shrink-0"
          style={{ backgroundColor: `${accentColor}18` }}
        >
          <span style={{ color: accentColor }} className="text-xl">
            {icon}
          </span>
        </div>
      )}
    </div>
  );
}
