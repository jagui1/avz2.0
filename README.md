# AVZ 2.0

Remaster of a college game, built as fullstack practice with an Angular frontend and Spring Boot backend.

## Prerequisites

- **Node.js 20+** and npm
- **Angular CLI 19** (`npm install -g @angular/cli@19`) — use 19 on Node 20; CLI 22+ needs Node 22+
- **Java 21** (Spring Boot 4 requires 17+)
- **Maven** (or use the included `backend/mvnw` wrapper)

If Java/Maven were installed under `~/jdks` and `~/tools` (when Homebrew isn’t available):

```bash
source ~/jdks/env.sh
```

Verify:

```bash
java -version   # 21.x
mvn -v          # or use ./mvnw from backend/
ng version      # 19.x
```

## Project layout

```text
avz2.0/
  frontend/   # Angular (ng serve → http://localhost:4200)
  backend/    # Spring Boot (port 8080)
```

## Run the backend

```bash
cd backend
./mvnw spring-boot:run
```

Smoke test:

```bash
curl http://localhost:8080/api/hello
# {"message":"Hello from AVZ"}
```

## Run the frontend

In a second terminal:

```bash
cd frontend
npm start
# or: ng serve
```

Open [http://localhost:4200](http://localhost:4200). You should see the message from Spring Boot.

## API

| Method | Path | Response |
|--------|------|----------|
| GET | `/api/hello` | `{ "message": "Hello from AVZ" }` |

CORS allows `http://localhost:4200` during local development.

