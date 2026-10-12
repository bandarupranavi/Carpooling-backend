COLLEGE CARPOOLING BACKEND (Spring Boot + MySQL)

Requirements:
- JDK 17 or newer
- MySQL Server
- Internet connection on first Maven build (to download dependencies)

1. Create database (optional because createDatabaseIfNotExist=true):
   CREATE DATABASE carpooling_db;

2. Set database credentials in Windows Command Prompt for the current terminal:
   set DB_USERNAME=root
   set DB_PASSWORD=YOUR_MYSQL_PASSWORD
   (If MySQL has no password, leave DB_PASSWORD unset.)

   Or edit src/main/resources/application.properties:
   spring.datasource.username
   spring.datasource.password

3. Open a terminal in the folder containing pom.xml and run:
   .\mvnw.cmd clean spring-boot:run
   If Maven is installed, use: mvn clean spring-boot:run

4. Backend runs on http://localhost:8081
   GET http://localhost:8081/ should return status=running.

Endpoints:
POST /api/students/register
GET  /api/students
GET  /api/students/{id}

POST /api/rides
Example JSON:
{
  "driverId": 1,
  "origin": "College Gate",
  "destination": "Madhapur",
  "departureTime": "2027-01-15T09:30:00",
  "seatsAvailable": 3,
  "pricePerSeat": 25.00
}
GET /api/rides?origin=College&destination=Madhapur
GET /api/rides/{id}
GET /api/rides/driver/{driverId}

POST /api/bookings
Example JSON: {"rideId": 1, "studentId": 2, "seatsBooked": 1}
GET /api/bookings/student/{studentId}
GET /api/bookings/ride/{rideId}
DELETE /api/bookings/{bookingId}

Student registration JSON:
{
  "name": "A Student",
  "email": "student@example.com",
  "password": "ChangeMe123!",
  "college": "Example College",
  "phone": "9876543210"
}

Notes:
- Passwords are BCrypt-hashed and never included in the registration response.
- This is a college-project backend baseline, not a production-ready authentication system.
- Add authentication/authorization before allowing users to act as arbitrary student IDs.
- Ensure the frontend sends JSON with Content-Type: application/json.
- Departure times must be future local date-time values formatted yyyy-MM-ddTHH:mm:ss.
