# Ants vs Zombies — 5-phase build plan

Sources: [PRD.md](PRD.md) (product/MVP) and [TECH.md](TECH.md) (stages 0–11). Auth (TECH Stage 9) is stretch and **out of MVP**.

**Rule:** Finish Phase N (compile + phase tests) before Phase N+1. Keep all game rules in `backend/.../engine/*`; frontend only renders and sends commands.

**Starting point:** Hello-world scaffold exists (`frontend/`, `backend/` with `/api/hello`). Absorb it into Phase 1 layout; remove hello once game APIs exist.

```text
Phase1_Foundation → Phase2_Backend → Phase3_AngularUI → Phase4_WebSockets → Phase5_TestAndDocker
```

---

## Checklist (in order)

### Phase 1 — Foundation
- [x] Extract Stage 0 rules/mapping docs (or TODO gaps) into `specifications/`
- [x] Restructure packages/folders + add Stage 1 backend deps (WS, JPA, SQLite, Lombok, Validation)
- [x] Implement Stage 2 domain model POJOs + unit tests

### Phase 2 — Authoritative backend
- [ ] Implement Stage 3 game engines + 100ms tick loop
- [ ] Implement Stage 4 SQLite JPA save/load (30s + exit)
- [ ] Implement Stage 5 REST game/ants APIs; remove hello endpoint

### Phase 3 — Angular client
- [ ] Build Stage 6 Angular board/HUD component tree (CSS Grid)
- [ ] Stage 7 GameService BehaviorSubject; wire REST commands (poll until Phase 4)

### Phase 4 — Real-time
- [ ] STOMP WebSocket broadcast + frontend subscribe; drop polling

### Phase 5 — Test & ship MVP
- [ ] Backend + frontend tests (combat, waves, API, render, WS)
- [ ] docker-compose (Angular, Spring Boot, SQLite volume) + MVP acceptance

---

## Phase 1 — Foundation (TECH Stages 0–2)

**Goal:** Know the rules; reshape the repo; domain model exists and is unit-tested.

1. **Stage 0 — Extract rules** from the original Java game (or mark gaps as `TODO(needs-source-value)`): mapping table old→new; combat; movement/speed; damage; resource rate; wave schedule; win/loss/life. Write under `specifications/` (e.g. `RULES.md`, `MAPPING.md`). Unblocks PRD open questions (§9).
2. **Stage 1 — Scaffold to TECH layout**
   - Backend package tree under `com.game` (`controller`, `service`, `engine`, `model`, `repository`, `dto`, `websocket`); add deps: WebSocket, Data JPA, SQLite, Lombok, Validation (Web/JUnit already present).
   - Frontend folders: `game/board|entities|hud`, `services`, `models`, `core`.
   - Add `database/` placeholder; leave `docker-compose.yml` for Phase 5.
3. **Stage 2 — Domain model** (no API/DB/network): `Game`, `Board`, `Tile`, `Ant`, `Zombie`, `GameStatus`; POJO unit tests.

**Exit:** Rules doc + packages/deps + model tests green.

---

## Phase 2 — Authoritative backend (TECH Stages 3–5)

**Goal:** Server can run a game tick, save state, and accept REST commands (FR-1–9).

1. **Stage 3 — Engine:** `GameEngine`, `CombatEngine`, `MovementEngine`, `WaveEngine`, `ResourceEngine`; `@Scheduled(fixedRate = 100)` tick order: spawn → move → attack → damage → remove dead → win/loss. Constants from Phase 1 only.
2. **Stage 4 — SQLite:** JPA tables `game`, `game_tiles`, `entities`; save every 30s and on exit (not every tick); restore on restart (FR-3, FR-4).
3. **Stage 5 — REST:**
   - `POST /api/games` → create `{ id, status }`
   - `GET /api/games/{id}` → state (wave, resources, ants, zombies, life)
   - `POST /api/games/{id}/ants` → place ant (validate → cost → create → deduct)
   - Replace `/api/hello` once these work.

**Exit:** Create game, place ant via curl; tick advances state in memory + DB; combat/wave unit tests start here (expand in Phase 5).

---

## Phase 3 — Angular client UI + state shell (TECH Stages 6–7)

**Goal:** Board/HUD render server state; commands go through REST; no client-side rule logic (FR-11–13).

1. **Stage 6 — UI:** `GameComponent` → `BoardComponent` (`Tile` / `Ant` / `Zombie`) + `HudComponent`; CSS Grid board.
2. **Stage 7 — State:** `GameService` with `BehaviorSubject<GameState>`; components subscribe only. Temporary: poll `GET /api/games/{id}` until Phase 4 WebSocket (do not invent local combat).

**Exit:** Create game + place ant from UI; HUD/board update from service state.

---

## Phase 4 — Real-time WebSockets (TECH Stage 8)

**Goal:** State sync is STOMP-only after each tick; REST is command-only (FR-10).

1. Backend: Spring WebSocket + STOMP; broadcast after tick step 7.
2. Frontend: `@stomp/stompjs` → `GameService`; remove polling.
3. Verify: zombies move/die live without refresh (PRD acceptance).

**Exit:** Live board updates over WS; REST only for create/place.

---

## Phase 5 — Test, ship MVP (TECH Stages 10–11)

**Goal:** PRD acceptance (§8) and FR-14–16. Skip JWT auth (Stage 9) for MVP.

1. **Stage 10 — Tests:** Backend combat (e.g. HP 10 → 0 removed), wave 2 spawn counts, `POST /ants` API; frontend render-from-state, API-on-action, WS→UI.
2. **Stage 11 — Docker:** `docker-compose.yml` — Angular :4200, Spring Boot :8080, SQLite volume; `docker compose up` is the only setup step.
3. **Acceptance check:** one-command start → create → place ants → live waves → win/loss; kill/restart backend → saved game resumes.

**Exit:** MVP done. Optional later: Stage 9 auth.

---

## Phase → TECH stage map

| Phase | TECH stages | PRD coverage |
|-------|-------------|--------------|
| 1 | 0, 1, 2 | Rules for FR-6–9; layout |
| 2 | 3, 4, 5 | FR-1–9 |
| 3 | 6, 7 | FR-11–13 |
| 4 | 8 | FR-10 |
| 5 | 10, 11 | FR-14–16, §8 acceptance |

## Notes

- PRD §10 points at `TECHNICAL_SPEC.md`; repo file is [TECH.md](TECH.md).
- If original Java source is missing in Phase 1, document gaps and use `TODO(needs-source-value)` — do not silently invent balance numbers.
