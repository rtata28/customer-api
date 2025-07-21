# Customer Management API

A Spring Boot RESTful API for managing customers with CRUD operations, tier classification based on annual spending, email validation, and in-memory H2 database support.

---

## 🚀 Features

- ✅ Spring Boot + Maven Project
- ✅ CRUD Endpoints: Create, retrieve, update, and delete customers.
- 💾 In-memory H2 database config for local development and testing.
- ✅ UUID for Customer ID
- 🎯 Classify customers into tiers based on `annualSpend`.
- 📧 Email format validation.
- ✅ Tier Calculation (Silver/Gold/Plat)
- ✅ OpenAPI Spec (YAML)
- 📓 Unit Tests (Service + Controller)
- ✅ README with build/run instructions

## 🛠️ Build and Run

### 📋 Prerequisites

- Java 17+ (Java 24.0.1)
- Maven 3.8+
- Git

### 📦 Clone the repo

```bash
git clone git@github.com:rtata28/customer-api.git
cd customer-api
```

### 🔨 Build the application

```bash
mvn clean install
```

### ▶️ Run the application

```bash
mvn spring-boot:run
```

The application runs at:  
`http://localhost:8080`

---
Use Swagger EndPoint to view the Webservices
'http://localhost:8080/swagger-ui.html'
---

## 📬 API Endpoints

### ✅ Create a Customer

**POST** `/customers`

Request Body:
```json
{
  "name": "Alice",
  "email": "alice@example.com",
  "annualSpend": 15000,
  "lastPurchaseDate": "2024-10-01"
}
```

Response:
- Status: `201 Created`
- Body: Full customer object including generated `id` and calculated `tier`.

---

### 🔍 Get Customer by ID

**GET** `/customers/{id}`

Example:
```
/customers/fd13c180-882b-4d09-a60d-9d57c32b3a40
```

---

### 🔍 Get Customer by Name or Email

**GET** `/customers?name=Alice`  
**GET** `/customers?email=alice@example.com`

---

### ✏️ Update a Customer

**PUT** `/customers/{id}`

```json
{
  "name": "Alice Johnson",
  "email": "alice.j@example.com",
  "annualSpend": 18000,
  "lastPurchaseDate": "2024-12-15"
}
```

Response:
- Status: `200 OK`
- Updated customer details

---

### ❌ Delete a Customer

**DELETE** `/customers/{id}`

Response:
- Status: `204 No Content`

---

## 💎 Tier Classification Logic

| Annual Spend | Tier     |
|--------------|----------|
| < 5000       | Silver   |
| 5000–9999    | Gold     |
| ≥ 10000      | Platinum |

Tier is auto-computed on every create/update based on `annualSpend`.

---

## 🧪 Testing

Unit and controller tests cover:

- CRUD operations
- Tier calculation
- Email format validation

To run tests:

```bash
mvn test
```

---

## 💾 H2 Database Console

Access via:  
[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

**Settings:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: *(leave blank)*

---

## 📌 Assumptions

- `email` and `name` are treated as unique identifiers for lookup.
- No pagination or filtering logic for multi-customer listing (kept simple).
- No authentication or authorization added.
- All customer data is stored in-memory using H2 and reset on restart.

---

## 🧰 Tech Stack

- Spring Boot 3.x
- Spring Web
- Spring Validation
- Lombok
- JUnit 5 + Mockito
- H2 Database
- Maven

---

## 👩‍💻 Author

**Ramya Vaka**  
📧 ramya.tata@gmail.com  
🔗 GitHub: [rtata28](https://github.com/rtata28)

---