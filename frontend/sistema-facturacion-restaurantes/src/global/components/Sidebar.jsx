import { Link } from "react-router-dom";
import { SIDEBAR_BY_ROLE } from "../constants/sidebarItems.js";

export default function Sidebar({ role = "", active, isOpen = true, onClose }) {
  const items = SIDEBAR_BY_ROLE[role] ?? [];

  return (
    <>
      {isOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-20 md:hidden"
          onClick={onClose}
        />
      )}

      <aside
        className={`
          fixed top-14 left-0 bottom-0 z-30 w-56 bg-[#3D1A00] flex flex-col pt-4 transition-transform duration-300
          md:static md:translate-x-0 md:z-auto md:top-auto md:flex-shrink-0 md:h-full
          ${isOpen ? "translate-x-0" : "-translate-x-full"}
        `}
      >
        <nav className="flex-1 overflow-y-auto">
          {items.map(({ icon, label, key, path }) => {
            const isActive = active === key;
            return (
              <Link
                key={key}
                to={path}
                onClick={onClose}
                className={`
                  flex items-center gap-3 px-4 py-3 transition-colors
                  border-l-4 select-none no-underline
                  ${
                    isActive
                      ? "bg-[#E87722] border-[#FFA94D] text-white"
                      : "border-transparent text-gray-300 hover:bg-white/10 hover:text-white"
                  }
                `}
              >
                <span className="text-base flex-shrink-0">{icon}</span>
                <span className={`text-sm ${isActive ? "font-bold" : "font-medium"}`}>
                  {label}
                </span>
              </Link>
            );
          })}
        </nav>
      </aside>
    </>
  );
}
