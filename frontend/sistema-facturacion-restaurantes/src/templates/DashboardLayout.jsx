import { useState } from "react";
import Topbar  from "../global/components/Topbar.jsx";
import Sidebar from "../global/components/Sidebar.jsx";
import { useAuth } from "../global/hooks/useAuth.js";

export default function DashboardLayout({ screenName, activeItem, children }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { user } = useAuth();

  return (
    <div className="h-screen flex flex-col bg-[#FFF8F0] overflow-hidden">
      <Topbar
        screenName={screenName}
        onToggleSidebar={() => setSidebarOpen((v) => !v)}
      />
      <div className="flex flex-1 overflow-hidden relative">
        <Sidebar
          role={user?.rol}
          active={activeItem}
          isOpen={sidebarOpen}
          onClose={() => setSidebarOpen(false)}
        />
        <main className="flex-1 overflow-y-auto p-4 md:p-6 page-enter">
          {children}
        </main>
      </div>
    </div>
  );
}
