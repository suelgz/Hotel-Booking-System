# Hotel Booking Management System

A hotel booking management system built as a portfolio project to demonstrate Java OOP, React frontend development, and full-stack project structure.

This project originally started as a Java-based hotel reservation system and is currently being converted into a full-stack web application with a React frontend and a Java Spring Boot backend.

## Project Status

The project is currently in progress.

- Java OOP core structure exists
- React frontend is created
- Frontend currently uses mock/local data
- Spring Boot backend structure is being prepared
- REST API integration will be added step by step

## Features

### Current Features

- Room management structure
- Customer management structure
- Reservation management logic
- Payment type structure using polymorphism
- Custom exception handling
- React dashboard layout
- Rooms, reservations, and customers pages
- Clean full-stack folder structure

### Planned Features

- Connect React frontend to Java Spring Boot REST API
- Add database support with PostgreSQL or SQLite
- Implement real CRUD operations
- Add room availability checking
- Add reservation cancellation and payment updates
- Improve validation and error handling
- Add screenshots and deployment link

## Technologies Used

### Frontend

- React
- JavaScript
- CSS
- Vite
- React Router

### Backend

- Java
- Spring Boot
- Maven
- Object-Oriented Programming


## Project Structure

```text
Hotel-Booking-System/
│
├── backend/
│   ├── pom.xml
│   └── src/
│       └── main/
│           └── java/
│               └── com/
│                   └── hotel/
│                       └── booking/
│                           ├── HotelBookingApplication.java
│                           └── model/
│                               ├── Room.java
│                               ├── Customer.java
│                               ├── Reservation.java
│                               ├── Payment.java
│                               └── ...
│
├── frontend/
│   ├── package.json
│   ├── index.html
│   └── src/
│       ├── components/
│       ├── pages/
│       ├── services/
│       ├── data/
│       └── styles/
│
├── README.md
├── LICENSE
└── .gitignore
```

## Frontend Setup

To run the frontend locally:

```bash
cd frontend
npm install
npm run dev
```

Then open the local development URL shown in the terminal:

```text
http://localhost:5173
```

## Backend Setup

The backend is being migrated to Spring Boot.

To run the backend after the Spring Boot setup is completed:

```bash
cd backend
mvn spring-boot:run
```

Expected backend URL:

```text
http://localhost:8080
```

Planned API examples:

```text
GET /api/rooms
POST /api/rooms
GET /api/customers
POST /api/customers
GET /api/reservations
POST /api/reservations
```
