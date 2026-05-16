# Hotel Booking Management System

A full-stack hotel booking management system built as a student portfolio project. It demonstrates a React/Vite frontend, a Java 17 Spring Boot REST API, simple OOP models, and deployment to Vercel and Render.

## Live Demo

 https://hotel-booking-system-mocha.vercel.app/

The backend is hosted on Render's free plan. If it has been idle, the first request can take about 30-60 seconds while the service wakes up.

## Tech Stack

Frontend:
- React
- Vite
- JavaScript
- React Router
- CSS

Backend:
- Java 17
- Spring Boot
- Maven
- Docker
- In-memory data storage

Deployment:
- Vercel for the frontend
- Render for the backend API

## Features

- Dashboard with room, customer, and reservation summaries
- Room listing, creation, update, and deletion
- Customer listing, creation, update, and deletion
- Reservation creation with date validation
- Reservation cancellation with room status updates
- Clear loading and error states for API requests
- Simple health endpoint at `GET /api/health`
- SPA routing support for Vercel refreshes

## Project Structure

```text
Hotel-Booking-System/
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/hotel/booking/
│       └── test/java/com/hotel/booking/
├── frontend/
│   ├── package.json
│   ├── vercel.json
│   └── src/
│       ├── components/
│       ├── pages/
│       ├── services/
│       └── styles/
└── README.md
```

## Local Setup

Backend:

```bash
cd backend
mvn spring-boot:run
```

The API runs at:

```text
http://localhost:8080/api
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

The frontend runs at:

```text
http://localhost:5173
```

The frontend uses this API base URL:

```js
const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";
```

For the live Vercel deployment, set:

```text
VITE_API_BASE_URL=https://hotel-booking-system-k8x2.onrender.com/api
```

## Deployment Notes

Vercel:
- Root directory: `frontend`
- Build command: `npm run build`
- Output directory: `dist`
- Environment variable: `VITE_API_BASE_URL`

Render:
- Docker build context: `backend`
- Dockerfile path: `backend/Dockerfile`
- The backend uses `server.port=${PORT:8080}` so Render can inject the correct port.

## API Examples

```text
GET    /api/health
GET    /api/rooms
POST   /api/rooms
PUT    /api/rooms/{id}
DELETE /api/rooms/{id}

GET    /api/customers
POST   /api/customers
PUT    /api/customers/{id}
DELETE /api/customers/{id}

GET    /api/reservations
POST   /api/reservations
PATCH  /api/reservations/{id}/cancel
```

## Known Limitations

- Data is currently stored in memory.
- Data may reset when the Render service restarts.
- There is no authentication yet.
- There is no persistent database yet.

## Future Improvements

- PostgreSQL database
- Authentication
- Admin and customer roles
- Better reservation calendar
- Payment status management
