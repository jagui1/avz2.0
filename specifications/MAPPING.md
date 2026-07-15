# Class / Concept Mapping — Java → Web Rewrite

Old source: `/Users/remy/programming/Ants-Vs-Zombies`  
New layout: [TECH.md](TECH.md) packages under `com.game` and Angular `frontend/src/app`.

| Old class / concept | New location | Notes |
|---------------------|--------------|-------|
| `proj4.Driver` loop | `com.game.service.GameService` + `com.game.engine.GameEngine` | Recruit ↔ invade phases; TECH also wants a tick loop for TD redesign |
| `proj4.Game` state | `com.game.model.Game` | food→resources, round→wave, colony/horde |
| `proj4.GameInterface` | REST controllers + services | Recruit / fight / state queries |
| `proj4.Ant` + `ants.*` | `com.game.model.Ant` + type enum; behavior in `engine.CombatEngine` | |
| `proj4.Zombie` + `zombies.*` | `com.game.model.Zombie` + type enum; `CombatEngine` | |
| `Ant.makeAnt` / `Zombie.makeZombie` | `engine` factory helpers | Keep type codes |
| `Game.nextFight` | `com.game.engine.CombatEngine` | One exchange (or per-tick combat in TD mode) |
| `Game.readHordeData` | `com.game.engine.WaveEngine` | Horde strings / wave schedule |
| `Game.recruitAnt` / costs | `com.game.service` + engine | Resource spend |
| `Game.isInvasionOver` / `isGameOver` | `com.game.engine.GameEngine` | Win/loss |
| Food / rewards | `com.game.engine.ResourceEngine` | Kill rewards; passive rate is gap |
| `Gigantic` / `Flammable` | model traits/tags | |
| Horde `.data` / Driver strings | `resources` or config | |
| `RecruitDialog` | `frontend/src/app/game/hud` | REST commands |
| `InvasionDialog` | `frontend/src/app/game` + entities | |
| Round / food labels | `frontend/src/app/game/hud` | |
| State broadcast | `com.game.websocket` | After tick / fight |
| API payloads | `com.game.dto` | |
| Persistence | `com.game.repository` | |
| Board / Tile / place-ant | `com.game.model.Board` / `Tile` | **No old equivalent** — PRD redesign |
| Movement / zombie speed | `com.game.engine.MovementEngine` | **No old equivalent** |
| 100ms tick | `@Scheduled` in `GameEngine` | **No old equivalent** |
| Player life | `Game.life` (when added) | **No old equivalent** — PRD FR-7 |

## Frontend folders (TECH)

| Folder | Purpose |
|--------|---------|
| `game/board` | CSS Grid board, tiles |
| `game/entities` | Ant / zombie presentation |
| `game/hud` | Wave, resources, life, actions |
| `services` | HTTP / WebSocket / GameService |
| `models` | TypeScript interfaces mirroring DTOs |
| `core` | Shared utilities, interceptors later |
