// Customers.jsx - customer directory with add, edit, delete
import { useState, useEffect } from "react";
import {
  getCustomers,
  createCustomer,
  updateCustomer,
  deleteCustomer,
} from "../services/api";
import Modal from "../components/Modal";
import "./Customers.css";

function CustomerForm({ initial = {}, onSubmit, onCancel }) {
  const [form, setForm] = useState({
    fullName: initial.fullName || "",
    email: initial.email || "",
    phone: initial.phone || "",
  });

  const set = (k, v) => setForm((f) => ({ ...f, [k]: v }));

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.fullName || !form.email || !form.phone) {
      alert("All fields are required.");
      return;
    }
    if (!form.email.includes("@")) {
      alert("Please enter a valid email.");
      return;
    }
    onSubmit(form);
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="modal-body">
        <div className="form-group">
          <label>Full Name</label>
          <input
            value={form.fullName}
            onChange={(e) => set("fullName", e.target.value)}
            placeholder="e.g. Emily Carter"
          />
        </div>
        <div className="form-group">
          <label>Email Address</label>
          <input
            type="email"
            value={form.email}
            onChange={(e) => set("email", e.target.value)}
            placeholder="email@example.com"
          />
        </div>
        <div className="form-group">
          <label>Phone Number</label>
          <input
            value={form.phone}
            onChange={(e) => set("phone", e.target.value)}
            placeholder="+1 555 0100"
          />
        </div>
      </div>
      <div className="modal-footer">
        <button type="button" className="btn btn-outline" onClick={onCancel}>
          Cancel
        </button>
        <button type="submit" className="btn btn-primary">
          {initial.customerId ? "Save Changes" : "Add Customer"}
        </button>
      </div>
    </form>
  );
}

// Initials avatar for the customer table
function Avatar({ name }) {
  const initials = name
    .split(" ")
    .map((n) => n[0])
    .slice(0, 2)
    .join("")
    .toUpperCase();

  // Pick a color based on the first character - just for variety
  const colors = ["#2980b9", "#27ae60", "#8e44ad", "#c9913d", "#e74c3c"];
  const color = colors[name.charCodeAt(0) % colors.length];

  return (
    <div className="customer-avatar" style={{ background: color }}>
      {initials}
    </div>
  );
}

export default function Customers() {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [modal, setModal] = useState(null); // null | "add" | { customer }

  const load = async () => {
    setLoading(true);
    setCustomers(await getCustomers());
    setLoading(false);
  };

  useEffect(() => { load(); }, []);

  const handleAdd = async (data) => {
    await createCustomer(data);
    setModal(null);
    load();
  };

  const handleEdit = async (data) => {
    await updateCustomer(modal.customer.customerId, data);
    setModal(null);
    load();
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Remove this customer from the system?")) return;
    await deleteCustomer(id);
    load();
  };

  const filtered = customers.filter((c) => {
    const q = search.toLowerCase();
    return (
      c.fullName.toLowerCase().includes(q) ||
      c.email.toLowerCase().includes(q) ||
      c.phone.includes(q)
    );
  });

  const totalReservations = customers.reduce(
    (sum, c) => sum + c.totalReservations,
    0
  );

  return (
    <div className="page-content">
      <div className="page-header">
        <div>
          <h1>Customers</h1>
          <p>{customers.length} registered guests · {totalReservations} reservations total</p>
        </div>
        <button className="btn btn-primary" onClick={() => setModal("add")}>
          + Add Customer
        </button>
      </div>

      <div className="card">
        <div className="toolbar">
          <div className="search-wrap">
            <span className="search-icon">🔍</span>
            <input
              placeholder="Search by name or email…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
          <span className="toolbar-count">{filtered.length} customers</span>
        </div>

        {loading ? (
          <div className="loading-state">Loading customers…</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">⊙</div>
            <p>No customers found.</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Guest</th>
                  <th>Email</th>
                  <th>Phone</th>
                  <th>Reservations</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((c) => (
                  <tr key={c.customerId}>
                    <td>
                      <div className="customer-name-cell">
                        <Avatar name={c.fullName} />
                        <div>
                          <div className="customer-name">{c.fullName}</div>
                          <div className="customer-id">ID #{c.customerId}</div>
                        </div>
                      </div>
                    </td>
                    <td className="email-cell">{c.email}</td>
                    <td>{c.phone}</td>
                    <td>
                      <span className="res-count-badge">
                        {c.totalReservations} stay{c.totalReservations !== 1 ? "s" : ""}
                      </span>
                    </td>
                    <td>
                      <div className="action-btns">
                        <button
                          className="btn btn-outline btn-sm"
                          onClick={() => setModal({ customer: c })}
                        >
                          Edit
                        </button>
                        <button
                          className="btn btn-danger btn-sm"
                          onClick={() => handleDelete(c.customerId)}
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

      {modal === "add" && (
        <Modal title="Add New Customer" onClose={() => setModal(null)}>
          <CustomerForm onSubmit={handleAdd} onCancel={() => setModal(null)} />
        </Modal>
      )}

      {modal?.customer && (
        <Modal title="Edit Customer" onClose={() => setModal(null)}>
          <CustomerForm
            initial={modal.customer}
            onSubmit={handleEdit}
            onCancel={() => setModal(null)}
          />
        </Modal>
      )}
    </div>
  );
}
