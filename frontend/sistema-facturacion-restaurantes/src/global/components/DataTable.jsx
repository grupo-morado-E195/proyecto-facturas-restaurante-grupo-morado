export default function DataTable({
  columns = [],
  rows = [],
  emptyMessage = "Sin registros.",
}) {
  return (
    <div className="w-full overflow-x-auto rounded-lg">
      <table className="w-full text-sm border-collapse min-w-[600px]">
        <thead>
          <tr className="bg-gray-50 border-b-2 border-gray-200">
            {columns.map((col) => (
              <th
                key={col}
                className="px-4 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wide whitespace-nowrap"
              >
                {col}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.length === 0 ? (
            <tr>
              <td
                colSpan={columns.length}
                className="px-4 py-8 text-center text-gray-400 text-sm"
              >
                {emptyMessage}
              </td>
            </tr>
          ) : (
            rows.map((row, i) => (
              <tr
                key={i}
                className={`border-b border-gray-100 hover:bg-orange-50/50 transition-colors ${
                  i % 2 === 0 ? "bg-white" : "bg-gray-50/60"
                }`}
              >
                {row.map((cell, j) => (
                  <td key={j} className="px-4 py-3 text-gray-700 align-middle">
                    {cell}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
