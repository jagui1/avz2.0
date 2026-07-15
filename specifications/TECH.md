# Technical Specification — Ants vs Zombies (Web Rewrite)

Reference implementation plan for Cursor. Implement stages in order; each stage should compile/run and pass its own tests before moving to the next.

## Architecture

```
Angular (4200)
  Components | RxJS State | CSS Grid board
        | REST (commands) + WebSocket/STOMP (state)
Spring Boot (8080)
  Controllers -> Services -> Game Engine -> WebSocket Handler
                                  |
                            Persistence Layer
                                  |
                               SQLite
```

Backend is authoritative. Frontend never computes game rules; it renders server state and sends commands.

## Repository Layout

```
ants-vs-zombies/
├── frontend/            Angular app
│   └── src/app/
│       ├── game/
│       │   ├── board/
│       │   ├── entities/
│       │   └── hud/
│       ├── services/
│       ├── models/
│       └── core/
├── backend/              Spring Boot app
│   └── src/main/java/com/game/
│       ├── controller/
│       ├── service/
│       ├── engine/
│       ├── model/
│       ├── repository/
│       ├── dto/
│       └── websocket/
├── database/
│   └── sqlite.db
└── docker-compose.yml
```

## Stage 0 — Analyze Existing Java Game
- Produce a mapping table (old class/concept -> new location) before writing any new code.
- Document exact rules for: ant attack resolution, zombie movement/speed, damage formulas, resource generation rate, wave spawn schedule, win/loss conditions.
- These extracted values feed the engine implementation in Stage 3 — do not invent placeholder numbers if the source values are available.

## Stage 1 — Project Scaffolding
Backend dependencies: Spring Web, Spring WebSocket, Spring Data JPA, SQLite JDBC driver, Lombok, Validation, JUnit.
Create backend/frontend directory structures as shown above.

## Stage 2 — Domain Model (no API, no DB, no network yet)

```java
class Game {
    Long id;
    GameStatus status;
    int wave;
    int resources;
    Board board;
}

class Board {
    int rows;
    int columns;
    List<Tile> tiles;
}

class Tile {
    int x;
    int y;
    Ant ant;
    Zombie zombie;
}

class Ant {
    int health;
    int damage;
    int attackSpeed;
}

class Zombie {
    int health;
    int speed;
    int position;
}
```

Validate this model in isolation (unit-testable POJOs) before building the engine around it.

## Stage 3 — Game Engine
Classes: `GameEngine`, `CombatEngine`, `MovementEngine`, `WaveEngine`, `ResourceEngine`.

Tick loop:
```java
@Scheduled(fixedRate = 100)
public void tick() {
    updateGame();
}
```

Per tick, in order:
1. Spawn zombies (per wave schedule)
2. Move zombies
3. Resolve attacks
4. Apply damage
5. Remove dead entities
6. Check win/loss
7. Broadcast state (via WebSocket)

Combat example: ant attacks zombie -> zombie health decreases; if health <= 0, remove zombie.

## Stage 4 — SQLite Persistence
Use Spring Data JPA + SQLite driver.

Tables:
```
games        (id, status, wave, resources)
game_tiles   (id, game_id, x, y)
entities     (id, game_id, type, health, position)
```

Save flow: `GameEngine -> GameService -> Repository -> SQLite`.
Save cadence: every 30 seconds OR on player exit — **not** every tick.

## Stage 5 — REST API

| Endpoint | Method | Purpose |
|---|---|---|
| `/api/games` | POST | Create game. Response: `{ "id": 1, "status": "RUNNING" }` |
| `/api/games/{id}` | GET | Fetch state. Response includes `wave`, `resources`, `zombies[]`, `ants[]` |
| `/api/games/{id}/ants` | POST | Place ant. Body: `{ "type": "SHOOTER", "x": 3, "y": 5 }` |

Place-ant flow: validate placement -> check resources -> create ant -> deduct resources.

## Stage 6 — Angular UI

Component tree:
```
GameComponent
 ├── BoardComponent
 │    ├── TileComponent
 │    ├── AntComponent
 │    └── ZombieComponent
 └── HudComponent
```

Board rendering via CSS Grid:
```css
.board {
  display: grid;
  grid-template-columns: repeat(10, 50px);
}
```

## Stage 7 — Angular State Management
Centralize state in a `GameService` using RxJS:
```typescript
gameState$ = new BehaviorSubject<GameState>(initialState);
```
Flow: `WebSocket -> GameService -> Components (subscribe) -> UI update`. Components must not hold parallel copies of game-rule state.

## Stage 8 — WebSockets
- Backend: Spring WebSocket + STOMP, broadcasting state after each tick.
- Frontend: `@stomp/stompjs` client subscribed to game-state topic.
- REST is command-only; state sync is WebSocket-only.

## Stage 9 — Authentication (Optional)
- Entities: `User` (password), relation to `Games`.
- Flow: login -> JWT issued -> Angular stores token -> subsequent API requests include token.

## Stage 10 — Testing

Backend (highest priority):
- Combat: zombie health 10, ant attacks, health reaches 0, zombie removed.
- Wave engine: wave 2 spawns the correct number/type of zombies.
- API: `POST /ants` returns success and game state reflects the new ant.

Frontend:
- Components render correctly given a state input.
- User actions trigger the correct API calls.
- WebSocket messages update the UI.

## Stage 11 — Local Deployment
`docker-compose.yml` runs three services: Angular container, Spring Boot container, SQLite volume.
Run with `docker compose up`. Frontend at `localhost:4200`, backend at `localhost:8080`.

## Build Order

| Stage | Deliverable |
|---|---|
| 0 | Rule/mapping documentation from existing Java game |
| 1 | Project scaffolding |
| 2 | Domain model |
| 3 | Working backend game engine |
| 4 | SQLite save/load |
| 5 | REST API |
| 6 | Angular UI |
| 7 | Angular state management |
| 8 | Real-time WebSockets |
| 9 | Authentication (optional) |
| 10 | Testing |
| 11 | Docker local deployment |

## Implementation Notes for Cursor
- Do not implement Stage N+1 until Stage N compiles and its own tests (if any) pass.
- Do not hardcode game-rule constants (damage, spawn rates, costs) without first checking Stage 0's extracted values; if unavailable, flag as `TODO(needs-source-value)` rather than guessing silently.
- Keep all authoritative game-rule logic in `backend/engine/*`; frontend code must not reimplement combat, movement, or win/loss logic.