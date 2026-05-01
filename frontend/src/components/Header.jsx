// Header.jsx - top bar with page title and user info
import { useLocation } from "react-router-dom";
import "./Header.css";

const pageTitles = {
  "/": "Dashboard",
  "/rooms": "Room Management",
  "/availability": "Availability",
  "/reservations": "Reservations",
  "/customers": "Customers",
};

export default function Header() {
  const location = useLocation();
  const title = pageTitles[location.pathname] || "Hotel Management";

  const today = new Date().toLocaleDateString("en-US", {
    weekday: "short",
    month: "long",
    day: "numeric",
  });

  return (
    <header className="header">
      <div className="header-left">
        <span className="header-title">{title}</span>
        <span className="header-date">{today}</span>
      </div>
      <div className="header-right">
        <div className="header-user">
          <div className="user-avatar">A</div>
          <div className="user-info">
            <span className="user-name">Admin</span>
            <span className="user-role">Hotel Manager</span>
          </div>
        </div>
      </div>
    </header>
  );
}
