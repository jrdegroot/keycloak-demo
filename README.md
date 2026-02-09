
# Keycloak Demo - OAuth 2.0 Resource Server

A Spring Boot application demonstrating OAuth 2.0 Resource Server implementation using Keycloak as the authorization server. This project showcases how to secure REST APIs with JWT token validation.

## Project Overview

This application acts as a **Resource Server** in an OAuth 2.0 authorization flow. It validates JWT tokens issued by a Keycloak authorization server and protects API endpoints.

### Key Components

- **Java 25** with Spring Boot 4.0.2
- **Spring Data JPA** for database operations
- **Spring Security OAuth 2.0 Resource Server** for token validation
- **Keycloak** as the authorization and authentication server
- **H2 Database** for data persistence
- **Lombok** for reducing boilerplate code
- **CORS Support** for frontend integration

## OAuth 2.0 Authorization Code Flow

This application participates in the OAuth 2.0 Authorization Code Flow as the **Resource Server**:


### OAuth 2.0 Flow Steps

1. **User initiates login**: The frontend redirects the user to Keycloak's login page
2. **Authorization Code issued**: After successful authentication, Keycloak issues an authorization code
3. **Code exchanged for token**: The backend exchanges the authorization code for an access token
4. **JWT Token received**: Keycloak returns a signed JWT access token
5. **API Request**: The frontend includes the JWT token in request headers (`Authorization: Bearer <token>`)
6. **Token Validation**: This Resource Server validates the JWT signature and claims against the Keycloak authorization server
7. **Resource Access**: Upon successful validation, the protected resource is returned

## Architecture

### Security Configuration

The application is configured as a Spring Security OAuth 2.0 Resource Server:

- **JWT Token Validation**: Tokens are validated using the public key from Keycloak's JWK Set endpoint
- **Issuer URI**: Points to `http://localhost:8180/realms/demo-realm`
- **Stateless Authentication**: Uses JWT tokens instead of sessions
- **CORS Enabled**: Allows requests from the frontend at `http://localhost:4200/`

### Protected Endpoints

All endpoints under `/api/**` require a valid JWT token in the request header:


### Configuration Files

#### `application.yaml`
Configures the OAuth 2.0 Resource Server settings:
- Spring application name
- JWT issuer URI (Keycloak realm)
- Logging levels for debugging
- Frontend URL for CORS configuration

#### `docker-compose.yaml`
Defines the Keycloak service:
- Keycloak server on port 8180
- Admin credentials: `admin` / `password`
- Automatic realm import from configuration file


## Getting Started

### Prerequisites

- Java 25
- Gradle 9.3.0
- Docker and Docker Compose

### Setup Instructions

1. **Start Keycloak**:
   ```bash
   docker-compose up -d
   ```
   Keycloak will be available at `http://localhost:8180`
   Admin console: `http://localhost:8180/admin` (admin/password) 

    
2. **Import Realm Configuration**:
   Import the `demo-realm.json` file from the `keycloak` directory into Keycloak.

3. **Build the Application**:
   ```bash
   ./gradlew build
   ```
   
4. **Run the Application**:
   ```bash
   ./gradlew bootRun
   ```
   The application will start on `http://localhost:8080`

### Testing the OAuth Flow

1. **Obtain a Token**:
    - Use the Keycloak token endpoint to get a JWT token
    - Or use a tool like Postman to authenticate through Keycloak

2. **Access Protected Endpoints**:
   ```bash
   curl -H "Authorization: Bearer <JWT_TOKEN>" \
        http://localhost:8080/api/read
   ```

3. **Without Token** (Should fail with 401 Unauthorized):
   ```bash
   curl http://localhost:8080/api/read
   ```

## Key Components Explained

### SecurityConfig
- Configures JWT-based resource server authentication
- Enables CORS for frontend communication
- Disables CSRF (stateless REST API)
- Requires authentication for `/api/**` endpoints

### WebMvcCorsConfig
- Configures CORS policies to allow requests from the frontend
- Allows credentials and specific HTTP methods

### ReadController
- Simple REST controller demonstrating protected endpoints
- All `/api/**` endpoints require valid JWT tokens

## Debugging

The application includes debug logging for:
- Security filters and authentication
- Controller requests
- Controller requests
- CORS configuration
- HTTP interactions

Enable detailed logging by adjusting `logging.level` in `application.yaml`.

## License

This project is provided as-is for educational and demonstration purposes.
