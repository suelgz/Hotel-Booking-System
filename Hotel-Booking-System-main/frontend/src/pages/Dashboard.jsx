// Dashboard.jsx - main overview page with summary cards and recent activity
import { useState, useEffect } from "react";
import { getRooms, getReservations, getCustomers } from "../services/api";
import StatusBadge from "../components/StatusBadge";
import "./Dashboard.css";

// One summary card with an icon, label, count and optional sub-label
function StatCard({ label, value, icon, accent, sub }) {
  return (
    <div className={`stat-card stat-card--${accent}`}>
      <div className="stat-icon">{icon}</div>
      <div className="stat-info">
        <div className="stat-value">{value}</div>
        <div className="stat-label">{label}</div>
        {sub && <div className="stat-sub">{sub}</div>}
      </div>
    </div>
  );
}

export default function Dashboard() {
  const [rooms, setRooms] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchAll() {
      const [r, res, c] = await Promise.all([
        getRooms(),
        getReservations(),
        getCustomers(),
      ]);
      setRooms(r);
      setReservations(res);
      setCustomers(c);
      setLoading(false);
    }
    fetchAll();
  }, []);

  if (loading) {
    return <div className="loading-state">Loading dashboard…</div>;
  }

  // Derived stats
  const totalRooms = rooms.length;
  const available = rooms.filter((r) => r.status === "Available").length;
  const occupied = rooms.filter((r) => r.status === "Occupied").length;
  const maintenance = rooms.filter((r) => r.status === "Maintenance").length;
  const activeRes = reservations.filter((r) => r.status === "Active").length;

  // Most recent 5 reservations for the table
  const recentReservations = [...reservations].reverse().slice(0, 5);

  return (
    <div className="page-content">
      {/* Welcome strip */}
      <div className="dash-welcome">
        <div>
          <h1 className="dash-heading">Good morning ✦</h1>
          <p className="dash-sub">
            Here's what's happening at Aurum Hotel today.
          </p>
        </div>
        <div className="dash-total-guests">
          <span>{customers.length}</span> registered guests
        </div>
      </div>

      {/* Summary cards */}
      <div className="stat-grid">
        <StatCard label="Total Rooms" value={totalRooms} icon="⊡" accent="navy" />
        <StatCard
          label="Available"
          value={available}
          icon="✓"
          accent="green"
          sub={`${Math.round((available / totalRooms) * 100)}% occupancy free`}
        />
        <StatCard
          label="Occupied"
          value={occupied}
          icon="⊛"
          accent="gold"
          sub={`${maintenance} in maintenance`}
        />
        <StatCard
          label="Active Reservations"
          value={activeRes}
          icon="◫"
          accent="blue"
        />
      </div>

      {/* Bottom two columns */}
      <div className="dash-bottom">
        {/* Recent reservations */}
        <div className="card dash-recent">
          <div className="dash-card-header">
            <span className="dash-card-title">Recent Reservations</span>
            <a href="/reservations" className="dash-card-link">
              View all →
            </a>
          </div>
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Guest</th>
                  <th>Room</th>
                  <th>Check-in</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {recentReservations.map((res) => (
                  <tr key={res.reservationId}>
                    <td className="res-id">{res.reservationId}</td>
                    <td>{res.customerName}</td>
                    <td>#{res.roomNumber}</td>
                    <td>{res.checkInDate}</td>
                    <td>
                      <StatusBadge value={res.status} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Room status overview */}
        <div className="card dash-room-status">
          <div className="dash-card-header">
            <span className="dash-card-title">Room Status Overview</span>
          </div>
          <div className="room-status-list">
            {rooms.map((room) => (
              <div key={room.roomId} className="room-status-row">
                <div className="room-status-left">
                  <span className="room-num">#{room.roomNumber}</span>
                  <span className="room-type-sm">{room.type}</span>
                </div>
                <StatusBadge value={room.status} />
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
