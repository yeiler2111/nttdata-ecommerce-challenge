# Plataforma de Gestión de Pedidos - NTTDATA

API REST para gestionar usuarios, productos y pedidos de una tienda en línea, desarrollada con Java + Spring Boot y PostgreSQL.

## Tabla de Contenidos

- [Descripción del Proyecto](#descripción-del-proyecto)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Ejecución](#instalación-y-ejecución)
  - [Ejecutar con Docker (Recomendado)](#1-ejecutar-con-docker-recomendado)
  - [Ejecutar sin Docker](#2-ejecutar-sin-docker)
- [Arquitectura del Proyecto](#arquitectura-del-proyecto)
- [Modelo de Datos](#modelo-de-datos)
- [Endpoints de la API](#endpoints-de-la-api)
- [Seguridad y Autenticación](#seguridad-y-autenticación)
- [Usuarios de Prueba](#usuarios-de-prueba)
- [Documentación API (Swagger)](#documentación-api-swagger)
- [Testing](#testing)
- [Configuración Docker](#configuración-docker)

## Descripción del Proyecto

Este proyecto implementa una API REST completa para la gestión de una tienda en línea, evaluando las siguientes capacidades técnicas:

- Diseño de arquitectura en capas (Controller - Service - Repository)
- Implementación de APIs RESTful
- Persistencia con Spring Data JPA
- Seguridad con JWT y roles
- Validaciones y manejo de errores
- Tests automatizados
- Orquestación con Docker

## Tecnologías Utilizadas

- **Backend:** Java 17+, Spring Boot 3.x
- **Base de Datos:** PostgreSQL 16
- **Seguridad:** Spring Security, JWT
- **Documentación:** Swagger/OpenAPI (springdoc-openapi)
- **Testing:** JUnit 5, Mockito
- **Containerización:** Docker, Docker Compose
- **Build Tool:** Maven 3.9+

## Requisitos Previos

### Para ejecutar con Docker (Recomendado):
- Docker Desktop o Docker Engine
- Docker Compose

### Para ejecutar sin Docker:
- Java 17 o superior
- Maven 3.9+
- PostgreSQL 16 (o versión compatible)

## Instalación y Ejecución

### 1. Ejecutar con Docker (Recomendado)

El proyecto incluye un `docker-compose.yml` que levanta automáticamente la base de datos y la aplicación.

#### Levantar el entorno

```bash
# Clonar el repositorio (si aplica)
git clone <url-repositorio>
cd <nombre-proyecto>

# Construir y levantar los contenedores
docker-compose up --build
```

La API estará disponible en: **http://localhost:8083**

#### Detener los contenedores

```bash
docker-compose down
```

> **Nota:** El volumen `db-data` mantiene los datos de PostgreSQL incluso después de detener los contenedores.

#### Conectar a la base de datos

Puedes conectarte a PostgreSQL desde un gestor como DBeaver, DataGrip o TablePlus:

| Parámetro | Valor |
|-----------|-------|
| Host | `localhost` |
| Port | `5433` |
| Database | `ntt_tienda` |
| User | `ntt_user` |
| Password | `ntt_pass` |

### 2. Ejecutar sin Docker

#### Configurar PostgreSQL

```sql
-- Crear la base de datos y usuario
CREATE DATABASE ntt_tienda;
CREATE USER ntt_user WITH PASSWORD 'ntt_pass';
GRANT ALL PRIVILEGES ON DATABASE ntt_tienda TO ntt_user;
```

#### Configurar application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ntt_tienda
spring.datasource.username=ntt_user
spring.datasource.password=ntt_pass
spring.jpa.hibernate.ddl-auto=update
```

#### Ejecutar la aplicación

```bash
# Compilar y ejecutar
mvn clean spring-boot:run
```

La API estará disponible en: **http://localhost:8080**

## Arquitectura del Proyecto

El proyecto sigue una arquitectura en capas bien definida:

```
com.example.technicalTest
├── auth/                  # Autenticación y seguridad
│   ├── controller/        # Endpoints de login
│   ├── service/           # Lógica de autenticación
│   └── security/          # Configuración JWT
├── user/                  # Gestión de usuarios
│   ├── controller/        # REST endpoints
│   ├── service/           # Lógica de negocio
│   ├── dto/               # Data Transfer Objects
│   ├── entity/            # Entidades JPA
│   └── repository/        # Acceso a datos
├── product/               # Gestión de productos
│   ├── controller/
│   ├── service/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── util/
├── order/                 # Gestión de pedidos
│   ├── controller/
│   ├── service/
│   ├── dto/
│   ├── entity/
│   └── repository/
├── config/                # Configuraciones
│   ├── DataLoader.java    # Datos iniciales
│   ├── SecurityConfig.java
│   ├── OpenApiConfig.java
│   └── WebConfig.java
└── exception/             # Manejo de errores
    └── GlobalExceptionHandler.java
```

### Capas de la Arquitectura

| Capa | Responsabilidad |
|------|----------------|
| **Controller** | Maneja peticiones HTTP y mapea endpoints REST |
| **Service** | Contiene la lógica de negocio y validaciones |
| **Repository** | Interactúa con la base de datos (Spring Data JPA) |
| **Config** | Configuraciones de seguridad, datos iniciales, etc. |
| **Exception** | Manejo centralizado de errores |

## Modelo de Datos

### Usuario (User)
```java
- id: Long
- name: String
- email: String (único)
- password: String (hash)
- role: String (ROLE_USER, ROLE_ADMIN)
- orders: List<Order>
```

### Producto (Product)
```java
- id: Long
- name: String
- description: String
- price: BigDecimal
- stock: Integer
```

### Pedido (Order)
```java
- id: Long
- user: User
- orderDate: LocalDateTime
- total: BigDecimal
- items: List<OrderItem>
```

### Detalle de Pedido (OrderItem)
```java
- id: Long
- order: Order
- product: Product
- quantity: Integer
- unitPrice: BigDecimal
```

## Endpoints de la API

### Autenticación (Público)

#### Registro de Usuario
```http
POST /usuarios
Content-Type: application/json

{
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "password": "password123"
}
```
- **Acceso:** Público
- **Rol asignado:** Siempre `ROLE_USER`
- **Validaciones:** Email único, formato válido, contraseña mínima

#### Login
```http
POST /login
Content-Type: application/json

{
  "email": "user@ntt.com",
  "password": "user123"
}
```
**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 3600000
}
```

### Productos

#### Listar Productos
```http
GET /productos?page=0&size=10&name=laptop&minPrice=100&maxPrice=5000
Authorization: Bearer <token>
```
- **Acceso:** Usuario autenticado
- **Paginación:** Soporta `page`, `size`
- **Filtros:** `name`, `minPrice`, `maxPrice`

#### Crear Producto
```http
POST /productos
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Monitor 4K",
  "description": "Monitor profesional 27 pulgadas",
  "price": 1200.00,
  "stock": 30
}
```
- **Acceso:** Solo `ROLE_ADMIN`

#### Actualizar Producto
```http
PUT /productos/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Monitor 4K Pro",
  "price": 1350.00,
  "stock": 25
}
```
- **Acceso:** Solo `ROLE_ADMIN`

### Pedidos

#### Crear Pedido
```http
POST /pedidos
Authorization: Bearer <token>
Content-Type: application/json

{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```
- **Acceso:** Usuario autenticado
- **Validaciones:** Stock disponible, producto existente

#### Ver Detalle de Pedido
```http
GET /pedidos/{id}
Authorization: Bearer <token>
```
- **Acceso:** Solo el propietario del pedido

#### Mis Pedidos
```http
GET /pedidos/mis-pedidos
Authorization: Bearer <token>
```
- **Acceso:** Usuario autenticado
- **Retorna:** Todos los pedidos del usuario actual

## Seguridad y Autenticación

### JWT (JSON Web Token)

Todos los endpoints protegidos requieren un token JWT en la cabecera:

```http
Authorization: Bearer <tu-token-jwt>
```

### Configuración JWT

| Variable | Valor por Defecto |
|----------|-------------------|
| `APP_JWT_SECRET` | `UnaClaveMasLargaParaDocker_123456789` |
| `APP_JWT_EXPIRATION_MS` | `3600000` (1 hora) |

### Roles y Permisos

| Endpoint | ROLE_USER | ROLE_ADMIN |
|----------|-----------|------------|
| `POST /usuarios` | Público | Público |
| `POST /login` | Público | Público |
| `GET /productos` | Público | Público |
| `POST /productos` | Privado | Público |
| `PUT /productos/{id}` | Privado | Público |
| `POST /pedidos` | Público | Público |
| `GET /pedidos/*` | Público (propios) | Público |

## Usuarios de Prueba

Al iniciar la aplicación, el `DataLoader` crea automáticamente:

### Usuario Administrador
```
Email: admin@ntt.com
Password: admin123
Rol: ROLE_ADMIN
```
- **No se puede crear vía API pública**
- Solo existe a través del DataLoader

### Usuario Normal
```
Email: user@ntt.com
Password: user123
Rol: ROLE_USER
```

### Productos de Prueba

| Producto | Precio | Stock |
|----------|--------|-------|
| Laptop Lenovo | $3,500.00 | 15 |
| Mouse Logitech | $120.00 | 50 |
| Teclado Mecánico | $220,000.00 | 20 |

## Documentación API (Swagger)

Una vez levantada la aplicación, la documentación interactiva está disponible en:

- **Swagger UI:** http://localhost:8083/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8083/v3/api-docs

> **Nota:** Si ejecutas sin Docker, usa el puerto `8080` en lugar de `8083`

## Testing

### Ejecutar todos los tests

```bash
mvn test
```

### Tipos de Tests Implementados

- **Tests Unitarios de Servicios**
  - `AuthServiceImplTest`
  - `UserServiceTest`
  - `ProductServiceTest`
  - `OrderServiceTest`

- **Tests de Controladores**
  - `AuthControllerTest`
  - `UserControllerTest`
  - `ProductControllerTest`
  - `OrderControllerTest`

### Coverage

```bash
mvn clean test jacoco:report
```

## Configuración Docker

### docker-compose.yml

El proyecto usa Docker Compose v3.9 con dos servicios:

#### Servicio: `db` (PostgreSQL)

| Configuración | Valor |
|---------------|-------|
| Imagen | `postgres:16` |
| Contenedor | `ntt-postgres` |
| Database | `ntt_tienda` |
| User | `ntt_user` |
| Password | `ntt_pass` |
| Puerto interno | `5432` |
| Puerto host | `5433` |
| Volumen | `db-data:/var/lib/postgresql/data` |

#### Servicio: `app` (Spring Boot API)

| Configuración | Valor |
|---------------|-------|
| Contenedor | `ntt-api` |
| Puerto interno | `8080` |
| Puerto host | `8083` |
| Dependencias | `db` |
| Restart policy | `on-failure` |

### Variables de Entorno

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/ntt_tienda
SPRING_DATASOURCE_USERNAME=ntt_user
SPRING_DATASOURCE_PASSWORD=ntt_pass
SPRING_JPA_HIBERNATE_DDL_AUTO=update
APP_JWT_SECRET=UnaClaveMasLargaParaDocker_123456789
APP_JWT_EXPIRATION_MS=3600000
```

### Red Docker

Ambos servicios se comunican a través de la red `ntt-network`.

## Contribuciones

Este proyecto fue desarrollado como prueba técnica para NTTDATA.

## Licencia

Este proyecto es de uso educativo y evaluativo.

---

**Desarrollado usando Spring Boot y PostgreSQL**