# Game Rules — Extracted from Original Java Source

Source: `/Users/remy/programming/Ants-Vs-Zombies` (package `proj4`, `ants`, `zombies`).

**Critical design note:** The original game is **turn-based queue combat** (recruit ants → FIGHT! exchanges), **not** a grid tower-defense. PRD/TECH assume a board + 100ms tick. Entity combat numbers below are from source; grid/tick/life/movement values are gaps.

---

## Mapping to web rewrite

See [MAPPING.md](MAPPING.md).

---

## Starting state

| Rule | Value | Source |
|------|-------|--------|
| Starting resources (food) | `100` | `proj4.Game` constructor |
| Starting round / wave | `1` | `proj4.Game` |

---

## Ant types (cost, health, attack)

Constructor pattern: `super(cost, life, description)`.

| Type | Cost | Health | Attack / special |
|------|------|--------|------------------|
| Army Ant | 35 | 30 | `10 + 5 * armyCounter` to front zombie |
| Bullet Ant | 10 | 1 | 25 to front |
| Carpenter Ant | 10 | 10 | 10 to front |
| Citronella Ant | 25 | 20 | 10 to front; **on death**: all ants & zombies take 2 |
| Devil Ant | 100 | 666 | 2 to front; **on death**: remove horde[0] (no reward); between rounds: `life += 50` instead of full reset |
| Fire Ant | 15 | 20 | 20 if target `Flammable`, else 10 |
| Leafcutter Ant | 20 | 10 | 10 to front; if kill, zombie does not counterattack |
| Pharoh Ant | 15 | 10 | 30 if target `Gigantic`, else 10 |
| Shadow Ant | 15 | 10 | 5 to front; dodge if `Random.nextFloat() < 0.4` (~40%) |
| Sugar Ant | 20 | 20 | 10 to front; **+5 food** on kill |
| Thief Ant | 15 | 25 | No attack on own turn; on damage: take amount, deal `amount / 2` |
| Weaver Ant | 20 | 10 | If horde size > 1: 15 to index 1; else no attack |
| Scout Ant | 5 | 5 | 5 to front; reveals horde in UI; cannot undo recruit |

**`attackSpeed`:** `TODO(needs-source-value)` — original combat is one exchange per FIGHT!, not timed attacks.

Closest baseline for a simple “SHOOTER” placeholder until product picks types: Carpenter (cost 10, HP 10, dmg 10) or Bullet (cost 10, HP 1, dmg 25).

---

## Zombie types (health, reward)

Constructor: `super(life, reward, description)`. No speed field in source.

| Code | Type | Health | Food reward | Attack / special |
|------|------|--------|-------------|------------------|
| `Z` | Standard | 10 | 10 | 10 to front ant |
| `A` | Armored | 20 | 15 | 10; first hit ignored |
| `G` | Giant | 40 | 20 | 35; `Gigantic` |
| `I` | Infected | 20 | 15 | 25; `Flammable` |
| `R` | Radioactive | 70 | 25 | 5 to front ant and 5 to horde[1] if present; `Gigantic`+`Flammable` |
| `V` | Voodoo | 15 | 15 | 10; if ant dies, append Standard Zombie |
| `N` | Ninja | 10 | 5 | 15 + steal 5 food |
| `S` | Scientist | 5 | 5 | 5 |

**Zombie `speed` / movement:** `TODO(needs-source-value)` — no movement in source.

---

## Combat resolution (`Game.nextFight`)

1. Front ant (`colony[0]`) attacks.
2. Unless Leafcutter already killed front zombie, front zombie counterattacks.
3. Remove ants with `life <= 0` (apply Citronella/Devil death effects).
4. Remove zombies with `life <= 0`; add `zombie.getReward()` to food.
5. If colony empty and horde remaining → game over (loss).

---

## Resources

| Rule | Value |
|------|-------|
| Passive / per-tick generation | **None** in source — `TODO(needs-source-value)` for PRD FR-9 “accumulate over time” |
| Food on zombie kill | `zombie.getReward()` |
| Sugar Ant kill bonus | `+5` |
| Ninja steal | `-5` (can go negative; no clamp in source) |
| Cap | `TODO(needs-source-value)` — none in source |
| Recruit | Allowed only if `food - cost >= 0` |

---

## Waves / hordes

Parser: non-digit char → add that zombie; digit `d` → add `d` extra copies of the previous char.

| Round index | String | Expanded (front → back) |
|-------------|--------|-------------------------|
| 1 | `SZI1` | S, Z, I, I |
| 2 | `GA` | G, A |
| 3 | `RS9Z2` | R, 10×S, 3×Z |
| 4 | `V1` | V, V |
| 5 | `INNV` | I, N, N, V |

Selection: `hordes[(roundNumber - 1) % 5]` — **cycles forever**; no finite “clear all waves” win in active code (win path commented out).

PRD FR-8 (“clearing all waves → WON”) is a **product redesign** vs original. Mark implementation choice explicitly when building the engine.

---

## Win / loss (original)

| Condition | Behavior |
|-----------|----------|
| Invasion over | Colony empty **or** horde empty → `roundNumber++`; survivors reset HP (Devil: `+50`) |
| Loss mid-fight | Colony empty, horde remaining → game over |
| Loss between rounds | `food == 0 && colony.isEmpty()` → game over |
| Player life / end-of-board | `TODO(needs-source-value)` — does not exist in source (PRD FR-7 is redesign) |

---

## Timing

| Concept | Original | Web PRD/TECH |
|---------|----------|--------------|
| Tick | Player clicks FIGHT! | `@Scheduled(fixedRate = 100)` — `TODO(needs-source-value)` for rate if preserving feel |
| Board size | N/A | `TODO(needs-source-value)` — invent for TD redesign |

---

## Gaps summary (`TODO(needs-source-value)`)

- Board `rows` / `columns` / tiles / placement
- Ant `attackSpeed`
- Zombie `speed` / movement / end-of-board
- Player `life`
- Passive resource generation rate / caps
- Continuous tick rate (100ms is TECH target, not from Java source)
- Finite win-after-N-waves (original cycles hordes)
- Canonical “SHOOTER” ant type name
