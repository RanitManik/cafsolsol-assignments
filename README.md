# CafSolSol Assignments Solution

A Spring Boot application implementing two core functionalities: configuration file parsing and time-based pricing management with TSV file support.

## Features

### Assignment 1: Configuration Parser
- Parse custom configuration files with section-based structure
- In-memory storage for efficient retrieval
- REST API to fetch configuration by section name
- Support for single-value and array properties

### Assignment 2: Pricing Service
- Upload and parse TSV files containing SKU pricing data
- Time-based pricing with support for overlapping time ranges
- Fast retrieval mechanism using in-memory data structures
- Query prices by SKU ID with optional time parameter

## Tech Stack

- **Java 17+**
- **Spring Boot 4.0.1**
- **Gradle** (build tool)
- **JUnit 5** (testing framework)

## Project Structure

```
src/
├── main/
│   ├── java/com/ranitmanik/cafsolsol/
│   │   ├── Application.java
│   │   ├── controller/
│   │   │   ├── ConfigController.java
│   │   │   └── PricingController.java
│   │   ├── service/
│   │   │   ├── ConfigParser.java
│   │   │   └── PricingService.java
│   │   └── model/
│   │       ├── ConfigSection.java
│   │       └── PricingRecord.java
│   └── resources/
│       ├── config.txt
│       └── pricing_data.tsv
└── test/
    └── java/com/ranitmanik/cafsolsol/
        ├── controller/
        ├── service/
        └── ApplicationTests.java
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Gradle (or use included wrapper)

### Build and Run

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The application starts on `http://localhost:8080`

### Run Tests

```bash
./gradlew test
```

### Format Project

```bash
./gradlew spotlessApply
```

## API Endpoints

### Configuration API

#### Load Configuration
```http
POST /api/config/load
```
Loads the default `config.txt` file from resources.

**Response:**
```
Configuration loaded successfully
```

#### Get Section by Name
```http
GET /api/config/section/{sectionName}
```

**Example:**
```bash
curl http://localhost:8080/api/config/section/Order%20Service
```

**Response:**
```json
{
  "broker": "https://orbroker.in",
  "topic": ["test_os_topic_1", "test_os_topic_2"]
}
```

#### Get All Sections
```http
GET /api/config/sections
```

**Response:**
```json
{
  "Gateway": {
    "endpoint": "https://xyz.in",
    "certurl": "https://cloud.internalportal.com",
    "download loc": "/home/user/temp"
  },
  "CXO": {
    "endpont": "http://internal.cxo.com",
    "broker": "http://cxobroker.in",
    "topic": ["test_cxo_topic", "test_cxo_topic_1"]
  },
  "Order Service": {
    "broker": "https://orbroker.in",
    "topic": ["test_os_topic_1", "test_os_topic_2"]
  }
}
```

### Pricing API

#### Load Default Pricing Data
```http
POST /api/pricing/load-default
```
Loads the default `pricing_data.tsv` file from resources.

**Response:**
```
Default pricing data loaded successfully
```

#### Upload TSV File
```http
POST /api/pricing/upload
Content-Type: multipart/form-data
```

**Parameters:**
- `file`: TSV file with columns: SkuID, StartTime, EndTime, Price

#### Get Price
```http
GET /api/pricing/price?skuId={skuId}&time={time}
```

**Parameters:**
- `skuId` (required): The SKU identifier
- `time` (optional): Time in HH:mm format (e.g., 10:05)

**Examples:**

1. **Without time** (returns first available price):
```bash
curl "http://localhost:8080/api/pricing/price?skuId=u00006541"
```
Response:
```json
{
  "skuId": "u00006541",
  "price": 101
}
```

2. **With time before any range**:
```bash
curl "http://localhost:8080/api/pricing/price?skuId=u00006541&time=09:55"
```
Response:
```json
{
  "skuId": "u00006541",
  "time": "09:55",
  "price": "NOT SET"
}
```

3. **With time in first range**:
```bash
curl "http://localhost:8080/api/pricing/price?skuId=u00006541&time=10:03"
```
Response:
```json
{
  "skuId": "u00006541",
  "time": "10:03",
  "price": 101
}
```

4. **With time in overlapping range** (returns last matching):
```bash
curl "http://localhost:8080/api/pricing/price?skuId=u00006541&time=10:05"
```
Response:
```json
{
  "skuId": "u00006541",
  "time": "10:05",
  "price": 99
}
```

## Configuration File Format

```
SectionName
key = value
another key = another value
array key = value1, value2, value3

Another Section
key = value
```

**Features:**
- Section headers have no `=` sign
- Properties with commas are parsed as arrays
- Empty values are allowed
- Spaces around `=` are trimmed

## TSV File Format

```
SkuID	StartTime	EndTime	Price
u00006541	10:00	10:15	101
u00006541	10:05	10:10	99
```

**Rules:**
- Tab-separated values
- StartTime and EndTime in HH:mm format
- Time ranges: inclusive start, exclusive end `[start, end)`
- Multiple ranges allowed per SKU
- Overlapping ranges: last matching range wins

## Test Coverage

Comprehensive unit tests following TDD approach:

- **ConfigParserTest**: Tests for configuration parsing logic
- **ConfigControllerTest**: Tests for configuration API endpoints
- **PricingServiceTest**: Tests for pricing logic with various scenarios
- **PricingControllerTest**: Tests for pricing API endpoints

**Key test scenarios:**
- Valid and invalid section/SKU lookups
- Array property parsing
- Time range boundary conditions
- Overlapping time ranges
- Edge cases and error handling

## Author

**Ranit Manik**

---

*This project demonstrates TDD principles, clean architecture, and RESTful API design using Spring Boot.*
