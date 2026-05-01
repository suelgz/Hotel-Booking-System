import { useState } from "react";
import { getAvailability } from "../services/api";
import StatusBadge from "../components/StatusBadge";
import "./Availability.css";

export default function Availability() {
  const [checkInDate, setCheckInDate] = useState("");
  const [checkOutDate, setCheckOutDate] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setResult(null);

    if (!checkInDate || !checkOutDate) {
      setError("Choose both check-in and check-out dates.");
      return;
    }

    setLoading(true);
    try {
      setResult(await getAvailability(checkInDate, checkOutDate));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const rooms = result?.availableRooms || [];
  const nights = result?.nights || 0;

  return (
    <div className="page-content">
      <div className="page-header">
        <div>
          <h1>Availability Check</h1>
          <p>Search real room availability by stay dates.</p>
        </div>
      </div>

      <div className="card availability-panel">
        <form className="availability-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Check-in</label>
            <input type="date" value={checkInDate} onChange={(e) => setCheckInDate(e.target.value)} />
          </div>
          <div className="form-group">
            <label>Check-out</label>
            <input type="date" value={checkOutDate} onChange={(e) => setCheckOutDate(e.target.value)} />
          </div>
          <button className="btn btn-primary" type="submit" disabled={loading}>
            {loading ? "Checking..." : "Check Availability"}
          </button>
        </form>

        {error && <div className="error-state">{error}</div>}
      </div>

      {loading && <div className="loading-state">Checking rooms...</div>}

      {result && !loading && (
        <div className="availability-results">
          <div className="availability-summary">
            <div>
              <span className="summary-label">Selected stay</span>
              <strong>{result.checkInDate} to {result.checkOutDate}</strong>
            </div>
            <div>
              <span className="summary-label">Nights</span>
              <strong>{nights}</strong>
            </div>
            <div>
              <span className="summary-label">Available rooms</span>
              <strong>{rooms.length}</strong>
            </div>
          </div>

          {rooms.length === 0 ? (
            <div className="card availability-empty">
              No rooms are available for those dates. Try a different stay window.
            </div>
          ) : (
            <div className="availability-grid">
              {rooms.map((room) => (
                <div key={room.roomId} className="availability-room-card">
                  <div className="availability-room-top">
                    <span className="room-number">Room {room.roomNumber}</span>
                    <StatusBadge value={room.status} />
                  </div>
                  <div className="availability-room-meta">
                    <span>{room.type}</span>
                    <span>{room.capacity} guests</span>
                  </div>
                  <div className="availability-price">
                    <span>${room.pricePerNight}/night</span>
                    <strong>${Number(room.pricePerNight * nights).toLocaleString()} total</strong>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
