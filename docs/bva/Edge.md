# BVA: Edge

---

### Method under test: `Edge(String id)`

|     | System under test | Expected output                                                                     | Implemented?       |
|-----|-------------------|-------------------------------------------------------------------------------------|--------------------|
| TC1 | `new Edge("42")`  | `getId() == "42"`                                                                   | :white_check_mark: |
| TC2 | `new Edge("0")`   | `getAdjacentTiles().isEmpty() == true` (BVA: boundary - empty list on construction) | :white_check_mark: |

---

### Method under test: `addTile(Tile)`

|     | System under test                        | Expected output                                                     | Implemented?       |
|-----|------------------------------------------|---------------------------------------------------------------------|--------------------|
| TC3 | `addTile` called 2 times (interior edge) | `getAdjacentTiles().size() == 2` (BVA: max; boundary from below: 1) | :white_check_mark: |
| TC4 | `addTile` called 1 time (coastal edge)   | `getAdjacentTiles().size() == 1` (BVA: min; boundary from above: 2) | :white_check_mark: |
| TC5 | `addTile` called with a specific `Tile`  | `getAdjacentTiles()` contains that exact tile reference             | :white_check_mark: |

---

### Method under test: `setOwner(Player)` / `getOwner()`

|     | System under test                           | Expected output                                          | Implemented? |
|-----|---------------------------------------------|----------------------------------------------------------|--------------|
| TC6 | `new Edge`, no `setOwner` called            | `getOwner() == null` (BVA: null boundary - unowned edge) | :x:          |
| TC7 | `setOwner(player)` with a non-null `Player` | `getOwner() == player`                                   | :x:          |
