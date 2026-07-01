# AlertaMascota

Sistema web para reportar mascotas perdidas, buscarlas y conectar con cuidadores.

## Stack tecnológico

| Capa | Tecnología |
|------|------------|
| Backend | Spring Boot 3 + Java 17 |
| Frontend | React 18 + Vite |
| Base de datos | MongoDB 7 |
| Mapas | Leaflet.js (OpenStreetMap, sin API key) |
| Contenedores | Docker + Docker Compose |

---

## Cómo ejecutar

### Con Docker (recomendado)

```bash
# 1. Clonar / descomprimir el proyecto
cd alertamascota

# 2. Levantar todo
docker compose up --build

# 3. Abrir en el navegador
# Frontend:  http://localhost:5173
# Backend:   http://localhost:8080
# MongoDB:   localhost:27017
```

Para detener:
```bash
docker compose down
```

Para detener y borrar datos:
```bash
docker compose down -v
```

---

### Con VSCode (desarrollo local)

**Requisitos:** Java 17, Maven, Node 18+, MongoDB local

**Backend:**
```bash
cd backend
mvn spring-boot:run
# Corre en http://localhost:8080
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
# Corre en http://localhost:5173
```

**MongoDB local:** Asegúrate de que corra en `localhost:27017`.  
Cambia en `backend/src/main/resources/application.properties`:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/alertamascota
```

---

## Estructura del proyecto

```
alertamascota/
├── docker-compose.yml
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/alertamascota/
│       ├── AlertaMascotaApplication.java
│       ├── CorsConfig.java
│       ├── DataSeeder.java              ← datos de demo
│       ├── model/                       ← Alert, Caregiver, Sighting
│       ├── repository/                  ← MongoRepository
│       ├── controller/                  ← REST endpoints
│       ├── service/
│       │   ├── CaregiverService.java
│       │   ├── observer/                ← Patrón Observer
│       │   └── strategy/                ← Patrón Strategy
│       └── patterns/
│           ├── AlertValidationHandler.java  ← Chain of Responsibility
│           ├── builder/AlertBuilder.java    ← Builder
│           └── facade/AlertFacade.java      ← Facade
└── frontend/
    ├── Dockerfile
    ├── vite.config.js
    └── src/
        ├── App.jsx
        ├── pages/
        │   ├── AlertsPage.jsx
        │   ├── SearchPage.jsx
        │   └── CaregiversPage.jsx
        └── services/api.js
```

---

## API REST — Endpoints principales

### Alertas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/alertas` | Listar alertas activas |
| POST | `/api/alertas` | Crear nueva alerta (RF 1.1, 1.2) |
| PATCH | `/api/alertas/{id}/resolver` | Marcar como encontrado |
| GET | `/api/alertas/buscar?tipo=all` | Búsqueda (Strategy) |
| GET | `/api/alertas/buscar?tipo=species&species=Perro` | Filtrar por especie |
| GET | `/api/alertas/buscar?tipo=breed&breed=Labrador` | Filtrar por raza |
| POST | `/api/alertas/avistamientos` | Reportar avistamiento (RF 1.3) |

### Cuidadores
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/cuidadores` | Cuidadores verificados |
| GET | `/api/cuidadores/todos` | Todos (admin) |
| POST | `/api/cuidadores` | Registrar cuidador (RF 3.1) |
| PATCH | `/api/cuidadores/{id}/alertas?enabled=true` | Toggle alertas (RF 3.3) |
| PATCH | `/api/cuidadores/{id}/verificar` | Verificar identidad (RNF 3.1) |

---

## Patrones de diseño implementados

### Comportamiento

#### 1. Observer
**Archivo:** `service/observer/`  
**Uso:** Cuando se publica una alerta (RF 1.4), se notifica automáticamente a todos los cuidadores con `alertsEnabled=true` (RF 3.3). En producción enviaría email/push; en desarrollo loguea en consola.

```java
// AlertFacade.java
observers.forEach(o -> o.onAlertPublished(saved));
```

#### 2. Strategy
**Archivo:** `service/strategy/`  
**Uso:** El buscador cambia su algoritmo según el tipo seleccionado (RF 2.2). Los motores son intercambiables (RNF 2.1).

- `BySpeciesStrategy` → filtra por especie
- `ByBreedStrategy` → filtra por raza (contiene, ignora mayúsculas)
- `AllActiveStrategy` → devuelve todas las activas

```java
// SearchContext.java
return strategies.stream()
    .filter(s -> s.getType().equals(type))
    .findFirst()
    .get()
    .search(criteria);
```

#### 3. Chain of Responsibility
**Archivo:** `patterns/AlertValidationHandler.java`  
**Uso:** Antes de guardar una alerta, pasa por una cadena de validadores:
1. `RequiredFieldsHandler` — nombre y especie obligatorios (RF 1.1)
2. `LocationHandler` — coordenadas obligatorias (RF 1.2)
3. `OwnerHandler` — teléfono de contacto obligatorio

```java
AlertValidationHandler chain = AlertValidationHandler.buildChain();
chain.validate(alert); // lanza excepción si falla algún paso
```

### Creacionales

#### 4. Builder
**Archivo:** `patterns/builder/AlertBuilder.java`  
**Uso:** Construye objetos `Alert` paso a paso validando cada campo, evitando constructores con muchos parámetros.

```java
Alert alert = new AlertBuilder()
    .petName("Luna")
    .species("Perro")
    .location(-12.046, -77.043, "Miraflores")
    .owner("Ana", "987654321")
    .build();
```

### Estructurales

#### 5. Facade
**Archivo:** `patterns/facade/AlertFacade.java`  
**Uso:** El controller solo llama a `AlertFacade`, que coordina internamente Builder + Chain of Responsibility + Observer + Repository. Simplifica la interfaz del subsistema.

---

## Requisitos cubiertos (70%+)

| RF | Descripción | Estado |
|----|-------------|--------|
| RF 1.1 | Registrar mascota perdida con nombre, especie, raza, descripción | ✅ |
| RF 1.2 | Coordenadas geográficas en mapa (Leaflet) | ✅ |
| RF 1.3 | Avistamiento anónimo | ✅ |
| RF 1.4 | Notificación a cuidadores (Observer) | ✅ |
| RF 2.2 | Búsqueda por especie / raza (Strategy) | ✅ |
| RF 3.1 | Registro de cuidadores con 3 roles | ✅ |
| RF 3.3 | Toggle de alertas | ✅ |
| RNF 1.2 | Datos del dueño no expuestos (sanitize) | ✅ |
| RNF 2.1 | Motor de búsqueda intercambiable | ✅ |
| RNF 3.1 | Verificación de identidad antes de publicar | ✅ |
