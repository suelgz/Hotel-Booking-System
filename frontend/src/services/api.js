const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

async function request(path, options = {}) {
  let response;

  try {
    response = await fetch(`${BASE_URL}${path}`, {
      headers: { "Content-Type": "application/json", ...(options.headers || {}) },
      ...options,
    });
  } catch {
    throw new Error(
      "Could not reach the hotel API. If this is the live demo, the Render backend may need 30-60 seconds to wake up."
    );
  }

  if (response.status === 204) return { success: true };

  const text = await response.text();
  const data = text ? tryParseJson(text) : {};

  if (!response.ok) {
    throw new Error(data.error || data.message || `Request failed with status ${response.status}.`);
  }

  return data;
}

function tryParseJson(text) {
  try {
    return JSON.parse(text);
  } catch {
    return {};
  }
}

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

export async function getReservations() {
  return request("/reservations");
}

export async function createReservation(reservationData) {
  return request("/reservations", {
    method: "POST",
    body: JSON.stringify(reservationData),
  });
}

export async function cancelReservation(reservationId) {
  return request(`/reservations/${reservationId}/cancel`, { method: "PATCH" });
}

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
