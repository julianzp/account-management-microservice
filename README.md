# Account Management Microservices

Este proyecto implementa un sistema de gestión de clientes, cuentas, movimientos y reportes usando Java, Spring Boot, PostgreSQL, RabbitMQ, Docker y Maven.

La solución está dividida en dos microservicios independientes:

* `client-service`: administra personas y clientes.
* `account-movement-service`: administra cuentas, movimientos y reportes de estado de cuenta.

Los microservicios se comunican de forma asíncrona mediante RabbitMQ. Cuando se crea o actualiza un cliente en `client-service`, se publica un evento de integración. El microservicio `account-movement-service` consume ese evento y guarda una copia mínima del cliente en su propia base de datos.

---

## Descripción de la arquitectura

```text
client-service
   |
   | publica eventos ClientCreated / ClientUpdated
   v
RabbitMQ
   |
   | consume eventos de cliente
   v
account-movement-service
   |
   | guarda una proyección local del cliente
   v
client_snapshots
```

Cada microservicio tiene su propia base de datos:

```text
client-service            -> client_db
account-movement-service  -> account_db
```

PostgreSQL se utiliza como motor de base de datos relacional y RabbitMQ como broker de mensajería asíncrona.

---

## Tecnologías utilizadas

* Java 21
* Spring Boot
* Maven
* Spring Web
* Spring Data JPA
* Hibernate
* PostgreSQL
* Flyway
* RabbitMQ
* Docker
* Docker Compose
* JUnit
* Testcontainers

---

## Estructura del proyecto

```text
account-management-microservice/
│
├── client-service/
│   ├── src/main/java/
│   ├── src/main/resources/db/migration/
│   ├── src/test/java/
│   ├── Dockerfile
│   └── pom.xml
│
├── account-movement-service/
│   ├── src/main/java/
│   ├── src/main/resources/db/migration/
│   ├── src/test/java/
│   ├── Dockerfile
│   └── pom.xml
│
├── docker/
│   └── postgres/
│       └── init/
│           └── 01-create-databases.sql
│
├── BaseDatos.sql
├── docker-compose.yml
├── .env.docker.example
├── .env.example
└── README.md
```

---

## Configuración de variables de entorno

La configuración de ejecución se maneja mediante variables de entorno.

Los archivos reales de entorno no deben subirse al repositorio.

El proyecto incluye archivos de ejemplo:

```text
.env.example
.env.docker.example
```

Para crear el archivo de entorno local para Docker:

```bash
cp .env.docker.example .env.docker
```

Ejemplo de `.env.docker` para entorno local:

```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

CLIENT_SERVICE_PORT=8081
ACCOUNT_SERVICE_PORT=8082

CLIENT_DB_URL=jdbc:postgresql://postgres:5432/client_db
CLIENT_DB_USERNAME=postgres
CLIENT_DB_PASSWORD=postgres

ACCOUNT_DB_URL=jdbc:postgresql://postgres:5432/account_db
ACCOUNT_DB_USERNAME=postgres
ACCOUNT_DB_PASSWORD=postgres

RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

CLIENT_EVENTS_EXCHANGE=client.exchange
CLIENT_CREATED_QUEUE=client.created.queue
CLIENT_UPDATED_QUEUE=client.updated.queue
CLIENT_CREATED_ROUTING_KEY=client.created
CLIENT_UPDATED_ROUTING_KEY=client.updated
```

---

## Levantar todo el proyecto con Docker

Desde la raíz del proyecto:

```bash
docker compose --env-file .env.docker up --build
```

Este comando levanta:

* PostgreSQL
* RabbitMQ
* `client-service`
* `account-movement-service`

El servicio `postgres-init` crea las bases de datos necesarias si no existen:

```text
client_db
account_db
```

Luego, Flyway crea las tablas correspondientes dentro de cada base de datos.

---

## Detener el proyecto

```bash
docker compose --env-file .env.docker down
```

Para detener todo y eliminar el volumen de PostgreSQL:

```bash
docker compose --env-file .env.docker down -v
```

Usar `down -v` únicamente cuando se quiera borrar la información local de la base de datos.

---

## Verificar contenedores activos

```bash
docker compose --env-file .env.docker ps
```

Servicios esperados:

```text
banking-postgres
banking-rabbitmq
client-service
account-movement-service
```

El contenedor `postgres-init` puede aparecer como finalizado con código `0`. Esto es normal, ya que solo se encarga de crear las bases de datos y luego termina.

---

## Health checks

Client service:

```bash
curl http://localhost:8081/actuator/health
```

Account movement service:

```bash
curl http://localhost:8082/actuator/health
```

Respuesta esperada:

```json
{
  "status": "UP"
}
```

