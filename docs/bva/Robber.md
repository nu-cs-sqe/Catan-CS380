# BVA: Robber

---

### Method under test: `Robber()`

|     | System under test | Expected output                                                                 | Implemented?       |
|-----|-------------------|---------------------------------------------------------------------------------|--------------------|
| TC1 | `new Robber()`    | `getTile() == null` (BVA: null boundary - no tile assigned at construction)     | :white_check_mark: |
| TC2 | `new Robber()`    | `getPlayer() == null` (BVA: null boundary - no player assigned at construction) | :white_check_mark: |

---

### Method under test: `setTile(Tile)` / `getTile()`

|     | System under test                        | Expected output                                                              | Implemented?       |
|-----|------------------------------------------|------------------------------------------------------------------------------|--------------------|
| TC3 | `setTile(tile)` with a valid `Tile`      | `getTile() == tile` (valid tile assignment)                                  | :white_check_mark: |
| TC4 | `setTile(null)` after a prior assignment | `getTile() == null` (BVA: null boundary - robber returned to unplaced state) | :white_check_mark: |

---

### Method under test: `setPlayer(Player)` / `getPlayer()`

|     | System under test                          | Expected output                                                         | Implemented?       |
|-----|--------------------------------------------|-------------------------------------------------------------------------|--------------------|
| TC5 | `setPlayer(player)` with a valid `Player`  | `getPlayer() == player` (valid player assignment)                       | :white_check_mark: |
| TC6 | `setPlayer(null)` after a prior assignment | `getPlayer() == null` (BVA: null boundary - no player holds the robber) | :white_check_mark: |

---

## Robber Mechanics (in `Game`)

### Discarding on 7

### Method under test: `handleRollSeven()`

|      | System under test                                    | Expected output                                                          | Implemented? |
|------|------------------------------------------------------|--------------------------------------------------------------------------|--------------|
| TC7  | Player has 8 resource cards, a 7 is rolled           | Player must discard 4 cards                                              | :x:          |
|      |                                                      | BVA: boundary between 7 cards (no discard) and 8 cards (must discard 4)  |              |
| TC8  | Player has 7 resource cards, a 7 is rolled           | Player discards nothing                                                  | :x:          |
|      |                                                      | BVA: boundary between 7 cards (safe) and 8 cards (must discard)          |              |
| TC9  | Player has 9 resource cards, a 7 is rolled           | Player must discard 4 (9/2 rounded down)                                 | :x:          |
|      |                                                      | BVA: boundary between even (exact half) and odd (round down)             |              |

---

### Moving the Robber

### Method under test: `moveRobber(Board, Robber, int q, int r)`

|      | System under test                                          | Expected output                                                      | Implemented? |
|------|------------------------------------------------------------|----------------------------------------------------------------------|--------------|
| TC10 | Robber is on hex (0,0), player tries to move to (0,0)     | Throws `IllegalArgumentException`                                    | :x:          |
|      |                                                            | BVA: boundary between same hex (invalid) and different hex (valid)   |              |
| TC11 | Robber is on hex (0,0), player moves to (-2,0)            | Robber tile is now (-2,0)                                            | :x:          |
|      |                                                            | BVA: any hex other than current is valid                             |              |
| TC12 | Robber moved to hex with token 5, player has settlement adjacent | Rolling 5 produces nothing for that player                     | :x:          |
|      |                                                            | BVA: boundary between robber absent (produces) and present (blocked) |              |

---

### Stealing

### Method under test: `stealResource(Robber, Player victim)`

|      | System under test                                                           | Expected output                                                          | Implemented? |
|------|-----------------------------------------------------------------------------|--------------------------------------------------------------------------|--------------|
| TC13 | Robber on hex; opponent has settlement adjacent and holds resources         | Opponent loses 1 resource, current player gains 1 resource               | :x:          |
|      |                                                                             | BVA: boundary between 0 adjacent opponents (no steal) and 1+ (steal)     |              |
| TC14 | Robber on hex; opponent has settlement adjacent but holds 0 resources      | No resources change hands                                                | :x:          |
|      |                                                                             | BVA: boundary between 0 resources (nothing to steal) and 1+ (can steal)  |              |
| TC15 | Robber on hex; two opponents have settlements adjacent, choose one to steal | Current player steals from the chosen opponent                           | :x:          |
|      |                                                                             | BVA: boundary between 1 adjacent opponent (no choice) and 2+ (must choose) |            |
| TC16 | Robber on hex; no opponent has settlement adjacent                         | No steal occurs                                                          | :x:          |
|      |                                                                             | BVA: boundary between 0 adjacent opponents (no steal) and 1 (can steal)  |              |