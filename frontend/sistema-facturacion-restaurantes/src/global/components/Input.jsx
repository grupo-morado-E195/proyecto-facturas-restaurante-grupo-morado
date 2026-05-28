export default function Input({
  label,
  id,
  type = "text",
  placeholder = "",
  value,
  onChange,
  defaultValue,
  asTextarea = false,
  rows = 3,
  required = false,
  className = "",
}) {
  const inputId = id ?? label?.toLowerCase().replace(/\s+/g, "-");
  const baseClass = `w-full px-3 py-2.5 border border-gray-200 rounded-lg text-sm text-gray-800
    bg-white focus:outline-none focus:ring-2 focus:ring-[#E87722]/30 focus:border-[#E87722]
    transition-colors ${className}`;

  return (
    <div className="mb-4">
      {label && (
        <label
          htmlFor={inputId}
          className="block text-xs font-bold text-gray-500 mb-1.5 uppercase tracking-wide"
        >
          {label}
          {required && <span className="text-red-500 ml-1">*</span>}
        </label>
      )}
      {asTextarea ? (
        <textarea
          id={inputId}
          rows={rows}
          placeholder={placeholder}
          defaultValue={defaultValue}
          className={`${baseClass} resize-none`}
        />
      ) : (
        <input
          id={inputId}
          type={type}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          defaultValue={defaultValue}
          className={baseClass}
        />
      )}
    </div>
  );
}
