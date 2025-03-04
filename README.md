## BeautyBook – A platform for connecting beauty service providers with customers

BeautyBook is a platform designed to simplify the booking process for beauty services. Customers can easily find and book beauty professionals based on location, services offered, and availability. 
📌  **Technologies used:**
- Backend: Java, Spring Boot, REST API
- Frontend: React, JavaScript, Next.js [repository](https://github.com/Loginofff/Beauty-book-3.0.git). 
- Database: PostgreSQL
- Deployment: Vercel, Railway

**Demo:**  
You can check out the live version of the project [here](https://beauty-book-3-0.vercel.app/).

📺 **Application Interface**
![Application Demo](https://drive.google.com/file/d/1z8wacAdJRirYDUMFukMb0qmqHzYXMI3E/view?usp=sharing) 

## 📌 API Features  
### 🔹 Booking Management  
✅ Create, update, and cancel bookings (authorized users)  
✅ Find bookings by user ID and status  
✅ Admin can update booking status  

### 🔹 Category & Procedure Management  
✅ Get all categories and procedures  
✅ Find procedures by category  
✅ Admin can create, update, and delete categories/procedures  

### 🔹 Review & Rating System  
✅ Clients can add reviews for professionals  
✅ Get reviews and ratings by master ID  
✅ Admin can delete reviews  

### 🔹 User Management  
✅ Register new users & authentication (JWT)  
✅ Get and update user details  
✅ Find professionals by category  
✅ Admin can manage users (delete, confirm masters)  

### 🔹 File Management  
✅ Upload/delete profile and portfolio photos  

profile
Qwen2.5-Plus
1:28 pm
Here’s a professional and comprehensive README for your project based on the provided code and details:

BeautyBook Backend
📌 Spring Boot | PostgreSQL | REST API | JWT | Docker

🚀 Project Description
BeautyBook is a backend platform that connects clients with beauty professionals. The project provides an extensive RESTful API for user registration, booking management, category handling, review systems, metadata management, and more

Live Demo : https://beautybook-api.example.com
Please refer to the documentation for available endpoints

📺 Application Interface
Add a screenshot or video demo of the application interface here:
Screenshot
or
Video Demo

🔧 Tech Stack
Language : Java 17
Framework : Spring Boot (3.2.1)
Database : PostgreSQL
Authentication : JWT (JSON Web Tokens)
API Documentation : OpenAPI (Swagger UI)
File Storage : Cloudinary
Data Validation : Bean Validation
Testing Frameworks : JUnit 5, Spring Security Test, Spring Boot Test
Other Libraries : Lombok, JavaFaker
Deployment: Railway

📌 API Features
Booking Management
✅ Create bookings (available to all authorized users)
✅ Update booking status (available to ADMIN)
✅ Cancel bookings (available to all authorized users)
✅ Find bookings by user ID and status (available to all authorized users)

Category Management
✅ Get all categories (available to all users)
✅ Get a specific category by ID (available to all users)
✅ Create, update, and delete categories (available to ADMIN)

Procedure Management
✅ Create, update, and delete procedures (available to ADMIN)
✅ Get all procedures (available to all users)
✅ Find procedures by category ID (available to all users)

Review System
✅ Get reviews by master ID (available to all users)
✅ Add reviews (available to authorized clients)
✅ Delete reviews (available to ADMIN)
✅ Get master ratings (available to all users)

User Management
✅ Get user details by ID (available to all users)
✅ Register new users (available to all users)
✅ Update or add user details (available to authorized masters)
✅ Find users by category ID (available to all users)
✅ Get all users (available to ADMIN)
✅ Get all masters (available to all users)
✅ Delete users by ID (available to ADMIN)
✅ Confirm masters by email (available to ADMIN)

Metadata Management
✅ Upload profile photos (available to authorized users)
✅ Upload portfolio photos (available to authorized users)
✅ Delete profile photos (available to authorized users)
✅ Delete specific portfolio photos (available to authorized users)

Messaging
✅ Send messages to admin (available to all users)

🏗 Planned Features
🔄 Implement a notification system for clients and masters : Notify users about bookings, cancellations, and updates via push notifications or in-app alerts
💳 Integrate payment gateways for seamless transactions : Enable secure online payments for services booked through the platform
📅 Enhance scheduling management for professionals : Improve the scheduling interface for better time management and conflict resolution
📩 Add SMS notifications for bookings : Send SMS reminders to clients and professionals about upcoming appointments
📊 Provide analytics and reports for professionals and admins : Generate detailed reports on bookings, revenue, and customer feedback
💬 Integrate an AI Chatbot : Develop an AI-powered chatbot to assist users with inquiries, booking confirmations, and providing general support

📑 Project Structure
📂 beautybook-backend
 ┣ 📂 src/main/java/com/beautybook
 ┃ ┣ 📂 controllers     # REST controllers
 ┃ ┣ 📂 services        # Business logic
 ┃ ┣ 📂 repositories    # Spring Data JPA repositories
 ┃ ┣ 📂 models          # Entities (User, Professional, Booking)
 ┃ ┗ 📂 config          # Configuration (Security, CORS, Swagger)
 ┣ 📜 Dockerfile        # Docker image configuration
 ┣ 📜 application.yml   # Spring Boot settings
 ┗ 📜 README.md         # Documentation
 
🔌 How to Run Locally?
1️⃣ Install Dependencies
Java 17+
PostgreSQL
Maven
2️⃣ Set Up the Database
Create a PostgreSQL database:
CREATE DATABASE beautybook;

3️⃣ Start the Backend
Clone the repository and run the application:
git clone https://github.com/username/BeautyBook-Backend.git
cd BeautyBook-Backend
mvn spring-boot:run

🛠 API Testing
Swagger UI is available at:
http://localhost:8080/swagger-ui/index.html

## 📑 Project Structure  
```
📂 beautybook
 ┣ 📂 src
 ┃ ┣ 📂 main
 ┃ ┃ ┣ 📂 java/com.example.end
 ┃ ┃ ┃ ┣ 📂 controller       # REST controllers
 ┃ ┃ ┃ ┣ 📂 dto             # Data Transfer Objects
 ┃ ┃ ┃ ┣ 📂 exceptions      # Custom exceptions
 ┃ ┃ ┃ ┣ 📂 infrastructure  # Infrastructure-related code
 ┃ ┃ ┃ ┣ 📂 mapping        # Object mapping logic
 ┃ ┃ ┃ ┣ 📂 models         # Entities (User, Professional, Booking)
 ┃ ┃ ┃ ┣ 📂 repository     # Spring Data JPA repositories
 ┃ ┃ ┃ ┣ 📂 service        # Business logic
 ┃ ┃ ┃ ┣ 📂 utils          # Utility classes
 ┃ ┃ ┃ ┗ 📂 validation     # Validation logic
 ┃ ┃ ┃ ┗ 📄 BeautyProjectApplication.java  # Main Spring Boot application
 ┃ ┣ 📂 resources          # Configuration files
 ┃ ┣ 📂 test
 ┃ ┃ ┣ 📂 java/com.example.end
 ┃ ┃ ┃ ┣ 📂 service
 ┃ ┃ ┃ ┃ ┗ 📄 UserServiceImplTest.java  # Unit tests for service layer
 ┃ ┃ ┃ ┗ 📂 util
 ┃ ┗ 📂 resources
 ┣ 📄 application.yml   # Spring Boot settings
 ┗ 📄 README.md         # Documentation



