
🏦 Distributed Bank Management System

A robust Bank Transaction Management System built using Java Spring Boot and PostgreSQL, designed to handle secure financial transactions, account management, and user roles efficiently.

Phase 1 Completed : 
Transaction Api to add , delete , view , update transactions locally on a bank server. 

Phase 2 : authentication and authorization 

Phase 3 : event management and distributed system application

🚀 Features
🔐 Secure Authentication & Authorization (Admin/User roles)
👤 Account Creation & Management
💰 Deposit & Withdrawal Operations
🔄 Fund Transfer Between Accounts
📊 Transaction History & Statements
🧾 CRUD Operations for Transactions
⚠️ Global Exception Handling
🛡️ Input Validation & Data Integrity
📦 Database Versioning using Flyway
🛠️ Tech Stack
Backend
☕ Java 17+
🌱 Spring Boot
🗄️ Spring Data JPA (Hibernate)
🔐 Spring Security (JWT-based authentication)
🐘 PostgreSQL
🔄 Flyway (Database Migration)
Tools
🧪 Postman (API Testing)
🧰 Maven (Build Tool)
🗂️ Git & GitHub
📁 Project Structure
bank-transaction-system/
│── src/main/java/com/app/
│   ├── controller/       # REST Controllers
│   ├── service/          # Business Logic Layer
│   ├── repository/       # JPA Repositories
│   ├── model/            # Entity Classes
│   ├── dto/              # DTOs
│   ├── config/           # Security & App Config
│   ├── exception/        # Global Exception Handling
│
│── src/main/resources/
│   ├── application.properties
│   ├── db/migration/     # Flyway SQL Scripts
│
│── pom.xml
│── README.md
⚙️ Setup & Installation
1️⃣ Clone Repository
git clone https://github.com/your-username/bank-transaction-system.git
cd bank-transaction-system
2️⃣ Configure PostgreSQL Database

Make sure PostgreSQL is installed and running.

Create a database:

CREATE DATABASE bank_db;

Update application.properties:

spring.datasource.url=jdbc:postgresql://localhost:5432/bank_db
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
3️⃣ Build & Run Application
mvn clean install
mvn spring-boot:run

Application will run at:

http://localhost:8080
🔐 API Endpoints (Sample)
Authentication
POST /api/auth/register
POST /api/auth/login
Accounts
GET /api/accounts
POST /api/accounts
Transactions
POST /api/transactions/deposit
POST /api/transactions/withdraw
POST /api/transactions/transfer
GET /api/transactions
🧾 Example Transaction JSON
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 5000,
  "type": "TRANSFER"
}
🛡️ Security
JWT-based authentication
Role-based access control (ADMIN / USER)
Password encryption using BCrypt
🔄 Flyway Migration

Flyway automatically manages database schema.

Example:

V1__create_tables.sql
V2__add_transactions.sql
📌 Future Enhancements
📱 Frontend (React / Angular)
📊 Dashboard & Analytics
📧 Email Notifications
📥 Export Statements (PDF/Excel)
🏦 Integration with Payment Gateways
🤝 Contributing

Pull requests are welcome. For major changes, please open an issue first.

📄 License

This project is licensed under the MIT License.
