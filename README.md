## <img src="logo3.png" alt="Logo" width="50" /> BeautyBook – A platform for connecting beauty service providers with customers, featuring a booking system for easy appointment scheduling

You can check out the live version of the project <img src="logo3.png" alt="Logo" width="20" /> [BeautyBook](https://beauty-book-3-0.vercel.app/) <img src="logo3.png" alt="Logo" width="20" />

BeautyBook is a platform designed to simplify the booking process for beauty services. Customers can easily find and book beauty professionals based on location, services offered, and availability 
This is the backend module for the BeautyBook project. 

## 🌐 Frontend

The frontend of the BeautyBook project is developed by [Dmitrijs Loginovs](https://github.com/Loginofff)

You can check out the frontend code here:
[BeautyBook Frontend](https://github.com/Loginofff/Beauty-book-3.0.git)

📺 **Application Demo**


![Demo](Untitledvideo-MadewithClipchamp2-ezgif.com-crop.gif)


### 🔧 **Tech Stack**

- **Language**: ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white) 
- **Framework**: ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white) 
- **Database**: ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=flat&logo=postgresql&logoColor=white)
- **Authentication**: ![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=json-web-tokens&logoColor=white) 
- **API Documentation**: ![Swagger UI](https://img.shields.io/badge/Swagger_UI-85EA2D?style=flat&logo=swagger&logoColor=black) 
- **File Storage**: ![Cloudinary](https://img.shields.io/badge/Cloudinary-FF6600?style=flat&logo=cloudinary&logoColor=white)
- **Email Service**: ![Java Mail Sender](https://img.shields.io/badge/Java_Mail_Sender-26A153?style=flat&logo=java&logoColor=white)  
- **Testing**: ![JUnit 5](https://img.shields.io/badge/JUnit_5-25A162?style=flat&logo=junit5&logoColor=white), ![Spring Security Test](https://img.shields.io/badge/Spring_Security_Test-6DB33F?style=flat&logo=spring-boot&logoColor=white)

  
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



