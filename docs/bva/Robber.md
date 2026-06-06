# BVA: Robber

---

### Method under test: `Robber()`

|     | System under test | Expected output                                                                   | Implemented? |
|-----|-------------------|-----------------------------------------------------------------------------------|--------------|
| TC1 | `new Robber()`    | `getTile() == null` (BVA: null boundary - no tile assigned at construction)       | :x:          |
| TC2 | `new Robber()`    | `getPlayer() == null` (BVA: null boundary - no player assigned at construction)   | :x:          |

---

### Method under test: `setTile(Tile)` / `getTile()`

|     | System under test                        | Expected output                                                           | Implemented? |
|-----|------------------------------------------|---------------------------------------------------------------------------|--------------|
| TC3 | `setTile(tile)` with a valid `Tile`      | `getTile() == tile` (valid tile assignment)                               | :x:          |
| TC4 | `setTile(null)` after a prior assignment | `getTile() == null` (BVA: null boundary - robber returned to unplaced state) | :x:          |

---

### Method under test: `setPlayer(Player)` / `getPlayer()`

|     | System under test                          | Expected output                                                             | Implemented? |
|-----|--------------------------------------------|-----------------------------------------------------------------------------|--------------|
| TC5 | `setPlayer(player)` with a valid `Player`  | `getPlayer() == player` (valid player assignment)                           | :x:          |
| TC6 | `setPlayer(null)` after a prior assignment | `getPlayer() == null` (BVA: null boundary - no player holds the robber)     | :x:          |