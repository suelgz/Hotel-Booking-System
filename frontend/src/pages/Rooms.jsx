import { useEffect, useState } from "react";
import { createRoom, deleteRoom, getRooms, updateRoom } from "../services/api";
import StatusBadge from "../components/StatusBadge";
import Modal from "../components/Modal";
import "./Rooms.css";

const ROOM_TYPES = ["Single", "Double", "Suite"];
const STATUSES = ["Available", "Occupied", "Maintenance"];

function RoomForm({ initial = {}, onSubmit, onCancel, saving, error }) {
  const [form, setForm] = useState({
    roomNumber: initial.roomNumber || "",
    type: initial.type || "Single",
    capacity: initial.capacity || "",
    pricePerNight: initial.pricePerNight || "",
    status: initial.status || "Available",
  });

  const set = (key, value) => setForm((current) => ({ ...current, [key]: value }));

  const handleSubmit = (event) => {
    event.preventDefault();
    if (saving) return;
    onSubmit({
      ...form,
      roomNumber: form.roomNumber.trim(),
      capacity: Number(form.capacity),
      pricePerNight: Number(form.pricePerNight),
    });
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="modal-body">
        {error && <div className="form-error">{error}</div>}
        <div className="form-row">
          <div className="form-group">
            <label>Room Number</label>
            <input value={form.roomNumber} onChange={(e) => set("roomNumber", e.target.value)} placeholder="e.g. 205" />
          </div>
          <div className="form-group">
            <label>Room Type</label>
            <select value={form.type} onChange={(e) => set("type", e.target.value)}>
              {ROOM_TYPES.map((type) => <option key={type}>{type}</option>)}
            </select>
          </div>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Capacity (guests)</label>
            <input
              type="number"
              min="1"
              value={form.capacity}
              onChange={(e) => set("capacity", e.target.value)}
              placeholder="2"
            />
          </div>
          <div className="form-group">
            <label>Price / Night ($)</label>
            <input
              type="number"
              min="0"
              value={form.pricePerNight}
              onChange={(e) => set("pricePerNight", e.target.value)}
              placeholder="149"
            />
          </div>
        </div>
        <div className="form-group">
          <label>Status</label>
          <select value={form.status} onChange={(e) => set("status", e.target.value)}>
            {STATUSES.map((status) => <option key={status}>{status}</option>)}
          </select>
        </div>
      </div>
      <div className="modal-footer">
        <button type="button" className="btn btn-outline" onClick={onCancel} disabled={saving}>
          Cancel
        </button>
        <button type="submit" className="btn btn-primary" disabled={saving}>
          {saving ? "Saving..." : initial.roomId ? "Save Changes" : "Add Room"}
        </button>
      </div>
    </form>
  );
}

export default function Rooms() {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [formError, setFormError] = useState("");
  const [search, setSearch] = useState("");
  const [typeFilter, setTypeFilter] = useState("All");
  const [statusFilter, setStatusFilter] = useState("All");
  const [modal, setModal] = useState(null);

  const load = async () => {
    setLoading(true);
    setError("");
    try {
      setRooms(await getRooms());
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
    if (!data.roomNumber || !data.capacity || data.pricePerNight === "") return "Please fill in all fields.";
    if (data.capacity < 1) return "Capacity must be at least 1 guest.";
    if (data.pricePerNight < 0) return "Price per night cannot be negative.";
    return "";
  };

  const handleSave = async (data) => {
    const validationError = validate(data);
    if (validationError) {
      setFormError(validationError);
      return;
    }

    setSaving(true);
    setFormError("");
    try {
      if (modal?.room) {
        await updateRoom(modal.room.roomId, data);
      } else {
        await createRoom(data);
      }
      setModal(null);
      await load();
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this room? This cannot be undone.")) return;
    setError("");
    try {
      await deleteRoom(id);
      await load();
    } catch (err) {
      setError(err.message);
    }
  };

  const openModal = (value) => {
    setFormError("");
    setModal(value);
  };

  const filtered = rooms.filter((room) => {
    const query = search.toLowerCase();
    const matchSearch =
      (room.roomNumber || "").toLowerCase().includes(query) ||
      (room.type || "").toLowerCase().includes(query);
    const matchType = typeFilter === "All" || room.type === typeFilter;
    const matchStatus = statusFilter === "All" || room.status === statusFilter;
    return matchSearch && matchType && matchStatus;
  });

  return (
    <div className="page-content">
      <div className="page-header">
        <div>
          <h1>Rooms</h1>
          <p>{rooms.length} total rooms in the system</p>
        </div>
        <button className="btn btn-primary" onClick={() => openModal("add")}>
          + Add Room
        </button>
      </div>

      <div className="card">
        <div className="toolbar">
          <div className="search-wrap">
            <span className="search-icon">#</span>
            <input placeholder="Search rooms..." value={search} onChange={(e) => setSearch(e.target.value)} />
          </div>
          <select className="filter-select" value={typeFilter} onChange={(e) => setTypeFilter(e.target.value)}>
            <option value="All">All Types</option>
            {ROOM_TYPES.map((type) => <option key={type}>{type}</option>)}
          </select>
          <select className="filter-select" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="All">All Statuses</option>
            {STATUSES.map((status) => <option key={status}>{status}</option>)}
          </select>
          <span className="toolbar-count">{filtered.length} rooms</span>
        </div>

        {error && <div className="error-state">{error}</div>}

        {loading ? (
          <div className="loading-state">Loading rooms... Render may take a moment to wake up.</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">0</div>
            <p>No rooms match your filters.</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Room #</th>
                  <th>Type</th>
                  <th>Capacity</th>
                  <th>Price / Night</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((room) => (
                  <tr key={room.roomId}>
                    <td><strong>#{room.roomNumber}</strong></td>
                    <td><StatusBadge value={room.type} /></td>
                    <td>{room.capacity} guest{room.capacity > 1 ? "s" : ""}</td>
                    <td className="price-cell">${room.pricePerNight}<span>/night</span></td>
                    <td><StatusBadge value={room.status} /></td>
                    <td>
                      <div className="action-btns">
                        <button className="btn btn-outline btn-sm" onClick={() => openModal({ room })}>
                          Edit
                        </button>
                        <button className="btn btn-danger btn-sm" onClick={() => handleDelete(room.roomId)}>
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {modal === "add" && (
        <Modal title="Add New Room" onClose={() => !saving && setModal(null)}>
          <RoomForm onSubmit={handleSave} onCancel={() => setModal(null)} saving={saving} error={formError} />
        </Modal>
      )}

      {modal?.room && (
        <Modal title="Edit Room" onClose={() => !saving && setModal(null)}>
          <RoomForm
            initial={modal.room}
            onSubmit={handleSave}
            onCancel={() => setModal(null)}
            saving={saving}
            error={formError}
          />
        </Modal>
      )}
    </div>
  );
}
