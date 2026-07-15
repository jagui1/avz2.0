# Product Requirements Document — Ants vs Zombies (Web Rewrite)

## 1. Summary
Rewrite an existing desktop Java tower-defense game ("ants attack zombies") as a full-stack web application: Angular frontend, Spring Boot backend, SQLite persistence, REST + WebSocket communication. Backend is authoritative for all game state and rules; frontend is a rendering/input client.

## 2. Goals
- Preserve existing game rules (combat, movement, waves, resources, win/loss) from the Java implementation.
- Move all game logic server-side; client never computes authoritative state.
- Support real-time state updates via WebSocket (STOMP).
- Persist game state in SQLite; games survive server restart.
- Deployable locally via Docker Compose.

## 3. Non-Goals
- No multiplayer/matchmaking.
- No cloud deployment (local only).
- No mobile client.
- Authentication is optional/stretch, not required for MVP.

## 4. Users
Single local player running the app via `docker compose up`, interacting through a browser at `localhost:4200`.

## 5. Core Entities
- **Game**: id, status (`RUNNING`/`WON`/`LOST`), wave, resources, board.
- **Board**: rows, columns, tiles.
- **Tile**: x, y, optional ant, optional zombie.
- **Ant**: health, damage, attackSpeed, type.
- **Zombie**: health, speed, position.

## 6. Functional Requirements

### 6.1 Game Lifecycle
- FR-1: User can create a new game (`POST /api/games`).
- FR-2: User can fetch current game state (`GET /api/games/{id}`).
- FR-3: Game auto-saves every 30s and on player exit (not every tick).
- FR-4: Game state is restored from SQLite on backend restart.

### 6.2 Gameplay
- FR-5: User can place an ant on a valid tile (`POST /api/games/{id}/ants`), subject to resource cost and placement validation.
- FR-6: Backend runs a fixed-rate tick (100ms) that: spawns zombies per wave schedule, moves zombies, resolves attacks/damage, removes dead entities, evaluates win/loss, and broadcasts the updated state.
- FR-7: Zombie reaching the end of the board decrements player life; life reaching 0 ends the game as `LOST`.
- FR-8: Clearing all waves ends the game as `WON`.
- FR-9: Resources accumulate over time/ticks per existing game rules and are spent on ant placement.

### 6.3 Real-Time Updates
- FR-10: Frontend receives state updates via WebSocket (STOMP) after each tick; REST is used only for commands (create game, place ant), not for polling state.

### 6.4 Frontend
- FR-11: Board renders as a grid (CSS Grid) showing tiles, ants, zombies.
- FR-12: HUD displays wave number, resources, life/status.
- FR-13: User actions (placing ants) call REST endpoints; UI updates only from server-confirmed/broadcast state, not optimistic local mutation of game rules.

### 6.5 Testing
- FR-14: Backend has unit tests for combat resolution, wave spawning, and API request/response behavior.
- FR-15: Frontend has tests confirming components render given state, user actions trigger correct API calls, and WebSocket messages update UI.

### 6.6 Deployment
- FR-16: `docker-compose up` starts Angular (4200), Spring Boot (8080), and mounts a SQLite volume, with no additional manual setup.

## 7. Optional / Stretch
- Authentication (JWT-based login, user-scoped games).

## 8. Acceptance Criteria (MVP)
- A user can start the app with one command, create a game, place ants, watch zombies spawn/move/die in real time without manual refresh, and see win/loss resolved correctly.
- Killing the backend process and restarting it resumes the last saved game state.
- Core combat/wave logic is covered by passing unit tests.

## 9. Open Questions
- Exact resource generation formula (rate, caps) — must be extracted from existing Java source (see Technical Spec, Stage 0).
- Exact wave spawn schedule/difficulty curve — must be extracted from existing Java source.
- Ant types/costs beyond "SHOOTER" example — needs enumeration from existing Java source.

## 10. Reference
Full staged build plan and architecture: see `TECH.md`. Build order: see `TODO.md`. Rules extracted in Stage 0: see `RULES.md` and `MAPPING.md`.