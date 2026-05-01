import { useEffect, useState } from "react";
import { getAuditLogs, getDashboardSummary } from "../services/api";
import StatusBadge from "../components/StatusBadge";
import "./Dashboard.css";

function StatCard({ label, value, accent, sub }) {
  return (
    <div className={`stat-card stat-card--${accent}`}>
      <div className="stat-info">
        <div className="stat-value">{value}</div>
        <div className="stat-label">{label}</div>
        {sub && <div className="stat-sub">{sub}</div>}
      </div>
    </div>
  );
}

function ReservationList({ title, reservations }) {
  return (
    <div className="card ops-list-card">
      <div className="dash-card-header">
        <span className="dash-card-title">{title}</span>
      </div>
      <div className="ops-list">
        {reservations.length === 0 ? (
          <div className="ops-empty">No scheduled records for today.</div>
        ) : (
          reservations.map((reservation) => (
            <div key={reservation.reservationId} className="ops-row">
              <div>
                <strong>{reservation.customerName}</strong>
                <span>Room {reservation.roomNumber}</span>
              </div>
              <StatusBadge value={reservation.status} />
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default function Dashboard() {
  const [summary, setSummary] = useState(null);
  const [auditLogs, setAuditLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function fetchDashboard() {
      setLoading(true);
      setError("");
      try {
        const [summaryResult, auditResult] = await Promise.allSettled([
          getDashboardSummary(),
          getAuditLogs(),
        ]);

        if (summaryResult.status === "fulfilled") {
          setSummary(summaryResult.value);
        } else {
          setError("Dashboard data is unavailable right now. Please refresh in a moment.");
        }

        if (auditResult.status === "fulfilled") {
          setAuditLogs(auditResult.value.slice(0, 8));
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    fetchDashboard();
  }, []);

  if (loading) {
    return <div className="loading-state">Loading operations console... Render may take a moment to wake up.</div>;
  }

  const safeSummary = summary || {
    totalRooms: 0,
    availableRooms: 0,
    bookedRooms: 0,
    maintenanceRooms: 0,
    activeReservations: 0,
    occupancyRate: 0,
    estimatedRevenue: 0,
    todayArrivals: [],
    todayDepartures: [],
  };

  return (
    <div className="page-content">
      <div className="dash-welcome">
        <div>
          <h1 className="dash-heading">Operations Console</h1>
          <p className="dash-sub">Room status, today's movement, and recent booking activity in one place.</p>
        </div>
        <div className="dash-total-guests">
          <span>{safeSummary.occupancyRate}%</span> occupancy rate
        </div>
      </div>

      {error && <div className="error-state">{error}</div>}

      <div className="stat-grid stat-grid--ops">
        <StatCard label="Total Rooms" value={safeSummary.totalRooms} accent="navy" />
        <StatCard label="Available Rooms" value={safeSummary.availableRooms} accent="green" />
        <StatCard label="Booked Rooms" value={safeSummary.bookedRooms} accent="blue" />
        <StatCard label="Maintenance" value={safeSummary.maintenanceRooms} accent="gold" />
        <StatCard label="Active Reservations" value={safeSummary.activeReservations} accent="blue" />
        <StatCard
          label="Estimated Revenue"
          value={`$${Number(safeSummary.estimatedRevenue || 0).toLocaleString()}`}
          accent="green"
        />
      </div>

      <div className="ops-today-grid">
        <ReservationList title="Today's Arrivals" reservations={safeSummary.todayArrivals || []} />
        <ReservationList title="Today's Departures" reservations={safeSummary.todayDepartures || []} />
      </div>

      <div className="card activity-card">
        <div className="dash-card-header">
          <span className="dash-card-title">Activity Log</span>
          <a href="/availability" className="dash-card-link">Check availability</a>
        </div>
        <div className="activity-list">
          {auditLogs.length === 0 ? (
            <div className="ops-empty">No activity recorded yet.</div>
          ) : (
            auditLogs.map((entry) => (
              <div key={entry.id} className="activity-row">
                <div>
                  <div className="activity-action">{entry.action.replaceAll("_", " ")}</div>
                  <div className="activity-message">{entry.message}</div>
                </div>
                <div className="activity-meta">
                  <span>{entry.type}</span>
                  <time>{new Date(entry.timestamp).toLocaleString()}</time>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
