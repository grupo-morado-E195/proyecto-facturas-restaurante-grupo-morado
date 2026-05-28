export default function SearchBar({ placeholder = "Buscar...", filters = [] }) {
  return (
    <div className="flex flex-col sm:flex-row gap-2 mb-4">
      <div className="relative flex-1">
        <svg
          className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
          />
        </svg>
        <input
          type="search"
          placeholder={placeholder}
          className="w-full pl-9 pr-3 py-2.5 border border-gray-200 rounded-lg text-sm
            bg-white focus:outline-none focus:ring-2 focus:ring-[#E87722]/30 focus:border-[#E87722]
            transition-colors"
        />
      </div>
      {filters.map((f, i) => (
        <select
          key={i}
          className="px-3 py-2.5 border border-gray-200 rounded-lg text-sm text-gray-700
            bg-white focus:outline-none focus:ring-2 focus:ring-[#E87722]/30 focus:border-[#E87722]
            transition-colors cursor-pointer"
        >
          {f.options.map((opt) => (
            <option key={opt}>{opt}</option>
          ))}
        </select>
      ))}
    </div>
  );
}
