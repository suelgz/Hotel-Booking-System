// Rooms.jsx - view, add, edit, and delete hotel rooms
import { useState, useEffect } from "react";
import { getRooms, createRoom, updateRoom, deleteRoom } from "../services/api";
import StatusBadge from "../components/StatusBadge";
import Modal from "../components/Modal";
import "./Rooms.css";

const ROOM_TYPES = ["Single", "Double", "Suite"];
const STATUSES = ["Available", "Occupied", "Maintenance"];

// The form used for both adding and editing a room
function RoomForm({ initial = {}, onSubmit, onCancel }) {
  const [form, setForm] = useState({
    roomNumber: initial.roomNumber || "",
    type: initial.type || "Single",
    capacity: initial.capacity || "",
    pricePerNight: initial.pricePerNight || "",
    status: initial.status || "Available",
  });

  const set = (k, v) => setForm((f) => ({ ...f, [k]: v }));

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.roomNumber || !form.capacity || !form.pricePerNight) {
      alert("Please fill in all fields.");
      return;
    }
    onSubmit({ ...form, capacity: +form.capacity, pricePerNight: +form.pricePerNight });
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="modal-body">
        <div className="form-row">
          <div className="form-group">
            <label>Room Number</label>
            <input
              value={form.roomNumber}
              onChange={(e) => set("roomNumber", e.target.value)}
              placeholder="e.g. 205"
            />
          </div>
          <div className="form-group">
            <label>Room Type</label>
            <select value={form.type} onChange={(e) => set("type", e.target.value)}>
              {ROOM_TYPES.map((t) => <option key={t}>{t}</option>)}
            </select>
          </div>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Capacity (guests)</label>
            <input
              type="number"
              min={1}
              value={form.capacity}
              onChange={(e) => set("capacity", e.target.value)}
              placeholder="2"
            />
          </div>
          <div className="form-group">
            <label>Price / Night ($)</label>
            <input
              type="number"
              min={0}
              value={form.pricePerNight}
              onChange={(e) => set("pricePerNight", e.target.value)}
              placeholder="149"
            />
          </div>
        </div>
        <div className="form-group">
          <label>Status</label>
          <select value={form.status} onChange={(e) => set("status", e.target.value)}>
            {STATUSES.map((s) => <option key={s}>{s}</option>)}
          </select>
        </div>
      </div>
      <div className="modal-footer">
        <button type="button" className="btn btn-outline" onClick={onCancel}>
          Cancel
        </button>
        <button type="submit" className="btn btn-primary">
          {initial.roomId ? "Save Changes" : "Add Room"}
        </button>
      </div>
    </form>
  );
}

export default function Rooms() {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [typeFilter, setTypeFilter] = useState("All");
  const [statusFilter, setStatusFilter] = useState("All");

  // null = closed, "add" = add modal, {room} = edit modal
  const [modal, setModal] = useState(null);

  const load = async () => {
    setLoading(true);
    setRooms(await getRooms());
    setLoading(false);
  };

  useEffect(() => { load(); }, []);

  const handleAdd = async (data) => {
    await createRoom(data);
    setModal(null);
    load();
  };

  const handleEdit = async (data) => {
    await updateRoom(modal.room.roomId, data);
    setModal(null);
    load();
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this room? This can't be undone.")) return;
    await deleteRoom(id);
    load();
  };

  // Filter + search
  const filtered = rooms.filter((r) => {
    const q = search.toLowerCase();
    const matchSearch = r.roomNumber.includes(q) || r.type.toLowerCase().includes(q);
    const matchType = typeFilter === "All" || r.type === typeFilter;
    const matchStatus = statusFilter === "All" || r.status === statusFilter;
    return matchSearch && matchType && matchStatus;
  });

  return (
    <div className="page-content">
      <div className="page-header">
        <div>
          <h1>Rooms</h1>
          <p>{rooms.length} total rooms in the system</p>
        </div>
        <button className="btn btn-primary" onClick={() => setModal("add")}>
          + Add Room
        </button>
      </div>

      <div className="card">
        {/* Toolbar */}
        <div className="toolbar">
          <div className="search-wrap">
            <span className="search-icon">🔍</span>
            <input
              placeholder="Search rooms…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
          <select
            className="filter-select"
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value)}
          >
            <option value="All">All Types</option>
            {ROOM_TYPES.map((t) => <option key={t}>{t}</option>)}
          </select>
          <select
            className="filter-select"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="All">All Statuses</option>
            {STATUSES.map((s) => <option key={s}>{s}</option>)}
          </select>
          <span className="toolbar-count">{filtered.length} rooms</span>
        </div>

        {/* Table */}
        {loading ? (
          <div className="loading-state">Loading rooms…</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">⊡</div>
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
                        <button
                          className="btn btn-outline btn-sm"
                          onClick={() => setModal({ room })}
                        >
                          Edit
                        </button>
                        <button
                          className="btn btn-danger btn-sm"
                          onClick={() => handleDelete(room.roomId)}
                        >
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

      {/* Add modal */}
      {modal === "add" && (
        <Modal title="Add New Room" onClose={() => setModal(null)}>
          <RoomForm onSubmit={handleAdd} onCancel={() => setModal(null)} />
        </Modal>
      )}

      {/* Edit modal */}
      {modal?.room && (
        <Modal title="Edit Room" onClose={() => setModal(null)}>
          <RoomForm
            initial={modal.room}
            onSubmit={handleEdit}
            onCancel={() => setModal(null)}
          />
        </Modal>
      )}
    </div>
  );
}
