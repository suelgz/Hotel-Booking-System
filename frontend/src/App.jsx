// App.jsx - top-level router and layout wrapper
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Sidebar from "./components/Sidebar";
import Header from "./components/Header";
import Dashboard from "./pages/Dashboard";
import Rooms from "./pages/Rooms";
import Availability from "./pages/Availability";
import Reservations from "./pages/Reservations";
import Customers from "./pages/Customers";
import "./styles/globals.css";

export default function App() {
  return (
    <BrowserRouter>
      <div className="app-layout">
        <Sidebar />
        <div className="main-content">
          <Header />
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/rooms" element={<Rooms />} />
            <Route path="/availability" element={<Availability />} />
            <Route path="/reservations" element={<Reservations />} />
            <Route path="/customers" element={<Customers />} />
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  );
}
