# ArtMark — Auction System

A command-line (CLI) application for managing art auctions. Sellers list products and
organize auctions with lots, while clients place bids. The system keeps track of bids,
generates transactions when auctions are finalized, and sends notifications.

## Technologies

- **Java 21**
- **Maven** (build and dependencies)
- **MySQL** + **raw JDBC**
- Persistence via the `mysql-connector-j` connector

## Architecture

The application is organized in layers, wired together in `ConsoleApp`:

```
cli/         → console interface (menus, controllers, input reading)
service/     → business logic (UserService, ProductService, AuctionService)
repository/  → data access, one repository per entity
db/          → JDBC infrastructure (connection, generic repository, row mapping)
model/       → domain entities (with inheritance under model/user)
audit/       → audit service that writes to CSV
```

Flow of an action: `Controller` (cli) → `Service` (validation/logic) → `Repository` (SQL) →
`GenericRepository` (JDBC helper) → database.

### Key components

- **`DatabaseConnection`** — Singleton that reads the configuration from `db.properties`
  and provides the shared JDBC connection.
- **`GenericRepository<T>`** — abstract class with helper methods (`executeUpdate`,
  `executeQuery`, `executeQuerySingle`); every concrete repository extends it.
- **`RowMapper<T>`** — turns a `ResultSet` row into a domain object.
- **`AuditService`** — thread-safe Singleton that records actions in `audit.csv`.

### Entities

`Utilizator` (abstract) with subclasses `Client` and `Vanzator`, plus `Categorie`, `Produs`,
`Licitatie`, `Lot`, `Oferta`, `Tranzactie`, `Notificare`.

## Setup

1. Make sure you have a running MySQL database with an `artmark` schema created.
2. Copy the example configuration file and fill in your own values:

   ```bash
   cp src/main/resources/db.properties.example src/main/resources/db.properties
   ```

3. Edit `src/main/resources/db.properties`:

   ```properties
   db.url=jdbc:mysql://localhost:3306/artmark?serverTimezone=UTC
   db.username=YOUR_USERNAME
   db.password=YOUR_PASSWORD
   db.driver=com.mysql.cj.jdbc.Driver
   ```

   > The `db.properties` file contains credentials and **must not** be committed to git.

## Running

Compile and start the application with Maven:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.artmark.Main"
```

Or, after building, run the `com.artmark.Main` class directly from your IDE (IntelliJ IDEA).

On startup the menu appears:

```
=== ArtMark - sistem de licitatii ===

=== Meniu ===
1. Inregistrare
2. Autentificare
0. Iesire
```

## Features

**Public (not logged in)**
- Register an account (client or seller)
- Log in

**Seller**
- Add categories and products, view your own products
- Create auctions, add lots, activate and finalize auctions
- View your notifications and transactions
- Change password

**Client**
- View active auctions
- Place bids and view your own bids
- View your notifications and transactions
- Change password

All relevant actions are recorded in `audit.csv`.
