# 🎓 Bramhaa Coaching Classes - Student Management System

A full-stack web application for managing student records, fee receipts, and administrative tasks at Bramhaa Coaching Classes, Chakan, Pune.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?logo=html5&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6-yellow)

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Screenshots](#screenshots)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### 👥 Student Management
- **CRUD Operations** - Add, view, edit, and delete student records
- **Photo Upload** - Upload and display student photos (5MB max, automatic storage)
- **Advanced Search** - Filter by name, class, gender, and school
- **Class-wise View** - Organize students by class (1st-12th standard)
- **Detailed Student Profiles** - View complete student information with click-to-view modal

### 🧾 Fee Management
- **Receipt Generation** - Create professional fee receipts with auto-generated receipt numbers (BCC-YYYY-XXXX format)
- **PDF Download** - Download receipts as PDF matching physical receipt format
- **Payment History** - Track all fee payments per student
- **Flexible Fee Structure** - Support for admission fees, total fees, remaining fees, and partial payments

### 🔐 Authentication & Security
- **JWT Authentication** - Secure token-based authentication
- **Role-Based Access Control** - Admin-only access to sensitive operations
- **Password Encryption** - BCrypt password hashing
- **CORS Configuration** - Secure cross-origin resource sharing

### 📊 Dashboard & Analytics
- **Real-time Statistics** - Total students, gender distribution, school count
- **Recently Added** - Quick view of latest student enrollments
- **Pagination** - Efficient data display (8 students per page)
- **Responsive Design** - Professional navy & beige theme with Playfair Display + Outfit fonts

### 🛠️ Admin Features
- **Multi-Admin Support** - Create and manage multiple admin accounts
- **Cascade Delete** - Automatic cleanup of related fee receipts when deleting students
- **Bulk Operations** - Manage large datasets efficiently
- **Activity Logging** - Track admin actions (future enhancement)

## 🛠️ Tech Stack

### Backend
- **Java 17** - Core programming language
- **Spring Boot 3.x** - Application framework
  - Spring Security - Authentication & authorization
  - Spring Data JPA - Database ORM
  - Spring Web - RESTful API
- **MySQL 8.0** - Relational database
- **Hibernate** - ORM implementation
- **JWT (JSON Web Tokens)** - Secure authentication
- **BCrypt** - Password hashing

### Frontend
- **HTML5** - Structure
- **CSS3** - Styling (custom design system)
- **Vanilla JavaScript (ES6+)** - Client-side logic
- **jsPDF** - PDF generation
- **Google Fonts** - Typography (Playfair Display, Outfit)

### Tools & Infrastructure
- **Maven** - Dependency management
- **IntelliJ IDEA** - IDE
- **MySQL Workbench** - Database management
- **Git** - Version control
- **Postman** - API testing

## 📸 Screenshots

### Dashboard
![Dashboard](screenshots/dashboard.png)
*Real-time statistics and recently added students*

### Student Management
![Student List](screenshots/student-list.png)
*Complete student list with search and filters*

### Student Details
![Student Details](screenshots/student-details.png)
*Detailed student profile with photo*

### Fee Receipt
![Fee Receipt](screenshots/receipt.png)
*Professional fee receipt matching physical format*

## 🚀 Installation

### Prerequisites
- **Java 17** or higher
- **MySQL 8.0** or higher
- **Maven 3.6+** (or use IntelliJ's built-in Maven)
- **Git**

### Step 1: Clone Repository
```bash
git clone https://github.com/yourusername/bramhaa-coaching-classes.git
cd bramhaa-coaching-classes
```

### Step 2: Database Setup
1. **Create MySQL database:**
```sql
CREATE DATABASE studentmanagement;
```

2. **Update database credentials** in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/studentmanagement
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### Step 3: Build & Run Backend
```bash
# Using Maven
mvn clean install
mvn spring-boot:run

# Or using IntelliJ IDEA
# Right-click on StudentManagementApplication.java → Run
```

Backend will start on `http://localhost:8080`

### Step 4: Setup Frontend
1. Open `index.html` in your preferred location
2. Update API endpoint if needed (default: `http://localhost:8080/api`)
3. Open in browser or use a local server:
```bash
# Using Python
python -m http.server 3000

# Using Node.js
npx http-server -p 3000
```

### Step 5: Create First Admin
```bash
# Using Postman or cURL
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123",
  "role": "ADMIN"
}
```

**Then manually update the role in database:**
```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
```

## 📖 Usage

### Login
1. Open `index.html` in browser
2. Enter admin credentials
3. Access dashboard

### Add Student
1. Click **"+ Add Student"** button
2. Fill in student details
3. Upload photo (optional)
4. Click **"Save Student"**

### Generate Receipt
1. Click receipt icon (🧾) next to student name
2. Fill in fee details
3. Click **"Generate Receipt"**
4. Download PDF or view payment history

### View Student Details
1. Click on any student name (underlined in navy blue)
2. View complete profile with photo
3. Click **"Edit Student"** to modify

### Search & Filter
- **Search:** Type in search box (name, school, parent)
- **Filter:** Use dropdowns for class, gender, school
- Results update automatically

## 📡 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "string",
  "password": "string",
  "role": "USER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}

Response: {
  "token": "jwt-token",
  "username": "string",
  "role": "ADMIN"
}
```

#### Create Admin (Admin Only)
```http
POST /api/auth/admin/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "string",
  "password": "string",
  "role": "ADMIN"
}
```

### Student Endpoints

#### Get All Students
```http
GET /api/students
Authorization: Bearer {token}
```

#### Get Student by ID
```http
GET /api/students/{id}
Authorization: Bearer {token}
```

#### Create Student
```http
POST /api/students
Authorization: Bearer {token}
Content-Type: application/json

{
  "studentName": "string",
  "dateOfBirth": "2010-05-15",
  "className": "10th",
  "gender": "Male",
  "schoolName": "string",
  "parentName": "string",
  "parentEducation": "Graduate",
  "mobileNo": "9876543210",
  "aadharCard": "123456789012",
  "address": "string",
  "photoUrl": "/uploads/student-photos/uuid.jpg"
}
```

#### Update Student
```http
PUT /api/students/{id}
Authorization: Bearer {token}
Content-Type: application/json

{...student data...}
```

#### Delete Student (Admin Only)
```http
DELETE /api/students/{id}
Authorization: Bearer {token}
```

#### Search Students
```http
GET /api/students/search?name=John&class=10th&gender=Male&school=DPS
Authorization: Bearer {token}
```

### File Upload Endpoints

#### Upload Photo
```http
POST /api/files/upload-photo
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: <image-file>

Response: {
  "photoUrl": "/uploads/student-photos/uuid.jpg",
  "message": "Photo uploaded successfully"
}
```

### Fee Receipt Endpoints

#### Create Receipt
```http
POST /api/receipts
Authorization: Bearer {token}
Content-Type: application/json

{
  "studentId": "uuid",
  "admissionFees": 5000,
  "totalFees": 50000,
  "remainingFees": 30000,
  "feeAmountPaid": 20000,
  "paymentDate": "2026-04-18",
  "totalFeesDatedOn": "2026-04-01",
  "remarks": "Monthly fee for April 2026"
}
```

#### Get Student Payment History
```http
GET /api/receipts/student/{studentId}
Authorization: Bearer {token}
```

## 📁 Project Structure

```
bramhaa-coaching-classes/
├── src/
│   ├── main/
│   │   ├── java/com/brahmacoaching/studentmanagement/
│   │   │   ├── config/
│   │   │   │   ├── PasswordEncoderConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── FileUploadController.java
│   │   │   │   ├── FeeReceiptController.java
│   │   │   │   └── StudentController.java
│   │   │   ├── dto/
│   │   │   │   ├── StudentRequestDTO.java
│   │   │   │   └── StudentResponseDTO.java
│   │   │   ├── mapper/
│   │   │   │   └── StudentMapper.java
│   │   │   ├── model/
│   │   │   │   ├── FeeReceipt.java
│   │   │   │   ├── Student.java
│   │   │   │   └── User.java
│   │   │   ├── repository/
│   │   │   │   ├── FeeReceiptRepository.java
│   │   │   │   ├── StudentRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── security/
│   │   │   │   ├── filter/
│   │   │   │   │   └── JwtAuthFilter.java
│   │   │   │   └── util/
│   │   │   │       └── JwtUtil.java
│   │   │   ├── service/
│   │   │   │   ├── StudentService.java
│   │   │   │   ├── StudentServiceImpl.java
│   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   └── StudentManagementApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── uploads/
│   └── student-photos/
├── frontend/
│   └── index.html
├── screenshots/
├── pom.xml
├── README.md
└── .gitignore
```

## 🔑 Key Features Explained

### JWT Authentication Flow
1. User logs in with username/password
2. Backend validates credentials
3. JWT token generated and returned
4. Frontend stores token in localStorage
5. Token sent in Authorization header for all API calls
6. Backend validates token on each request

### Photo Upload System
1. User selects image file
2. Frontend validates (size < 5MB, type = image/*)
3. File uploaded to `/api/files/upload-photo`
4. Backend saves to `uploads/student-photos/` with UUID filename
5. Returns photo URL: `/uploads/student-photos/uuid.jpg`
6. URL stored in student record
7. WebConfig serves files from uploads directory

### Receipt Number Generation
- Format: `BCC-YYYY-XXXX`
- Example: `BCC-2026-0001`
- Auto-incremented per year
- Year resets counter to 0001

### Cascade Delete
When a student is deleted:
1. Service calls `feeReceiptRepository.deleteByStudentId(id)`
2. All receipts for that student are deleted
3. Then student record is deleted
4. All operations in single transaction (@Transactional)

## 🔒 Security Features

- **Password Hashing:** BCrypt with strength 10
- **JWT Tokens:** 24-hour expiration
- **CORS Protection:** Configured origins
- **SQL Injection Prevention:** JPA parameterized queries
- **XSS Protection:** Input sanitization
- **CSRF Disabled:** Stateless API (JWT-based)

## 🎨 Design System

### Color Palette
- **Primary Navy:** #1a2545
- **Accent Red:** #c0392b
- **Background Beige:** #f4f3ee
- **Success Green:** #1a6b3c
- **Text Dark:** #18180f

### Typography
- **Headings:** Playfair Display (serif)
- **Body:** Outfit (sans-serif)

### Components
- Rounded corners (8-16px)
- Soft shadows
- Smooth transitions (0.14s)
- Consistent spacing system

## 🚧 Future Enhancements

- [ ] SMS notifications for fee reminders
- [ ] Email integration for receipts
- [ ] Attendance tracking
- [ ] Performance analytics
- [ ] Bulk import/export (Excel)
- [ ] Mobile app (React Native)
- [ ] WhatsApp integration
- [ ] Automated backup system
- [ ] Multi-language support
- [ ] Dark mode

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

### Coding Standards
- Follow Java naming conventions
- Use meaningful variable names
- Add comments for complex logic
- Write unit tests for new features
- Update documentation

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Your Name**
- GitHub: [@PrayagKad](https://github.com/PrayagKad)
- LinkedIn: [Prayag KAd](https://linkedin.com/in/Prayag-kad)
- Email: prayagkad27@gmail.com

## 🙏 Acknowledgments

- Bramhaa Coaching Classes, Chakan, Pune
- Spring Boot documentation
- Font: Google Fonts (Playfair Display, Outfit)
- Icons: Unicode emojis
- PDF Generation: jsPDF library

## 📞 Support

For support, email 
prayagkad17@gmal.com or create an issue in this repository.

---

**Made with ❤️ for Bramhaa Coaching Classes**
