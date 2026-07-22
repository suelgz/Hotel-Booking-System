# Hotel Booking Management System

A university-style full-stack hotel booking application with a Java 17 Spring Boot REST API and a React/Vite frontend. The project keeps the original object-oriented hotel concepts while adding clearer booking rules, validation, and a cleaner portfolio-ready structure.

## Technology Stack

Backend:
- Java 17
- Spring Boot 3.2
- Maven
- Spring Web
- Spring Validation
- JUnit 5
- Docker
- In-memory data storage

Frontend:
- React
- Vite
- JavaScript
- React Router
- CSS

Deployment:
- Vercel for the frontend
- Render or another Docker-capable host for the backend API

## Current Features

- Dashboard with room counts, current occupancy, active reservations, estimated revenue, arrivals, departures, and recent activity
- Room listing, creation, update, and deletion
- Fixed backend-approved room pricing by room type
- Duplicate room number and room data validation
- Customer listing, creation, update, and deletion
- Reservation creation with check-in/check-out validation
- Date-based room availability checks
- Overlap detection that allows back-to-back reservations
- Reservation cancellation with room status refresh
- Consistent JSON error responses
- Simple health endpoint at `GET /api/health`
- SPA routing support for Vercel refreshes

## Project Structure

```text
Hotel-Booking-System/
|-- backend/
|   |-- Dockerfile
|   |-- pom.xml
|   |-- mvnw
|   |-- mvnw.cmd
|   `-- src/
|       |-- main/java/com/hotel/booking/
|       |   |-- controller/
|       |   |-- dto/
|       |   |-- exception/
|       |   |-- model/
|       |   `-- service/
|       `-- test/java/com/hotel/booking/
|-- frontend/
|   |-- package.json
|   |-- vercel.json
|   `-- src/
|       |-- components/
|       |-- pages/
|       |-- services/
|       `-- styles/
`-- README.md
```

## Local Setup

Backend:

```bash
cd backend
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The backend runs at:

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

## Environment Variables

Backend:

```text
PORT=8080
```

`PORT` is optional locally. Render can inject it when deployed.

Frontend:

```text
VITE_API_BASE_URL=http://localhost:8080/api
```

If `VITE_API_BASE_URL` is not set, the frontend uses `http://localhost:8080/api`.

## API Endpoint Summary

```text
GET    /api/health

GET    /api/dashboard/summary
GET    /api/audit-logs

GET    /api/rooms
POST   /api/rooms
PUT    /api/rooms/{id}
DELETE /api/rooms/{id}

GET    /api/customers
POST   /api/customers
PUT    /api/customers/{id}
DELETE /api/customers/{id}

GET    /api/availability?checkInDate=YYYY-MM-DD&checkOutDate=YYYY-MM-DD

GET    /api/reservations
POST   /api/reservations
PATCH  /api/reservations/{id}/cancel
```

Reservation creation body:

```json
{
  "customerName": "Emily Carter",
  "roomNumber": "101",
  "checkInDate": "2026-08-01",
  "checkOutDate": "2026-08-03"
}
```

Room creation body:

```json
{
  "roomNumber": "301",
  "type": "Double",
  "capacity": 3,
  "status": "Available"
}
```

Room prices are assigned by the backend from the room type:

```text
Single: 100
Double: 120
Suite: 300
```

## Testing Commands

Frontend:

```bash
cd frontend
npm run lint
npm run build
```

Backend:

```bash
cd backend
./mvnw test
./mvnw package
```

On Windows PowerShell, use `./mvnw.cmd` instead of `./mvnw`.

## Deployment Information

Frontend deployment on Vercel:
- Root directory: `frontend`
- Build command: `npm run build`
- Output directory: `dist`
- Required environment variable: `VITE_API_BASE_URL`

Backend deployment with Docker:
- Build context: `backend`
- Dockerfile: `backend/Dockerfile`
- The app reads `server.port=${PORT:8080}` from `application.properties`

## Known Limitations

- Data is stored in memory and resets when the backend restarts.
- There is no authentication or role-based access.
- There is no database persistence.
- Payments are represented by simple OOP classes but are not part of the active API workflow.
- The dashboard is operational and simple; it is not a reporting or analytics system.

## Future Improvements

- Add a small persistent database such as PostgreSQL or H2 for stored reservations.
- Add login for staff users.
- Add a clearer room calendar view.
- Add reservation editing after creation.
- Add basic payment status tracking if payment workflow becomes part of the app.
