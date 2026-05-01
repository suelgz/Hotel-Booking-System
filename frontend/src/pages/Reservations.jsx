import { useEffect, useState } from "react";
import {
  cancelReservation,
  createReservation,
  getCustomers,
  getReservations,
  getRooms,
} from "../services/api";
import StatusBadge from "../components/StatusBadge";
import Modal from "../components/Modal";
import "./Reservations.css";

const STATUSES = ["Active", "Completed", "Cancelled"];

function ReservationForm({ rooms, customers, onSubmit, onCancel, saving, error }) {
  const availableRooms = rooms.filter(
    (room) => room.status !== "Maintenance" && room.status !== "Cleaning"
  );
  const [form, setForm] = useState({
    customerName: "",
    roomNumber: availableRooms[0]?.roomNumber || "",
    checkInDate: "",
    checkOutDate: "",
  });

  const set = (key, value) => setForm((current) => ({ ...current, [key]: value }));

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

  const selectedRoom = rooms.find((room) => room.roomNumber === form.roomNumber);
  const estimatedTotal = selectedRoom ? nights * selectedRoom.pricePerNight : 0;

  const handleSubmit = (event) => {
    event.preventDefault();
    if (saving) return;
    onSubmit({
      ...form,
      customerName: form.customerName.trim(),
    });
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="modal-body">
        {error && <div className="form-error">{error}</div>}
        <div className="form-group">
          <label>Guest Name</label>
          <input
            value={form.customerName}
            onChange={(e) => set("customerName", e.target.value)}
            placeholder="e.g. Emily Carter"
            list="customer-suggestions"
          />
          <datalist id="customer-suggestions">
            {customers.map((customer) => (
              <option key={customer.customerId} value={customer.fullName} />
            ))}
          </datalist>
        </div>
        <div className="form-group">
          <label>Room</label>
          <select
            value={form.roomNumber}
            onChange={(e) => set("roomNumber", e.target.value)}
            disabled={availableRooms.length === 0}
          >
            {availableRooms.length === 0 && <option value="">No operational rooms</option>}
            {availableRooms.map((room) => (
              <option key={room.roomId} value={room.roomNumber}>
                #{room.roomNumber} - {room.type} - ${room.pricePerNight}/night
              </option>
            ))}
          </select>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Check-in</label>
            <input type="date" value={form.checkInDate} onChange={(e) => set("checkInDate", e.target.value)} />
          </div>
          <div className="form-group">
            <label>Check-out</label>
            <input type="date" value={form.checkOutDate} onChange={(e) => set("checkOutDate", e.target.value)} />
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
        <button type="button" className="btn btn-outline" onClick={onCancel} disabled={saving}>
          Cancel
        </button>
        <button type="submit" className="btn btn-primary" disabled={saving || availableRooms.length === 0}>
          {saving ? "Saving..." : "Create Reservation"}
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
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [formError, setFormError] = useState("");
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("All");
  const [showModal, setShowModal] = useState(false);

  const load = async () => {
    setLoading(true);
    setError("");
    try {
      const [reservationData, roomData, customerData] = await Promise.all([
        getReservations(),
        getRooms(),
        getCustomers(),
      ]);
      setReservations(reservationData);
      setRooms(roomData);
      setCustomers(customerData);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const timer = window.setTimeout(load, 0);
    return () => window.clearTimeout(timer);
  }, []);

  const validate = (data) => {
    if (!data.customerName || !data.roomNumber || !data.checkInDate || !data.checkOutDate) {
      return "Please fill in all fields.";
    }
    if (new Date(data.checkOutDate) <= new Date(data.checkInDate)) {
      return "Check-out must be after check-in.";
    }
    return "";
  };

  const handleCreate = async (data) => {
    const validationError = validate(data);
    if (validationError) {
      setFormError(validationError);
      return;
    }

    setSaving(true);
    setFormError("");
    try {
      await createReservation(data);
      setShowModal(false);
      await load();
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = async (id) => {
    if (!window.confirm("Cancel this reservation?")) return;
    setError("");
    try {
      await cancelReservation(id);
      await load();
    } catch (err) {
      setError(err.message);
    }
  };

  const openModal = () => {
    setFormError("");
    setShowModal(true);
  };

  const filtered = reservations.filter((reservation) => {
    const query = search.toLowerCase();
    const matchSearch =
      (reservation.customerName || "").toLowerCase().includes(query) ||
      (reservation.roomNumber || "").toLowerCase().includes(query) ||
      (reservation.reservationId || "").toLowerCase().includes(query);
    const matchStatus = statusFilter === "All" || reservation.status === statusFilter;
    return matchSearch && matchStatus;
  });

  const active = reservations.filter((reservation) => reservation.status === "Active").length;
  const completed = reservations.filter((reservation) => reservation.status === "Completed").length;
  const cancelled = reservations.filter((reservation) => reservation.status === "Cancelled").length;

  return (
    <div className="page-content">
      <div className="page-header">
        <div>
          <h1>Reservations</h1>
          <p>{reservations.length} reservations total</p>
        </div>
        <button className="btn btn-primary" onClick={openModal}>
          + New Reservation
        </button>
      </div>

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
            <span className="search-icon">#</span>
            <input placeholder="Search guest, room, ID..." value={search} onChange={(e) => setSearch(e.target.value)} />
          </div>
          <select className="filter-select" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="All">All Statuses</option>
            {STATUSES.map((status) => <option key={status}>{status}</option>)}
          </select>
          <span className="toolbar-count">{filtered.length} results</span>
        </div>

        {error && <div className="error-state">{error}</div>}

        {loading ? (
          <div className="loading-state">Loading reservations... Render may take a moment to wake up.</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">0</div>
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
                {filtered.map((reservation) => (
                  <tr key={reservation.reservationId}>
                    <td className="res-id-cell">{reservation.reservationId}</td>
                    <td><strong>{reservation.customerName}</strong></td>
                    <td>#{reservation.roomNumber}</td>
                    <td>{reservation.checkInDate}</td>
                    <td>{reservation.checkOutDate}</td>
                    <td className="price-cell">${Number(reservation.totalPrice || 0).toLocaleString()}</td>
                    <td><StatusBadge value={reservation.status} /></td>
                    <td>
                      {reservation.status === "Active" && (
                        <button className="btn btn-danger btn-sm" onClick={() => handleCancel(reservation.reservationId)}>
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
        <Modal title="New Reservation" onClose={() => !saving && setShowModal(false)}>
          <ReservationForm
            rooms={rooms}
            customers={customers}
            onSubmit={handleCreate}
            onCancel={() => setShowModal(false)}
            saving={saving}
            error={formError}
          />
        </Modal>
      )}
    </div>
  );
}