Consola de administración de RabbitMQ:

```text
http://localhost:15672
```

Credenciales locales, según el archivo `.env.docker`:

```text
guest / guest
```

---

## Validación de bases de datos

Listar bases de datos:

```bash
docker exec -it banking-postgres psql -U postgres -d postgres -c "\l"
```

Ver tablas de `client_db`:

```bash
docker exec -it banking-postgres psql -U postgres -d client_db -c "\dt"
```

Tablas esperadas:

```text
clientes
flyway_schema_history
```

Ver tablas de `account_db`:

```bash
docker exec -it banking-postgres psql -U postgres -d account_db -c "\dt"
```

Tablas esperadas:

```text
client_snapshots
cuentas
movimientos
flyway_schema_history
```

---

## Endpoints principales

### Client Service

URL base:

```text
http://localhost:8081
```

Endpoints:

```http
POST   /api/clientes
GET    /api/clientes
GET    /api/clientes/{clienteId}
PUT    /api/clientes/{clienteId}
PATCH  /api/clientes/{clienteId}/estado
DELETE /api/clientes/{clienteId}
```

Ejemplo de creación de cliente:

```http
POST http://localhost:8081/api/clientes
```

```json
{
  "nombre": "Marianela Montalvo",
  "genero": "FEMENINO",
  "edad": 29,
  "identificacion": "1717171717",
  "direccion": "Amazonas y Naciones Unidas",
  "telefono": "097548965",
  "contrasena": "5678",
  "estado": true
}
```

Cuando un cliente se crea o se actualiza, este servicio publica un evento hacia RabbitMQ.

---

### Account Movement Service

URL base:

```text
http://localhost:8082
```

Endpoints de cuentas:

```http
POST   /api/cuentas
GET    /api/cuentas
GET    /api/cuentas/{numeroCuenta}
PUT    /api/cuentas/{numeroCuenta}
PATCH  /api/cuentas/{numeroCuenta}/estado
DELETE /api/cuentas/{numeroCuenta}
```

Ejemplo de creación de cuenta:

```http
POST http://localhost:8082/api/cuentas
```

```json
{
  "numeroCuenta": "225487",
  "tipoCuenta": "CORRIENTE",
  "saldoInicial": 100,
  "estado": true,
  "clienteId": 1
}
```

Endpoints de movimientos:

```http
POST /api/movimientos
GET  /api/movimientos
GET  /api/movimientos/{movimientoId}
```

Ejemplo de depósito:

```http
POST http://localhost:8082/api/movimientos
```

```json
{
  "numeroCuenta": "225487",
  "fecha": "2022-02-10",
  "valor": 600
}
```

Ejemplo de retiro:

```http
POST http://localhost:8082/api/movimientos
```

```json
{
  "numeroCuenta": "225487",
  "fecha": "2022-02-11",
  "valor": -575
}
```

Si el retiro supera el saldo disponible, la API responde:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Saldo no disponible"
}
```

---

## Reporte de estado de cuenta

Endpoint:

```http
GET /api/reportes?clienteId={clienteId}&fechaInicio={yyyy-MM-dd}&fechaFin={yyyy-MM-dd}
```

Ejemplo:

```http
GET http://localhost:8082/api/reportes?clienteId=1&fechaInicio=2022-02-01&fechaFin=2022-02-28
```

Ejemplo de respuesta:

```json
[
  {
    "fecha": "2022-02-10",
    "cliente": "Marianela Montalvo",
    "numeroCuenta": "225487",
    "tipo": "CORRIENTE",
    "saldoInicial": 100.00,
    "estado": true,
    "movimiento": 600.00,
    "saldoDisponible": 700.00
  }
]
```

---

## Comunicación asíncrona

El proyecto usa RabbitMQ para desacoplar el dominio de clientes del dominio de cuentas.

La configuración de mensajería se encuentra externalizada mediante variables de entorno:

```env
CLIENT_EVENTS_EXCHANGE=client.exchange
CLIENT_CREATED_QUEUE=client.created.queue
CLIENT_UPDATED_QUEUE=client.updated.queue
CLIENT_CREATED_ROUTING_KEY=client.created
CLIENT_UPDATED_ROUTING_KEY=client.updated
```

La capa de aplicación no depende directamente de RabbitMQ. La mensajería está implementada mediante puertos y adaptadores:

```text
Puerto de aplicación
   |
   v
