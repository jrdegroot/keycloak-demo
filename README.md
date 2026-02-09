
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
6. **Token Validation & Role Extraction**: This Resource Server:
    - Validates the JWT signature using Keycloak's public key from the JWK Set endpoint
    - Calls the Keycloak UserInfo endpoint with the access token to fetch user roles
    - Extracts roles and converts them to Spring Security GrantedAuthorities
    - Adds roles to the SecurityContext for authorization checks
7. **Authorization Check**: Verifies the user has required roles for the requested endpoint
8. **Resource Access**: Upon successful validation and authorization, the protected resource is returned

## Architecture

### Security Configuration

The application is configured as a Spring Security OAuth 2.0 Resource Server with the following features:

- **JWT Token Validation**: Tokens are validated using the public key from Keycloak's JWK Set endpoint
- **Role-Based Access Control (RBAC)**: Uses JSR-250 `@RolesAllowed` annotations to enforce role-based endpoint access
- **Issuer URI**: Points to `http://localhost:8180/realms/demo-realm`
- **UserInfo Endpoint Integration**: Fetches user roles from the Keycloak UserInfo endpoint
- **Stateless Authentication**: Uses JWT tokens instead of sessions
- **CORS Enabled**: Allows requests from the frontend at `http://localhost:4200/`

### Role Extraction Flow

The application implements a custom JWT authentication token converter that:

1. **Validates JWT Token**: Spring Security validates the JWT signature against Keycloak's JWK Set
2. **Calls UserInfo Endpoint**: Uses the `KeycloakClient` to fetch user information and roles from Keycloak
3. **Extracts Roles**: Retrieves roles from the UserInfo response
4. **Converts to GrantedAuthorities**: Transforms role names into Spring Security authorities with `ROLE_` prefix
    - Example: `read` → `ROLE_READ`
    - Dashes are converted to underscores: `read-data` → `ROLE_READ_DATA`
5. **Populates SecurityContext**: Adds authorities to the JWT authentication token
6. **Enforces Access Control**: Spring Security checks `@RolesAllowed` annotations on endpoints

### Key Components

#### KeycloakClient
An HTTP client interface that communicates with the Keycloak server:
- **getUserInfo(accessToken)**: Calls the Keycloak UserInfo endpoint to fetch user information including roles
- Used by `JwtAuthenticationTokenConverter` to enrich the security context with role information
- Handles HTTP communication with the authorization server

#### KeycloakClientConfig
Configuration class that sets up the HTTP client for Keycloak communication:
- Configures the REST template or HTTP client with appropriate timeouts and headers
- Provides bean definitions for the `KeycloakClient` implementation

#### JwtAuthenticationTokenConverter
A custom Spring Security converter that implements `Converter<Jwt, AbstractAuthenticationToken>`:
- Extracts JWT claims and validates the token
- Fetches user information from Keycloak via `KeycloakClient`
- Merges JWT claims and user roles into Spring Security authorities
- Sets the user's full name (given_name + family_name) as the principal name
- Handles missing or unavailable user info gracefully with optional fallback to JWT subject claim
- Converts role names with `ROLE_` prefix formatting (dashes to underscores, uppercase)


#### SecurityConfig
Spring Security configuration that:
- Configures OAuth 2.0 resource server with JWT support
- Registers the custom `JwtAuthenticationTokenConverter` for role extraction
- Enables CORS with the configured `CorsConfigurationSource`
- Requires authentication for all `/api/**` endpoints
- Uses JSR-250 annotations (`@RolesAllowed`) for method-level security
- Disables CSRF since the application uses stateless REST endpoints

#### Protected Endpoints

All endpoints under `/api/**` require:
1. A valid JWT token in the request header
2. The appropriate role for the endpoint


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

### Testing the OAuth Flow and Role-Based Access

1. **Obtain a Token**:
    - Use the Keycloak token endpoint to get a JWT token
    - Example using curl:
      ```bash
      curl -X POST \
        http://localhost:8180/realms/demo-realm/protocol/openid-connect/token \
        -H 'Content-Type: application/x-www-form-urlencoded' \
        -d 'grant_type=password&client_id=keycloak-client&client_secret=your-client-secret&username=user&password=password'
      ```

2. **Access Protected Endpoint with Correct Role**:
   ```bash
   curl -H "Authorization: Bearer <JWT_TOKEN>" \
        http://localhost:8080/api/books
   ```
   Returns: 200 OK with book list (if user has ROLE_READ)

3. **Access Protected Endpoint without Token** (Should fail with 401 Unauthorized):
   ```bash
   curl http://localhost:8080/api/books
   ```

4. **Access Protected Endpoint without Required Role** (Should fail with 403 Forbidden):
   ```bash
   curl -H "Authorization: Bearer <JWT_TOKEN>" \
        http://localhost:8080/api/admin
   ```
   If the token doesn't have the required role


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
