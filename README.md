## BeautyBook – A platform for connecting beauty service providers with customers

You can check out the live version of the project 🌐 [BeautyBook](https://beauty-book-3-0.vercel.app/) 🌐

BeautyBook is a platform designed to simplify the booking process for beauty services. Customers can easily find and book beauty professionals based on location, services offered, and availability 

📺 **Application Demo**


![Demo](Untitledvideo-MadewithClipchamp2-ezgif.com-crop.gif)


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

## Environment Variables Setup

The application uses environment variables for configuration. To set them up:

1. Create a `.env` file at the root of the project
2. Copy the contents from `.env.example` and fill in your values
3. The app will automatically load these variables at startup

Required environment variables:
- `ACCESS_KEY` - JWT access token secret key
- `REFRESH_KEY` - JWT refresh token secret key
- `CLOUDINARY_API_KEY` - Cloudinary API key
- `CLOUDINARY_API_SECRET` - Cloudinary API secret
- `CLOUDINARY_CLOUD_NAME` - Cloudinary cloud name
- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `SPRING_MAIL_USERNAME` - Email service username
- `SPRING_MAIL_PASSWORD` - Email service password
- `SPRING_REDIS_URL` - Redis connection URL

## Redis Caching

Redis is used to enhance API performance through caching, significantly reducing response times.

### Local Development

#### Windows
1. Download Redis for Windows from [GitHub](https://github.com/tporadowski/redis/releases)
2. Install and start the Redis server
3. Redis runs on port 6379 by default

#### Linux/Mac
```bash
# For Ubuntu/Debian
sudo apt update
sudo apt install redis-server
sudo systemctl start redis-server

# For Mac OS X via Homebrew
brew install redis
brew services start redis
```

### Production (Railway)
In production environment on Railway, the Redis instance is provided by the platform. The connection URL is automatically passed to the application through the `SPRING_REDIS_URL` environment variable.

### Configuration
Redis is configured automatically when the application starts with the following parameters:
- **Local development**: redis://localhost:6379
- **Production**: Redis on Railway using the URL from environment variable
- **Cache TTL**: 1 hour for general cache, with custom settings for different cache types:
  - Categories cache: 30 minutes
  - Procedures cache: 20 minutes

### Verifying Redis Status
```bash
redis-cli ping
```
If Redis is running, it will respond with "PONG".



