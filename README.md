## <img src="logo3.png" alt="Logo" width="50" /> BeautyBook – A platform for connecting beauty service providers with customers 

You can check out the live version of the project <img src="logo3.png" alt="Logo" width="20" /> [BeautyBook](https://beauty-book-3-0.vercel.app/) <img src="logo3.png" alt="Logo" width="20" />

BeautyBook is a platform designed to simplify the booking process for beauty services. Customers can easily find and book beauty professionals based on location, services offered, and availability 

📺 **Application Demo**


![Demo](Untitledvideo-MadewithClipchamp2-ezgif.com-crop.gif)


### 🔧 **Tech Stack**
- **Language**: Java 17+
- **Framework**: [Spring Boot](https://spring.io/projects/spring-boot)
- **Database**: [PostgreSQL](https://www.postgresql.org/)
- **Authentication**: [JWT](https://jwt.io/)
- **API Documentation**: [OpenAPI (Swagger UI)](http://localhost:8080/swagger-ui/index.html)
- **File Storage**: [Cloudinary](https://cloudinary.com/)
- **Testing**: [JUnit 5](https://junit.org/junit5/), [Spring Security Test](https://docs.spring.io/spring-security/reference/test.html)
  
### 📌 **API Features**

#### Booking Management
- ✅ Create, Update, Cancel bookings
- ✅ Find bookings by user ID and status

#### Category Management
- ✅ Create, update, and delete categories (ADMIN only)

#### Procedure Management
- ✅ Get all procedures, create, update, delete procedures (ADMIN only)

#### User Management
- ✅ Register, update, and delete users (with roles)

🏗 Planned Features

🔄 Implement a notification system for clients and masters : Notify users about bookings, cancellations, and updates via push notifications or in-app alerts

💳 Integrate payment gateways for seamless transactions : Enable secure online payments for services booked through the platform

📅 Enhance scheduling management for professionals : Improve the scheduling interface for better time management and conflict resolution

📩 Add SMS notifications for bookings : Send SMS reminders to clients and professionals about upcoming appointments

📊 Provide analytics and reports for professionals and admins : Generate detailed reports on bookings, revenue, and customer feedback

💬 Integrate an AI Chatbot : Develop an AI-powered chatbot to assist users with inquiries, booking confirmations, and providing general support

 
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

[Swagger](http://localhost:8080/swagger-ui/index.html)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

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
 



