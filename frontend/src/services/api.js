// services/api.js
// Backend: http://localhost:8080
// Başlatmak için: cd backend && mvn spring-boot:run

const BASE_URL = "http://localhost:8080/api";

// Merkezi fetch helper - tüm istekler buradan geçiyor
async function request(path, options = {}) {
  const response = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });

  // 204 No Content - DELETE sonrası body olmaz
  if (response.status === 204) return { success: true };

  const data = await response.json();

  if (!response.ok) {
    // Sunucudan gelen hata mesajını fırlat (RoomNotAvailableException vs.)
    throw new Error(data.error || `HTTP ${response.status}`);
  }

  return data;
}

// ─── Rooms ────────────────────────────────────────────────────────────────────

export async function getRooms() {
  return request("/rooms");
}

export async function createRoom(roomData) {
  return request("/rooms", {
    method: "POST",
    body: JSON.stringify(roomData),
  });
}

export async function updateRoom(roomId, roomData) {
  return request(`/rooms/${roomId}`, {
    method: "PUT",
    body: JSON.stringify(roomData),
  });
}

export async function deleteRoom(roomId) {
  return request(`/rooms/${roomId}`, { method: "DELETE" });
}

// ─── Reservations ─────────────────────────────────────────────────────────────

export async function getReservations() {
  return request("/reservations");
}

export async function createReservation(reservationData) {
  return request("/reservations", {
    method: "POST",
    body: JSON.stringify(reservationData),
  });
}

// Senin orijinal Reservation.cancel() metodunu Spring Boot üzerinden çağırıyor
export async function cancelReservation(reservationId) {
  return request(`/reservations/${reservationId}/cancel`, { method: "PATCH" });
}

// ─── Customers ────────────────────────────────────────────────────────────────

export async function getCustomers() {
  return request("/customers");
}

export async function createCustomer(customerData) {
  return request("/customers", {
    method: "POST",
    body: JSON.stringify(customerData),
  });
}

export async function updateCustomer(customerId, customerData) {
  return request(`/customers/${customerId}`, {
    method: "PUT",
    body: JSON.stringify(customerData),
  });
}

export async function deleteCustomer(customerId) {
  return request(`/customers/${customerId}`, { method: "DELETE" });
}
