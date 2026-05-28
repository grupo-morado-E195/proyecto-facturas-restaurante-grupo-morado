import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth.js";
import { ROUTES } from "../constants/routes.js";
import ChangePasswordModal from "./ChangePasswordModal.jsx";

export default function Topbar({ screenName = "", onToggleSidebar }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const role     = user?.rol ?? "";
  const userName = user?.nombre ?? "Usuario";
  const roleInitial = userName ? userName[0].toUpperCase() : "U";

  const [dropdownOpen,       setDropdownOpen]       = useState(false);
  const [showChangePassword, setShowChangePassword] = useState(false);
  const dropdownRef = useRef(null);

  const roleLabel = {
    admin:  "Administrador",
    mesero: "Mesero",
    chef:   "Chef",
    cajero: "Cajero",
  }[role] ?? role;

  useEffect(() => {
    function handleClickOutside(e) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setDropdownOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = async () => {
    setDropdownOpen(false);
    await logout();
    navigate(ROUTES.LOGIN, { replace: true });
  };

  return (
    <>
      <header
        className="bg-[#1A0A00] h-14 px-4 md:px-6 flex items-center justify-between
        border-b-2 border-[#E87722] flex-shrink-0 z-40"
      >
        <div className="flex items-center gap-3">
          <button
            onClick={onToggleSidebar}
            className="md:hidden w-8 h-8 flex items-center justify-center text-gray-400
              hover:text-white transition-colors mr-1"
            aria-label="Menú"
          >
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" className="w-5 h-5">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
          <div className="w-8 h-8 rounded-lg bg-[#E87722] flex items-center justify-center flex-shrink-0">
            <svg viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2" className="w-4 h-4">
              <path strokeLinecap="round" strokeLinejoin="round"
                d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
          </div>
          <span className="text-white font-black text-sm tracking-wide hidden sm:block">
            SFR Sistema
          </span>
        </div>

        <div className="flex items-center gap-3 md:gap-4">
          {screenName && (
            <span className="text-gray-400 text-xs hidden md:block truncate max-w-[180px]">
              {screenName}
            </span>
          )}

          <div className="relative" ref={dropdownRef}>
            <button
              onClick={() => setDropdownOpen((v) => !v)}
              className="bg-[#3D1A00] rounded-full px-3 py-1.5 flex items-center gap-2
                hover:bg-[#5C2800] transition-colors focus:outline-none"
            >
              <div
                className="w-6 h-6 rounded-full bg-[#E87722] flex items-center justify-center
                text-white text-xs font-bold flex-shrink-0"
              >
                {roleInitial}
              </div>
              <div className="hidden sm:block">
                <span className="text-white text-xs font-bold">{userName}</span>
                <span className="text-gray-400 text-xs"> · {roleLabel}</span>
              </div>
              <svg
                fill="none" stroke="currentColor" viewBox="0 0 24 24"
                className={`w-3 h-3 text-gray-400 hidden sm:block transition-transform duration-200 ${dropdownOpen ? "rotate-180" : ""}`}
              >
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
              </svg>
            </button>

            {dropdownOpen && (
              <div className="absolute right-0 top-full mt-2 w-52 bg-white rounded-xl shadow-xl
                border border-gray-100 overflow-hidden z-50 animate-fade-in">
                <div className="px-4 py-3 border-b border-gray-100 bg-gray-50">
                  <p className="text-xs font-black text-gray-800 truncate">{userName}</p>
                  <p className="text-xs text-gray-500">{roleLabel}</p>
                </div>

                <button
                  onClick={() => { setDropdownOpen(false); setShowChangePassword(true); }}
                  className="w-full text-left flex items-center gap-3 px-4 py-3
                    text-sm text-gray-700 hover:bg-orange-50 hover:text-[#E87722] transition-colors"
                >
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" className="w-4 h-4 flex-shrink-0">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                      d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                  </svg>
                  Cambiar contraseña
                </button>

                <div className="border-t border-gray-100" />

                <button
                  onClick={handleLogout}
                  className="w-full text-left flex items-center gap-3 px-4 py-3
                    text-sm text-red-500 hover:bg-red-50 transition-colors"
                >
                  <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" className="w-4 h-4 flex-shrink-0">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                      d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                  </svg>
                  Cerrar sesión
                </button>
              </div>
            )}
          </div>
        </div>
      </header>

      {showChangePassword && (
        <ChangePasswordModal onClose={() => setShowChangePassword(false)} />
      )}
    </>
  );
}
