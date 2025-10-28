# Content Management System (CMS)

A simple Spring Boot-based Content Management System for creating, reading, updating, and deleting content. This is an internal application without user management.

## Features

- **Create** content with title and body
- **Read** all content or individual content by ID
- **Update** existing content
- **Delete** content
- **Search** content by title
- Automatic timestamps (created_at, updated_at)
- Input validation
- RESTful API
- In-memory H2 database
- Comprehensive test coverage
- Docker deployment ready

## Technology Stack

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (in-memory)
- Maven
- JUnit 5 & Mockito
- Docker & Docker Compose

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose (for containerized deployment)

## Getting Started

### Running Locally

1. Clone the repository:
```bash
git clone https://github.com/whitefallen/project-base.git
cd project-base
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Running with Docker Compose

1. Build and start the application:
```bash
docker-compose up --build
```

2. The application will be available at `http://localhost:8080`

3. To stop the application:
```bash
docker-compose down
```

## API Endpoints

### Base URL
```
http://localhost:8080/api/content
```

### Endpoints

#### Get All Content
```http
GET /api/content
```

#### Get Content by ID
```http
GET /api/content/{id}
```

#### Create Content
```http
POST /api/content
Content-Type: application/json

{
  "title": "My Content Title",
  "body": "This is the content body"
}
```

#### Update Content
```http
PUT /api/content/{id}
Content-Type: application/json

{
  "title": "Updated Title",
  "body": "Updated body content"
}
```

#### Delete Content
```http
DELETE /api/content/{id}
```

#### Search Content
```http
GET /api/content?search=keyword
```

## Example Usage

### Create Content
```bash
curl -X POST http://localhost:8080/api/content \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Getting Started with Spring Boot",
    "body": "Spring Boot makes it easy to create stand-alone applications..."
  }'
```

### Get All Content
```bash
curl http://localhost:8080/api/content
```

### Get Content by ID
```bash
curl http://localhost:8080/api/content/1
```

### Update Content
```bash
curl -X PUT http://localhost:8080/api/content/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Title",
    "body": "Updated content body"
  }'
```

### Delete Content
```bash
curl -X DELETE http://localhost:8080/api/content/1
```

### Search Content
```bash
curl "http://localhost:8080/api/content?search=spring"
```

## Response Format

### Success Response (GET single/POST/PUT)
```json
{
  "id": 1,
  "title": "Content Title",
  "body": "Content body text",
  "createdAt": "2025-10-28T13:12:00",
  "updatedAt": "2025-10-28T13:12:00"
}
```

### Error Response
```json
{
  "status": 404,
  "message": "Content not found with id: 1",
  "timestamp": "2025-10-28T13:12:00"
}
```

### Validation Error Response
```json
{
  "status": 400,
  "timestamp": "2025-10-28T13:12:00",
  "errors": {
    "title": "Title is required",
    "body": "Body is required"
  }
}
```

## Testing

Run all tests:
```bash
mvn test
```

Run tests with coverage:
```bash
mvn clean test
```

## H2 Console

The H2 database console is available for debugging at:
```
http://localhost:8080/h2-console
```

Connection details:
- JDBC URL: `jdbc:h2:mem:cmsdb`
- Username: `sa`
- Password: (empty)

## Project Structure

```
project-base/
├── src/
│   ├── main/
│   │   ├── java/com/whitefallen/cms/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── model/           # JPA entities
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── service/         # Business logic
│   │   │   └── CmsApplication.java
│   │   └── resources/
│   │       └── application.yml  # Application configuration
│   └── test/
│       └── java/com/whitefallen/cms/  # Test classes
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Development

### Building the JAR
```bash
mvn clean package
```

The JAR file will be created in the `target/` directory.

### Running the JAR
```bash
java -jar target/cms-1.0.0-SNAPSHOT.jar
```

## License

This project is for internal use only.