Adaptador RabbitMQ
```

En `client-service`, la aplicación publica eventos de cliente usando una abstracción.

En `account-movement-service`, el listener de RabbitMQ delega a un caso de uso de aplicación que sincroniza el snapshot local del cliente.

---

## Migraciones de base de datos

Las migraciones de base de datos se manejan con Flyway.

Migraciones del microservicio de clientes:

```text
client-service/src/main/resources/db/migration/
```

Migraciones del microservicio de cuentas y movimientos:

```text
account-movement-service/src/main/resources/db/migration/
```

Migraciones actuales:

```text
client-service:
- V1__create_clientes_table.sql

account-movement-service:
- V1__create_client_snapshots_table.sql
- V2__create_cuentas_table.sql
- V3__create_movimientos_table.sql
```

El archivo `BaseDatos.sql` se incluye como script de referencia para la entrega.

La creación real del esquema en tiempo de ejecución se hace mediante Flyway.

---

## Pruebas unitarias

Las pruebas unitarias se encuentran dentro de la carpeta de tests de cada microservicio:

```text
client-service/src/test/java/
account-movement-service/src/test/java/
```

Ejecutar pruebas de `client-service`:

```bash
cd client-service
./mvnw test
```

Ejecutar pruebas de `account-movement-service`:

```bash
cd account-movement-service
./mvnw test
```

---

## Pruebas de integración

Las pruebas de integración se encuentran en:

```text
account-movement-service/src/test/java/com/julian/account_movement_service/integration/
```

La prueba principal de integración valida:

* Creación de cuentas.
* Creación de movimientos.
* Actualización del saldo disponible.
* Regla de negocio de saldo insuficiente.
* Integración real con PostgreSQL usando Testcontainers.
* Contexto real de Spring Boot.
* Ejecución de peticiones HTTP reales contra la aplicación.

Ejecutar la prueba de integración:

```bash
cd account-movement-service
./mvnw -Dtest=MovimientoIntegrationTest test
```

Docker Desktop debe estar ejecutándose, ya que la prueba de integración usa Testcontainers.

---

## Compilar servicios manualmente

Compilar `client-service`:

```bash
cd client-service
./mvnw clean package
```

Compilar `account-movement-service`:

```bash
cd account-movement-service
./mvnw clean package
```

---

## Ejecutar servicios localmente sin Docker Compose

Para ejecutar los servicios directamente con Maven, primero PostgreSQL y RabbitMQ deben estar corriendo.

Luego se deben cargar las variables de entorno locales:

```bash
set -a
source .env.local
set +a
```

Ejecutar `client-service`:

```bash
cd client-service
./mvnw spring-boot:run
```

En otra terminal, cargar nuevamente las variables de entorno y ejecutar `account-movement-service`:

```bash
set -a
source .env.local
set +a

cd account-movement-service
./mvnw spring-boot:run
```

---

## Reglas de negocio principales

### Registro de movimientos

Un movimiento puede ser positivo o negativo:

```text
valor positivo -> depósito
valor negativo -> retiro
```

El saldo disponible se actualiza cuando se registra el movimiento.

### Saldo insuficiente

Si un retiro deja el saldo de la cuenta por debajo de cero, la operación es rechazada con el mensaje:

```text
Saldo no disponible
```

En ese caso, el saldo de la cuenta no se modifica y el movimiento no se guarda.

### Inmutabilidad de movimientos

Los movimientos representan hechos financieros. Por ese motivo, no se actualizan ni se eliminan directamente.

Si un movimiento requiere corrección, el enfoque correcto es crear un movimiento compensatorio, no modificar la información histórica.

---

## Comandos útiles de Docker

Reconstruir y levantar todo:

```bash
docker compose --env-file .env.docker up --build
```

Levantar en segundo plano:

```bash
docker compose --env-file .env.docker up -d --build
```

Detener contenedores:

```bash
docker compose --env-file .env.docker down
```

Detener y borrar volúmenes:

```bash
docker compose --env-file .env.docker down -v
```

Ver logs:

```bash
docker compose --env-file .env.docker logs -f
```

Ver logs de un servicio específico:

```bash
docker compose --env-file .env.docker logs -f client-service
```

```bash
docker compose --env-file .env.docker logs -f account-movement-service
```

---

## Checklist de entrega

El proyecto incluye:

* CRUD de clientes.
* CRUD de cuentas.
* Registro de movimientos.
* Actualización de saldo disponible.
* Validación de saldo insuficiente.
* Reporte de estado de cuenta por cliente y rango de fechas.
* Comunicación asíncrona usando RabbitMQ.
* Migraciones de base de datos con Flyway.
* PostgreSQL desplegado con Docker.
* RabbitMQ desplegado con Docker.
* Dockerfile para ambos microservicios.
* Orquestación con Docker Compose.
* Ubicación de pruebas unitarias.
* Prueba de integración.
* Configuración externalizada.
* Archivos de ejemplo para variables de entorno.
* Script `BaseDatos.sql` de referencia.
