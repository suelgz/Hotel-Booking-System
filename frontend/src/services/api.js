const rawBaseUrl = import.meta.env.VITE_API_BASE_URL;
const BASE_URL = normalizeBaseUrl(rawBaseUrl || "http://localhost:8080/api");
const RETRY_DELAYS_MS = [2000, 4000, 8000, 16000, 30000];

async function request(path, options = {}) {
  const method = (options.method || "GET").toUpperCase();
  const maxAttempts = method === "GET" ? RETRY_DELAYS_MS.length + 1 : 1;
  let response;
  let networkError;

  for (let attempt = 0; attempt < maxAttempts; attempt += 1) {
    try {
      response = await fetch(`${BASE_URL}${path}`, {
        headers: { "Content-Type": "application/json", ...(options.headers || {}) },
        ...options,
      });
      networkError = null;
      break;
    } catch (error) {
      networkError = error;

      if (attempt < maxAttempts - 1) {
        await delay(RETRY_DELAYS_MS[attempt]);
      }
    }
  }

  if (networkError) {
    throw new Error(getConnectionErrorMessage());
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

function normalizeBaseUrl(url) {
  return url.replace(/\/+$/, "");
}

function delay(ms) {
  return new Promise((resolve) => {
    window.setTimeout(resolve, ms);
  });
}

function getConnectionErrorMessage() {
  if (import.meta.env.PROD && !rawBaseUrl) {
    return "The live frontend is missing VITE_API_BASE_URL. Set it in Vercel to your Render backend URL, for example https://your-render-service.onrender.com/api, then redeploy.";
  }

  if (import.meta.env.PROD && /^https?:\/\/(localhost|127\.0\.0\.1)(:\d+)?\/api$/i.test(BASE_URL)) {
    return "The live frontend is pointed at localhost. Update VITE_API_BASE_URL in Vercel to your Render backend URL, then redeploy the frontend.";
  }

  return "Could not reach the hotel API. If this is the live demo, the Render backend may need 30-60 seconds to wake up. Try again shortly, or check the backend /api/health endpoint.";
}

export async function getRooms() {
  return request("/rooms");
}

export async function getAvailability(checkInDate, checkOutDate) {
  const params = new URLSearchParams({ checkInDate, checkOutDate });
  return request(`/availability?${params.toString()}`);
}

export async function getDashboardSummary() {
  return request("/dashboard/summary");
}

export async function getAuditLogs() {
  return request("/audit-logs");
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
