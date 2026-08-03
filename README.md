# Hotel Operations Console

A full-stack hotel operations application with a Java 17 Spring Boot REST API, MySQL persistence, and a React/Vite frontend. The project keeps the original object-oriented hotel concepts while adding clearer booking rules, validation, database-backed storage, and a cleaner portfolio-ready structure.

## Technology Stack

Backend:
- Java 17
- Spring Boot 3.2
- Maven
- Spring Web
- Spring Validation
- Spring Data JPA / Hibernate
- MySQL Connector/J
- JUnit 5 / Mockito
- Docker

Frontend:
- React
- Vite
- JavaScript
- React Router
- CSS

Deployment:
- Vercel for the frontend
- Render or another Docker-capable host for the backend API
- MySQL for persistent backend storage

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
- MySQL-backed persistence for rooms, customers, reservations, and audit logs
- Consistent JSON error responses
- Simple health endpoint at `GET /api/health`
- SPA routing support for Vercel refreshes

## Project Structure

```text
Hotel-Operations-Console/
|-- backend/
|   |-- Dockerfile
|   |-- pom.xml
|   |-- mvnw
|   |-- mvnw.cmd
|   `-- src/
|       |-- main/java/com/hotel/booking/
|       |   |-- config/
|       |   |-- controller/
|       |   |-- dto/
|       |   |-- exception/
|       |   |-- model/
|       |   |-- repository/
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

Backend database:

```sql
CREATE DATABASE hotel_operations_console;
```

Backend on Windows PowerShell:

```powershell
cd backend
$env:JAVA_HOME="C:\Program Files\Java\jdk-20"
$env:MYSQL_PASSWORD="your_mysql_root_password"
.\mvnw.cmd spring-boot:run
```

You can also copy `backend/.env.example` to `backend/.env` for local development and fill in your own values:

```text
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=hotel_operations_console
MYSQL_USER=root
MYSQL_PASSWORD=your_mysql_password
CORS_ALLOWED_ORIGIN_PATTERNS=http://localhost:5173,http://localhost:3000,https://*.vercel.app
```

The `.env` file is ignored by Git and should not be committed.

Backend on macOS/Linux:

```bash
cd backend
export MYSQL_PASSWORD="your_mysql_root_password"
./mvnw spring-boot:run
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
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=hotel_operations_console
MYSQL_USER=root
MYSQL_PASSWORD=your_mysql_password
MYSQL_DATABASE_URL=jdbc:mysql://your-host:3306/hotel_operations_console
MYSQL_URL=mysql://user:password@your-host:3306/hotel_operations_console
DATABASE_URL=mysql://user:password@your-host:3306/hotel_operations_console
SPRING_DATASOURCE_URL=jdbc:mysql://your-host:3306/hotel_operations_console
CORS_ALLOWED_ORIGIN_PATTERNS=http://localhost:5173,http://localhost:3000,https://*.vercel.app
```

`PORT` is optional locally. For local MySQL, the defaults connect to:

```text
jdbc:mysql://localhost:3306/hotel_operations_console
```

with username:

```text
root
```

For Render or another hosted backend, do not use `localhost` for MySQL. Set either `MYSQL_DATABASE_URL`, `MYSQL_URL`, `DATABASE_URL`, or `SPRING_DATASOURCE_URL`, or set `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_DATABASE`, `MYSQL_USER`, and `MYSQL_PASSWORD` to your hosted MySQL database values.

Frontend:

```text
VITE_API_BASE_URL=http://localhost:8080/api
```

If `VITE_API_BASE_URL` is not set, the frontend uses `http://localhost:8080/api`.

## Troubleshooting API Connection Errors

If the frontend shows `Could not reach the hotel API`, the React app cannot reach the backend URL it was built with.

For local development:
- Start MySQL first.
- Confirm the `hotel_operations_console` database exists.
- Set `MYSQL_PASSWORD` before starting the backend.
- Start the backend with `cd backend && .\mvnw.cmd spring-boot:run`.
- Start the frontend with `cd frontend && npm run dev`.
- Keep `VITE_API_BASE_URL=http://localhost:8080/api` in `frontend/.env` if you create one.

For Vercel or another hosted frontend:
- Set `VITE_API_BASE_URL` to the deployed backend URL, for example `https://your-render-service.onrender.com/api`.
- Redeploy the frontend after changing this variable because Vite reads it at build time.
- Make sure the Render backend has hosted MySQL environment variables. A local PC database at `localhost:3306` is not reachable from Render.
- If the frontend uses a custom domain, add it to `CORS_ALLOWED_ORIGIN_PATTERNS` on the backend, separated by commas.
- Check the backend directly at `/api/health`; it should return JSON with `"status":"ok"`.

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

On Windows PowerShell, use `./mvnw.cmd` instead of `./mvnw`. Use JDK 17 or a compatible newer JDK such as JDK 20. Java 24 can compile the app, but this project's Mockito/Byte Buddy test stack does not support Java 24 cleanly.

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
- Set hosted MySQL env vars before deploying: `MYSQL_DATABASE_URL`, `MYSQL_URL`, `DATABASE_URL`, or `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_DATABASE`, `MYSQL_USER`, and `MYSQL_PASSWORD`

## Known Limitations

- There is no authentication or role-based access.
- Payments are represented by simple OOP classes but are not part of the active API workflow.
- The dashboard is operational and simple; it is not a reporting or analytics system.
- Hibernate uses `ddl-auto=update` for portfolio/demo convenience; production systems should use migrations such as Flyway or Liquibase.

## Future Improvements

- Add login for staff users.
- Add a clearer room calendar view.
- Add reservation editing after creation.
- Add basic payment status tracking if payment workflow becomes part of the app.
- Add Flyway or Liquibase database migrations for production-style schema control.
