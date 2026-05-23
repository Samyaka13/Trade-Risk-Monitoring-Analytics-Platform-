# Trade Risk Monitoring & Analytics Platform

Enterprise-grade backend system simulating investment banking trade risk management — inspired by real-world risk platforms at **Nomura**, **JPMorgan**, **Goldman Sachs**, and **Morgan Stanley**.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                      REST API Layer (Controllers)               │
│  TradeController │ RiskController │ AlertController │ Reports   │
├─────────────────────────────────────────────────────────────────┤
│                      Service Layer                              │
│  TradeService │ PositionService │ PnLService │ RiskAlertService │
├─────────────────────────────────────────────────────────────────┤
│                      Risk Engine                                │
│  Exposure Calc │ VaR Engine │ Breach Detection │ Market Data    │
├─────────────────────────────────────────────────────────────────┤
│                      Data Access (JPA Repositories)             │
│  Advanced SQL: Joins, Aggregations, Window Functions            │
├─────────────────────────────────────────────────────────────────┤
│                      PostgreSQL / H2                            │
└─────────────────────────────────────────────────────────────────┘
```

## Tech Stack

| Category       | Technology                                    |
|----------------|-----------------------------------------------|
| **Language**   | Java 21                                       |
| **Framework**  | Spring Boot 3.4, Spring Security, Spring Data JPA |
| **Database**   | PostgreSQL 16 (H2 for dev)                    |
| **ORM**        | Hibernate 6                                   |
| **Security**   | JWT (jjwt 0.12.6)                             |
| **Build**      | Maven (wrapper included)                      |
| **Mapping**    | MapStruct 1.6                                 |
| **Docs**       | Swagger / OpenAPI 3.0 (springdoc)             |
| **Container**  | Docker, Docker Compose                        |

## Core Modules

### 1. Trade Capture
Create, update, and close trades with full lifecycle management. Automatically triggers position recalculation and risk engine updates.

### 2. Position Management
Aggregates trades into net positions per trader+asset. Calculates weighted average price, market value, and unrealized PnL.

### 3. Risk Engine
- **Exposure**: `Σ(|Quantity × Current Price|)` across all open positions
- **Unrealized PnL**: `(Current Price - Entry Price) × Quantity`
- **Realized PnL**: `(Exit Price - Entry Price) × Quantity` for closed trades
- **VaR (Value at Risk)**: Parametric method — `Portfolio × σ × Z × √t`
- **Breach Detection**: Compares exposure against trader risk limits

### 4. Alert Management
Automated risk alerts with severity classification (LOW → CRITICAL). Generated when traders approach or exceed risk limits.

### 5. Reporting
- Top risky traders (by limit utilization)
- Desk-level exposure summary
- PnL ranking with daily statistics

## Quick Start

### Option 1: Local Development (H2 In-Memory DB)

```bash
# No database setup needed — uses H2 in-memory
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Option 2: Docker Compose (PostgreSQL)

```bash
docker-compose up -d
```

The API will be available at `http://localhost:8080/api/v1`

### Option 3: Local with PostgreSQL

```bash
# Set environment variables
export DATABASE_URL=jdbc:postgresql://localhost:5432/traderisk
export DATABASE_USERNAME=traderisk
export DATABASE_PASSWORD=traderisk_secret

./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## API Documentation

Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`

### Authentication

```bash
# Login to get JWT token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password123"}'

# Use token in subsequent requests
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/v1/trades
```

### Pre-seeded Users

| Username       | Password      | Role          |
|----------------|---------------|---------------|
| `admin`        | `password123` | ADMIN         |
| `trader_john`  | `password123` | TRADER        |
| `risk_manager` | `password123` | RISK_MANAGER  |

### Key Endpoints

| Method | Endpoint                          | Description                |
|--------|-----------------------------------|----------------------------|
| POST   | `/auth/login`                     | JWT authentication         |
| POST   | `/trades`                         | Book a new trade           |
| PUT    | `/trades/{id}`                    | Update a trade             |
| POST   | `/trades/{id}/close`              | Close a trade              |
| GET    | `/trades`                         | List trades (paginated)    |
| GET    | `/risk/exposure/{traderId}`       | Get exposure metrics       |
| GET    | `/risk/pnl/{traderId}`            | Get PnL breakdown          |
| GET    | `/risk/var/{traderId}`            | Get VaR estimate           |
| GET    | `/risk/breaches`                  | Get all risk breaches      |
| GET    | `/alerts`                         | Get active alerts          |
| GET    | `/alerts/critical`                | Get critical alerts        |
| GET    | `/reports/top-risky-traders`      | Top risky traders report   |
| GET    | `/reports/exposure-summary`       | Desk-level exposure report |
| GET    | `/reports/pnl-summary`            | PnL performance ranking    |

## Project Structure

```
src/main/java/com/riskmanagement/
├── TradeRiskPlatformApplication.java
├── config/          # OpenAPI, JPA Auditing configuration
├── controller/      # REST API controllers
├── dto/
│   ├── request/     # Validated request DTOs
│   └── response/    # Response DTOs with calculated fields
├── entity/          # JPA entities with finance-concept documentation
│   └── enums/       # Domain enums (Desk, TradeType, Severity, etc.)
├── exception/       # Custom exceptions + global handler
├── mapper/          # MapStruct entity-DTO mappers
├── reporting/       # Reporting module with SQL analytics
├── repository/      # Spring Data JPA repos with custom queries
├── riskengine/      # Risk engine: VaR, exposure, breach detection
├── security/        # JWT auth: token provider, filter, config
├── service/         # Business logic layer
│   └── impl/        # Service implementations
└── util/            # Financial calculation utilities
```

## Financial Concepts

This codebase includes inline documentation of key investment banking concepts:

- **Mark-to-Market (MTM)**: Valuing positions at current market prices
- **Long/Short Positions**: Direction of exposure (profit when price rises vs falls)
- **Value at Risk (VaR)**: Maximum expected loss at a given confidence level
- **Risk Limits**: Maximum authorized exposure per trader
- **Counterparty Risk**: Risk that the other party in a trade defaults
- **Settlement (T+2/T+1)**: When cash and securities are actually exchanged
- **Realized vs Unrealized PnL**: Locked-in profits vs paper gains

## Sample Trade Request

```json
{
  "traderId": "b0000001-0000-0000-0000-000000000001",
  "assetSymbol": "AAPL",
  "assetType": "EQUITY",
  "tradeType": "BUY",
  "instrumentType": "STOCK",
  "quantity": 100,
  "entryPrice": 178.50,
  "counterparty": "Goldman Sachs"
}
```
