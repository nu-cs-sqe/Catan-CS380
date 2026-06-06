# BVA: Vertex

---

### Method under test: `Vertex(String id)`

|     | System under test | Expected output                                                                     | Implemented?       |
|-----|-------------------|-------------------------------------------------------------------------------------|--------------------|
| TC1 | `new Vertex("7")` | `getId() == "7"`                                                                    | :white_check_mark: |
| TC2 | `new Vertex("0")` | `getAdjacentTiles().isEmpty() == true` (BVA: boundary - empty list on construction) | :white_check_mark: |

---

### Method under test: `addTile(Tile)`

|     | System under test                               | Expected output                                                     | Implemented?       |
|-----|-------------------------------------------------|---------------------------------------------------------------------|--------------------|
| TC3 | `addTile` called 3 times (interior vertex)      | `getAdjacentTiles().size() == 3` (BVA: max; boundary from below: 2) | :white_check_mark: |
| TC4 | `addTile` called 1 time (corner coastal vertex) | `getAdjacentTiles().size() == 1` (BVA: min; boundary from above: 2) | :white_check_mark: |
| TC5 | `addTile` called with a specific `Tile`         | `getAdjacentTiles()` contains that exact tile reference             | :white_check_mark: |

---

### Method under test: `setOwner(Player)` / `getOwner()`

|     | System under test                           | Expected output                                            | Implemented? |
|-----|---------------------------------------------|------------------------------------------------------------|--------------|
| TC6 | `new Vertex`, no `setOwner` called          | `getOwner() == null` (BVA: null boundary - unowned vertex) | :x:          |
| TC7 | `setOwner(player)` with a non-null `Player` | `getOwner() == player`                                     | :x:          |

---

### Method under test: `setSettlement(Settlement)` / `getSettlement()`

|     | System under test                                        | Expected output                                                | Implemented? |
|-----|----------------------------------------------------------|----------------------------------------------------------------|--------------|
| TC8 | `new Vertex`, no `setSettlement` called                  | `getSettlement() == null` (BVA: null boundary - no settlement) | :x:          |
| TC9 | `setSettlement(settlement)` with a non-null `Settlement` | `getSettlement() == settlement`                                | :x:          |

---

### Method under test: `setHarbor(Harbor)` / `getHarbor()`

|      | System under test                            | Expected output                                                     | Implemented? |
|------|----------------------------------------------|---------------------------------------------------------------------|--------------|
| TC10 | `new Vertex`, no `setHarbor` called          | `getHarbor() == null` (BVA: null boundary - vertex not on a harbor) | :x:          |
| TC11 | `setHarbor(harbor)` with a non-null `Harbor` | `getHarbor() == harbor`                                             | :x:          |
