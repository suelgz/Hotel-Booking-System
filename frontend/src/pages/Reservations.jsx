// Reservations.jsx - manage all hotel reservations
import { useState, useEffect } from "react";
import {
  getReservations,
  createReservation,
  cancelReservation,
  getRooms,
  getCustomers,
} from "../services/api";
import StatusBadge from "../components/StatusBadge";
import Modal from "../components/Modal";
import "./Reservations.css";

const STATUSES = ["Active", "Completed", "Cancelled"];

function ReservationForm({ rooms, customers, onSubmit, onCancel }) {
  const [form, setForm] = useState({
    customerName: "",
    roomNumber: rooms[0]?.roomNumber || "",
    checkInDate: "",
    checkOutDate: "",
  });

  const set = (k, v) => setForm((f) => ({ ...f, [k]: v }));

  // Calculate nights and estimated price on the fly
  const nights =
    form.checkInDate && form.checkOutDate
      ? Math.max(
          0,
          Math.round(
            (new Date(form.checkOutDate) - new Date(form.checkInDate)) /
              (1000 * 60 * 60 * 24)
          )
        )
      : 0;

  const selectedRoom = rooms.find((r) => r.roomNumber === form.roomNumber);
  const estimatedTotal = selectedRoom ? nights * selectedRoom.pricePerNight : 0;

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.customerName || !form.roomNumber || !form.checkInDate || !form.checkOutDate) {
      alert("Please fill in all fields.");
      return;
    }
    if (nights <= 0) {
      alert("Check-out must be after check-in.");
      return;
    }
    onSubmit(form);
  };

  // Only show available rooms as options
  const availableRooms = rooms.filter((r) => r.status === "Available");

  return (
    <form onSubmit={handleSubmit}>
      <div className="modal-body">
        <div className="form-group">
          <label>Guest Name</label>
          <input
            value={form.customerName}
            onChange={(e) => set("customerName", e.target.value)}
            placeholder="e.g. Emily Carter"
            list="customer-suggestions"
          />
          {/* Suggests existing customers */}
          <datalist id="customer-suggestions">
            {customers.map((c) => (
              <option key={c.customerId} value={c.fullName} />
            ))}
          </datalist>
        </div>
        <div className="form-group">
          <label>Room</label>
          <select value={form.roomNumber} onChange={(e) => set("roomNumber", e.target.value)}>
            {availableRooms.length === 0 && (
              <option value="">No available rooms</option>
            )}
            {availableRooms.map((r) => (
              <option key={r.roomId} value={r.roomNumber}>
                #{r.roomNumber} — {r.type} · ${r.pricePerNight}/night
              </option>
            ))}
          </select>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Check-in</label>
            <input
              type="date"
              value={form.checkInDate}
              onChange={(e) => set("checkInDate", e.target.value)}
            />
          </div>
          <div className="form-group">
            <label>Check-out</label>
            <input
              type="date"
              value={form.checkOutDate}
              onChange={(e) => set("checkOutDate", e.target.value)}
            />
          </div>
        </div>
        {nights > 0 && (
          <div className="reservation-estimate">
            <span>{nights} night{nights !== 1 ? "s" : ""}</span>
            <span className="estimate-total">Est. total: <strong>${estimatedTotal}</strong></span>
          </div>
        )}
      </div>
      <div className="modal-footer">
        <button type="button" className="btn btn-outline" onClick={onCancel}>
          Cancel
        </button>
        <button type="submit" className="btn btn-primary">
          Create Reservation
        </button>
      </div>
    </form>
  );
}

export default function Reservations() {
  const [reservations, setReservations] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("All");
  const [showModal, setShowModal] = useState(false);

  const load = async () => {
    setLoading(true);
    const [res, r, c] = await Promise.all([
      getReservations(),
      getRooms(),
      getCustomers(),
    ]);
    setReservations(res);
    setRooms(r);
    setCustomers(c);
    setLoading(false);
  };

  useEffect(() => { load(); }, []);

  const handleCreate = async (data) => {
    await createReservation(data);
    setShowModal(false);
    load();
  };

  const handleCancel = async (id) => {
    if (!window.confirm("Cancel this reservation?")) return;
    await cancelReservation(id);
    load();
  };

  const filtered = reservations.filter((r) => {
    const q = search.toLowerCase();
    const matchSearch =
      r.customerName.toLowerCase().includes(q) ||
      r.roomNumber.includes(q) ||
      r.reservationId.toLowerCase().includes(q);
    const matchStatus = statusFilter === "All" || r.status === statusFilter;
    return matchSearch && matchStatus;
  });

  // Quick stats strip
  const active = reservations.filter((r) => r.status === "Active").length;
  const completed = reservations.filter((r) => r.status === "Completed").length;
  const cancelled = reservations.filter((r) => r.status === "Cancelled").length;

  return (
    <div className="page-content">
      <div className="page-header">
        <div>
          <h1>Reservations</h1>
          <p>{reservations.length} reservations total</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
          + New Reservation
        </button>
      </div>

      {/* Quick stats */}
      <div className="res-stats">
        <div className="res-stat">
          <span className="res-stat-val res-stat-blue">{active}</span>
          <span>Active</span>
        </div>
        <div className="res-stat">
          <span className="res-stat-val res-stat-green">{completed}</span>
          <span>Completed</span>
        </div>
        <div className="res-stat">
          <span className="res-stat-val res-stat-red">{cancelled}</span>
          <span>Cancelled</span>
        </div>
      </div>

      <div className="card">
        <div className="toolbar">
          <div className="search-wrap">
            <span className="search-icon">🔍</span>
            <input
              placeholder="Search guest, room, ID…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
          <select
            className="filter-select"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="All">All Statuses</option>
            {STATUSES.map((s) => <option key={s}>{s}</option>)}
          </select>
          <span className="toolbar-count">{filtered.length} results</span>
        </div>

        {loading ? (
          <div className="loading-state">Loading reservations…</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">◫</div>
            <p>No reservations found.</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Reservation ID</th>
                  <th>Guest</th>
                  <th>Room</th>
                  <th>Check-in</th>
                  <th>Check-out</th>
                  <th>Total</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((res) => (
                  <tr key={res.reservationId}>
                    <td className="res-id-cell">{res.reservationId}</td>
                    <td><strong>{res.customerName}</strong></td>
                    <td>#{res.roomNumber}</td>
                    <td>{res.checkInDate}</td>
                    <td>{res.checkOutDate}</td>
                    <td className="price-cell">
                      ${res.totalPrice.toLocaleString()}
                    </td>
                    <td><StatusBadge value={res.status} /></td>
                    <td>
                      {res.status === "Active" && (
                        <button
                          className="btn btn-danger btn-sm"
                          onClick={() => handleCancel(res.reservationId)}
                        >
                          Cancel
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {showModal && (
        <Modal title="New Reservation" onClose={() => setShowModal(false)}>
          <ReservationForm
            rooms={rooms}
            customers={customers}
            onSubmit={handleCreate}
            onCancel={() => setShowModal(false)}
          />
        </Modal>
      )}
    </div>
  );
}
