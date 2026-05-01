import { NavLink } from "react-router-dom";
import "./Sidebar.css";

const navItems = [
  { to: "/", label: "Dashboard", icon: "D" },
  { to: "/rooms", label: "Rooms", icon: "R" },
  { to: "/availability", label: "Availability", icon: "A" },
  { to: "/reservations", label: "Reservations", icon: "B" },
  { to: "/customers", label: "Customers", icon: "C" },
];

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <span className="brand-icon">A</span>
        <div>
          <div className="brand-name">Aurum Hotel</div>
          <div className="brand-sub">Management System</div>
        </div>
      </div>

      <nav className="sidebar-nav">
        <div className="nav-section-label">Main Menu</div>
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === "/"}
            className={({ isActive }) =>
              `nav-item ${isActive ? "nav-item--active" : ""}`
            }
          >
            <span className="nav-icon">{item.icon}</span>
            <span className="nav-label">{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        <div className="sidebar-footer-info">
          <div className="footer-dot"></div>
          <span>Operations ready</span>
        </div>
      </div>
    </aside>
  );
}
