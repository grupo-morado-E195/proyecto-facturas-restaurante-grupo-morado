export default function Select({
  label,
  id,
  options = [],
  defaultValue,
  required = false,
  className = "",
}) {
  const selectId = id ?? label?.toLowerCase().replace(/\s+/g, "-");

  return (
    <div className="mb-4">
      {label && (
        <label
          htmlFor={selectId}
          className="block text-xs font-bold text-gray-500 mb-1.5 uppercase tracking-wide"
        >
          {label}
          {required && <span className="text-red-500 ml-1">*</span>}
        </label>
      )}
      <select
        id={selectId}
        defaultValue={defaultValue}
        className={`w-full px-3 py-2.5 border border-gray-200 rounded-lg text-sm text-gray-800
          bg-white focus:outline-none focus:ring-2 focus:ring-[#E87722]/30 focus:border-[#E87722]
          transition-colors cursor-pointer ${className}`}
      >
        {options.map((opt) => {
          const val   = typeof opt === "string" ? opt : opt.value;
          const label = typeof opt === "string" ? opt : opt.label;
          return (
            <option key={val} value={val}>
              {label}
            </option>
          );
        })}
      </select>
    </div>
  );
}
