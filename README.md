# Examen 1 - Microservicios Integrados

APIs para el Examen 1: ExpressJS+MongoDB (Festivos) y Springboot+Postgres (Calendario Laboral).

## Requisitos previos

- **Node.js** 18+
- **MongoDB** en ejecución
- **PostgreSQL** en ejecución
- **Java 17** y **Gradle**

---

## 1. Poblar bases de datos

### MongoDB (Festivos)

Ejecutar el script **BDFestivos.mjs** sin modificar:

```bash
mongosh BDFestivos.mjs
```

O desde la carpeta del proyecto:

```bash
mongosh "c:\Users\chech\OneDrive\Desktop\MicroServicios\BDFestivos.mjs"
```

### PostgreSQL (Calendario Laboral)

Ejecutar en orden el archivo **DDL - Calendario Laboral.sql** sin modificar:

1. Ejecutar `DROP DATABASE` y `CREATE DATABASE`
2. Cambiar la conexión a la base `CalendarioLaboral` (PostgreSQL la crea como `calendariolaboral` en minúsculas)
3. Ejecutar el resto (tablas Tipo y Calendario)

Los tipos ("Día laboral", "Fin de Semana", "Día festivo") se crean automáticamente al poblar el calendario desde la API.

> **Nota:** Si usa otra contraseña de PostgreSQL, editar `api-calendario/src/main/resources/application.properties`

---

## 2. API Festivos (Express + MongoDB)

### Iniciar

```bash
cd api-festivos
npm install
npm start
```

Escucha en `http://localhost:3000`.

### Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/festivos/verificar/:anio/:mes/:dia` | Indica si una fecha es festiva |
| GET | `/api/festivos/listar/:anio` | Lista todos los festivos de un año |

### Ejemplos

- Verificar 12 de junio 2023: `GET /api/festivos/verificar/2023/6/12`
- Verificar 28 de febrero 2023: `GET /api/festivos/verificar/2023/2/28`
- Fecha inválida (35 feb): `GET /api/festivos/verificar/2023/2/35` → 400
- Listar festivos 2024: `GET /api/festivos/listar/2024`

---

## 3. API Calendario (Spring Boot + PostgreSQL)

### Configuración

Revisar `api-calendario/src/main/resources/application.properties`:

- `spring.datasource.url`: JDBC Postgres
- `spring.datasource.username` / `spring.datasource.password`
- `api.festivos.url`: URL de la API Festivos (por defecto `http://localhost:3000`)

### Iniciar

```bash
cd api-calendario
./gradlew bootRun
```

En Windows:

```bash
gradlew.bat bootRun
```

Escucha en `http://localhost:8080`.

### Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/calendario/festivos/{anio}` | Festivos del año (proxy a API Festivos) |
| POST | `/api/calendario/poblar/{anio}` | Pobla el calendario y retorna `{ "completado": true/false }` |
| GET | `/api/calendario/{anio}` | Calendario completo del año clasificado |

### Ejemplos

1. Poblar calendario 2024: `POST /api/calendario/poblar/2024`
2. Ver calendario 2024: `GET /api/calendario/2024`
3. Festivos 2024: `GET /api/calendario/festivos/2024`

---

## Orden de ejecución

1. Levantar MongoDB y PostgreSQL
2. Ejecutar `BDFestivos.mjs` en MongoDB
3. Ejecutar `DDL - Calendario Laboral.sql` en PostgreSQL
4. Iniciar API Festivos (`npm start` en api-festivos)
5. Iniciar API Calendario (`gradlew bootRun` en api-calendario)
6. Poblar el calendario: `POST /api/calendario/poblar/2024`
