# AVZ 2.0 — Ants vs Zombies (Web Rewrite)

Full-stack remaster of a college Java game: **Angular** frontend, **Spring Boot** backend, **SQLite** persistence (wired; game save/load in Phase 2), REST + WebSocket (planned). Backend is authoritative for game rules; the client renders state and sends commands.

## Status

**Phase 1 complete** (foundation). See [specifications/TODO.md](specifications/TODO.md) for the 5-phase plan.

| Phase | Focus | Status |
|-------|--------|--------|
| 1 | Rules extraction, package layout, domain model | Done |
| 2 | Game engine, SQLite save/load, REST game APIs | Next |
| 3 | Angular board / HUD | Pending |
| 4 | STOMP WebSockets | Pending |
| 5 | Tests + Docker Compose MVP | Pending |

Temporary smoke endpoint: `GET /api/hello` (removed when game APIs land in Phase 2).

## Specs

| Doc | Purpose |
|-----|---------|
| [specifications/PRD.md](specifications/PRD.md) | Product requirements / MVP acceptance |
| [specifications/TECH.md](specifications/TECH.md) | Staged technical build (0–11) |
| [specifications/TODO.md](specifications/TODO.md) | 5-phase checklist (source of truth for progress) |
| [specifications/RULES.md](specifications/RULES.md) | Numbers extracted from original Java game |
| [specifications/MAPPING.md](specifications/MAPPING.md) | Old classes → new packages |

**Design note:** The original game is turn-based queue combat, not grid TD. Combat/entity stats come from source; board size, tick rate, movement, and player life are documented gaps (`TODO(needs-source-value)`).

## Prerequisites

- **Node.js 20+** and npm
- **Angular CLI 19** (`npm install -g @angular/cli@19`) — use 19 on Node 20; CLI 22+ needs Node 22+
- **Java 21** (Spring Boot 4 requires 17+)
- **Maven** (or use `backend/mvnw`)

If Java/Maven live under `~/jdks` and `~/tools`:

```bash
source ~/jdks/env.sh
```

```bash
java -version   # 21.x
./mvnw -v       # from backend/
ng version      # 19.x
```

## Project layout

```text
avz2.0/
  frontend/                 # Angular (:4200)
    src/app/
      game/board|entities|hud
      services/ models/ core/
  backend/                  # Spring Boot (:8080)
    src/main/java/com/game/
      controller/ service/ engine/ model/
      repository/ dto/ websocket/
  database/                 # SQLite file at runtime (avz.db)
  specifications/           # PRD, TECH, TODO, RULES, MAPPING
```

## Run locally (Phase 1 smoke)

```bash
# Terminal 1 — backend
source ~/jdks/env.sh   # if needed
cd backend && ./mvnw spring-boot:run

# Terminal 2 — frontend
cd frontend && npm start
```

- API: `curl http://localhost:8080/api/hello`
- UI: http://localhost:4200

### Backend tests

```bash
cd backend && ./mvnw test
```

## API (current)

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/api/hello` | Temporary connectivity check |

CORS allows `http://localhost:4200` in local dev.

## Docs for demos / interviews

- [DEMO.md](DEMO.md) — live walkthrough script
- [INTERVIEW_NOTES.md](INTERVIEW_NOTES.md) — talking points
