import { useEffect, useState } from "react";
import { getCustomers, getReservations, getRooms } from "../services/api";
import StatusBadge from "../components/StatusBadge";
import "./Dashboard.css";

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
  const [error, setError] = useState("");

  useEffect(() => {
    async function fetchAll() {
      setLoading(true);
      setError("");
      try {
        const [roomData, reservationData, customerData] = await Promise.all([
          getRooms(),
          getReservations(),
          getCustomers(),
        ]);
        setRooms(roomData);
        setReservations(reservationData);
        setCustomers(customerData);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    fetchAll();
  }, []);

  if (loading) {
    return <div className="loading-state">Loading dashboard... Render may take a moment to wake up.</div>;
  }

  const totalRooms = rooms.length;
  const available = rooms.filter((room) => room.status === "Available").length;
  const occupied = rooms.filter((room) => room.status === "Occupied").length;
  const maintenance = rooms.filter((room) => room.status === "Maintenance").length;
  const activeRes = reservations.filter((reservation) => reservation.status === "Active").length;
  const availablePercent = totalRooms ? Math.round((available / totalRooms) * 100) : 0;
  const recentReservations = [...reservations].reverse().slice(0, 5);

  return (
    <div className="page-content">
      <div className="dash-welcome">
        <div>
          <h1 className="dash-heading">Good morning</h1>
          <p className="dash-sub">Here's what's happening at Aurum Hotel today.</p>
        </div>
        <div className="dash-total-guests">
          <span>{customers.length}</span> registered guests
        </div>
      </div>

      {error && <div className="error-state">{error}</div>}

      <div className="stat-grid">
        <StatCard label="Total Rooms" value={totalRooms} icon="#" accent="navy" />
        <StatCard
          label="Available"
          value={available}
          icon="OK"
          accent="green"
          sub={`${availablePercent}% occupancy free`}
        />
        <StatCard label="Occupied" value={occupied} icon="!" accent="gold" sub={`${maintenance} in maintenance`} />
        <StatCard label="Active Reservations" value={activeRes} icon="R" accent="blue" />
      </div>

      <div className="dash-bottom">
        <div className="card dash-recent">
          <div className="dash-card-header">
            <span className="dash-card-title">Recent Reservations</span>
            <a href="/reservations" className="dash-card-link">
              View all
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
                {recentReservations.length === 0 ? (
                  <tr>
                    <td colSpan="5">No reservations yet.</td>
                  </tr>
                ) : (
                  recentReservations.map((reservation) => (
                    <tr key={reservation.reservationId}>
                      <td className="res-id">{reservation.reservationId}</td>
                      <td>{reservation.customerName}</td>
                      <td>#{reservation.roomNumber}</td>
                      <td>{reservation.checkInDate}</td>
                      <td><StatusBadge value={reservation.status} /></td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>

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
