## Breathe - Air Quality Monitoring Backen

The backend service for Breathe, a comprehensive air quality monitoring application. This Spring Boot application aggregates real-time AQI and weather data from Open-Meteo, manages spatial clustering for map visualizations using PostGIS, and handles crowdsourced pollution reports.

### Features

- **Real-time Aggregation:** Fetches and merges data from Open-Meteo (Weather & AQI) and BigDataCloud (Reverse Geocoding).

- **Spatial Clustering:** Utilizes PostGIS to efficiently cluster thousands of sensor points into grid-based markers for map performance.

- **Crowdsourced Reporting:** Allows users to report local pollution events (burning, industrial smoke) which are searchable via geospatial radius.

- **Historical & Forecast Data:** Provides 3-day forecasts and 60-day historical data analysis.

- **Unified Map API:** Returns a unified payload of AQI clusters and community reports for map rendering.

### Tech Stack

- **Language:** Java 17

- **Framework:** Spring Boot 3

- **Database:** PostgreSQL 15 + PostGIS Extension

- **Build Tool:** Maven

- **Containerization:** Docker & Docker Compose

- **External APIs:**

    - [Open-Meteo](https://open-meteo.com/) (Weather & Air Quality)

    - [BigDataCloud](https://www.bigdatacloud.com/) (Reverse Geocoding)

### System Architecture

The system follows a layered architecture separating the Client (API calls), Service (Business Logic), and Repository (Spatial Data) layers.
```
graph TD
User[Mobile App / Client] -->|REST API| Controller
Controller --> Service

    subgraph Backend
        Service -->|Geospatial Queries| PostGIS[(PostgreSQL + PostGIS)]
        Service -->|Fetch Weather/AQI| OpenMeteo[Open-Meteo API]
        Service -->|Reverse Geocoding| BDC[BigDataCloud API]
    end
```

### Getting Started

#### Prerequisites

- Java 17 SDK

- Docker & Docker Compose (Recommended)

- OR Maven and a local PostgreSQL instance

#### Run with Docker (Recommended)

The easiest way to run the application and the database is using Docker Compose.

1. **Clone the repository:**

``` bash
git clone https://github.com/vitiligo610/breathe-backend.git
cd breathe-backend
```

2. **Build the application:**

```
mvn clean package -DskipTests
```

3. **Start the services:**
```
docker-compose up -d
```

4. **Verify:** The API will be available at http://localhost:8080.

