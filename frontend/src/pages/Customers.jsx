import { useEffect, useState } from "react";
import {
  createCustomer,
  deleteCustomer,
  getCustomers,
  updateCustomer,
} from "../services/api";
import Modal from "../components/Modal";
import "./Customers.css";

function CustomerForm({ initial = {}, onSubmit, onCancel, saving, error }) {
  const [form, setForm] = useState({
    fullName: initial.fullName || "",
    email: initial.email || "",
    phone: initial.phone || "",
  });

  const set = (key, value) => setForm((current) => ({ ...current, [key]: value }));

  const handleSubmit = (event) => {
    event.preventDefault();
    if (saving) return;
    onSubmit({
      fullName: form.fullName.trim(),
      email: form.email.trim(),
      phone: form.phone.trim(),
    });
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="modal-body">
        {error && <div className="form-error">{error}</div>}
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
        <button type="button" className="btn btn-outline" onClick={onCancel} disabled={saving}>
          Cancel
        </button>
        <button type="submit" className="btn btn-primary" disabled={saving}>
          {saving ? "Saving..." : initial.customerId ? "Save Changes" : "Add Customer"}
        </button>
      </div>
    </form>
  );
}

function Avatar({ name }) {
  const safeName = name || "?";
  const initials = safeName
    .split(" ")
    .map((part) => part[0])
    .slice(0, 2)
    .join("")
    .toUpperCase();
  const colors = ["#2980b9", "#27ae60", "#8e44ad", "#c9913d", "#e74c3c"];
  const color = colors[safeName.charCodeAt(0) % colors.length];

  return (
    <div className="customer-avatar" style={{ background: color }}>
      {initials}
    </div>
  );
}

export default function Customers() {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [formError, setFormError] = useState("");
  const [search, setSearch] = useState("");
  const [modal, setModal] = useState(null);

  const load = async () => {
    setLoading(true);
    setError("");
    try {
      setCustomers(await getCustomers());
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
    if (!data.fullName || !data.email || !data.phone) return "All fields are required.";
    if (!data.email.includes("@")) return "Please enter a valid email address.";
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
      if (modal?.customer) {
        await updateCustomer(modal.customer.customerId, data);
      } else {
        await createCustomer(data);
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
    if (!window.confirm("Remove this customer from the system?")) return;
    setError("");
    try {
      await deleteCustomer(id);
      await load();
    } catch (err) {
      setError(err.message);
    }
  };

  const openModal = (value) => {
    setFormError("");
    setModal(value);
  };

  const filtered = customers.filter((customer) => {
    const query = search.toLowerCase();
    return (
      (customer.fullName || "").toLowerCase().includes(query) ||
      (customer.email || "").toLowerCase().includes(query) ||
      (customer.phone || "").includes(query)
    );
  });

  const totalReservations = customers.reduce(
    (sum, customer) => sum + (customer.totalReservations || 0),
    0
  );

  return (
    <div className="page-content">
      <div className="page-header">
        <div>
          <h1>Customers</h1>
          <p>{customers.length} registered guests - {totalReservations} reservations total</p>
        </div>
        <button className="btn btn-primary" onClick={() => openModal("add")}>
          + Add Customer
        </button>
      </div>

      <div className="card">
        <div className="toolbar">
          <div className="search-wrap">
            <span className="search-icon">#</span>
            <input
              placeholder="Search by name or email..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
          <span className="toolbar-count">{filtered.length} customers</span>
        </div>

        {error && <div className="error-state">{error}</div>}

        {loading ? (
          <div className="loading-state">Loading customers... Render may take a moment to wake up.</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <div className="empty-icon">0</div>
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
                {filtered.map((customer) => (
                  <tr key={customer.customerId}>
                    <td>
                      <div className="customer-name-cell">
                        <Avatar name={customer.fullName} />
                        <div>
                          <div className="customer-name">{customer.fullName}</div>
                          <div className="customer-id">ID #{customer.customerId}</div>
                        </div>
                      </div>
                    </td>
                    <td className="email-cell">{customer.email}</td>
                    <td>{customer.phone}</td>
                    <td>
                      <span className="res-count-badge">
                        {customer.totalReservations || 0} stay{customer.totalReservations !== 1 ? "s" : ""}
                      </span>
                    </td>
                    <td>
                      <div className="action-btns">
                        <button className="btn btn-outline btn-sm" onClick={() => openModal({ customer })}>
                          Edit
                        </button>
                        <button className="btn btn-danger btn-sm" onClick={() => handleDelete(customer.customerId)}>
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
        <Modal title="Add New Customer" onClose={() => !saving && setModal(null)}>
          <CustomerForm
            onSubmit={handleSave}
            onCancel={() => setModal(null)}
            saving={saving}
            error={formError}
          />
        </Modal>
      )}

      {modal?.customer && (
        <Modal title="Edit Customer" onClose={() => !saving && setModal(null)}>
          <CustomerForm
            initial={modal.customer}
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
