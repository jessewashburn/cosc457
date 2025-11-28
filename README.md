# Baltimore Metal Crafters Database Management System

Java Swing application for managing restoration projects, customer relationships, employee records, and billing.

## Technology Stack

- **Java 21** | **MySQL 8.x** | **Maven 3.9+** | **Swing GUI**

## Quick Start

**Run (Pre-built):**
```bash
java -jar app/target/bmc-app-1.0.0.jar
```
*Uses default database configuration (jwashb2 credentials on triton.towson.edu:3360)*

**Build:**
```bash
cd app
mvn clean package
```

## Database Setup

```bash
mysql -h triton.towson.edu -P 3360 -u [username] -p [database] < db/schema.sql
mysql -h triton.towson.edu -P 3360 -u [username] -p [database] < db/data.sql
```

Configure connection in `app/src/main/resources/application.properties`