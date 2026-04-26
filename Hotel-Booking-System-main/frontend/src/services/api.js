// services/api.js
// This file contains all the functions that talk to the backend.
// Right now they work with in-memory mock data, but when the
// Spring Boot API is ready, just replace the body of each function
// with a fetch() or axios call to the right endpoint.

import {
  mockRooms,
  mockReservations,
  mockCustomers,
} from "../data/mockData";

// In-memory "database" so changes persist during the session
let rooms = [...mockRooms];
let reservations = [...mockReservations];
let customers = [...mockCustomers];

// Small helper to simulate network latency - makes it feel real
const delay = (ms = 200) => new Promise((res) => setTimeout(res, ms));

// ─── Rooms ───────────────────────────────────────────────────────────────────
// GET /api/rooms
export async function getRooms() {
  await delay();
  return [...rooms];
}

// POST /api/rooms
export async function createRoom(roomData) {
  await delay();
  const newRoom = {
    ...roomData,
    roomId: rooms.length > 0 ? Math.max(...rooms.map((r) => r.roomId)) + 1 : 1,
  };
  rooms = [...rooms, newRoom];
  return newRoom;
}

// PUT /api/rooms/:id
export async function updateRoom(roomId, roomData) {
  await delay();
  rooms = rooms.map((r) => (r.roomId === roomId ? { ...r, ...roomData } : r));
  return rooms.find((r) => r.roomId === roomId);
}

// DELETE /api/rooms/:id
export async function deleteRoom(roomId) {
  await delay();
  rooms = rooms.filter((r) => r.roomId !== roomId);
  return { success: true };
}

// ─── Reservations ─────────────────────────────────────────────────────────────
// GET /api/reservations
export async function getReservations() {
  await delay();
  return [...reservations];
}

// POST /api/reservations
export async function createReservation(reservationData) {
  await delay();
  const nights =
    (new Date(reservationData.checkOutDate) -
      new Date(reservationData.checkInDate)) /
    (1000 * 60 * 60 * 24);
  const room = rooms.find((r) => r.roomNumber === reservationData.roomNumber);
  const totalPrice = room ? Math.round(room.pricePerNight * nights) : 0;

  const newReservation = {
    ...reservationData,
    reservationId: `RES-${String(reservations.length + 1).padStart(3, "0")}`,
    totalPrice,
    status: "Active",
  };
  reservations = [...reservations, newReservation];

  // Mark the room as occupied
  rooms = rooms.map((r) =>
    r.roomNumber === reservationData.roomNumber
      ? { ...r, status: "Occupied" }
      : r
  );
  return newReservation;
}

// PATCH /api/reservations/:id/cancel
export async function cancelReservation(reservationId) {
  await delay();
  let cancelledRoom = null;
  reservations = reservations.map((res) => {
    if (res.reservationId === reservationId) {
      cancelledRoom = res.roomNumber;
      return { ...res, status: "Cancelled" };
    }
    return res;
  });
  // Free the room back up
  if (cancelledRoom) {
    rooms = rooms.map((r) =>
      r.roomNumber === cancelledRoom ? { ...r, status: "Available" } : r
    );
  }
  return { success: true };
}

// ─── Customers ────────────────────────────────────────────────────────────────
// GET /api/customers
export async function getCustomers() {
  await delay();
  return [...customers];
}

// POST /api/customers
export async function createCustomer(customerData) {
  await delay();
  const newCustomer = {
    ...customerData,
    customerId:
      customers.length > 0
        ? Math.max(...customers.map((c) => c.customerId)) + 1
        : 1,
    totalReservations: 0,
  };
  customers = [...customers, newCustomer];
  return newCustomer;
}

// PUT /api/customers/:id
export async function updateCustomer(customerId, customerData) {
  await delay();
  customers = customers.map((c) =>
    c.customerId === customerId ? { ...c, ...customerData } : c
  );
  return customers.find((c) => c.customerId === customerId);
}

// DELETE /api/customers/:id
export async function deleteCustomer(customerId) {
  await delay();
  customers = customers.filter((c) => c.customerId !== customerId);
  return { success: true };
}
