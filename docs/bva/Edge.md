# BVA Analysis – `Edge`

## Method under test: `Edge(String id)` (constructor) and `addTile(Tile tile)`

### TC1 – Constructor stores id correctly
- **State of the system**: New `Edge("42")`
- **Expected output**: `getId() == "42"`

### TC2 – Constructor initializes adjacentTiles as empty
- **State of the system**: New `Edge("0")`
- **Expected output**: `getAdjacentTiles().isEmpty() == true`

### TC3 – Interior edge has exactly 2 adjacent tiles
- **State of the system**: New `Edge` with `addTile` called twice (both surrounding hexes present)
- **Expected output**: `getAdjacentTiles().size() == 2`
- **BVA note**: 2 is the maximum adjacent tile count. Boundaries: 1 (coastal), 2 (interior/valid max)

### TC4 – Coastal edge has exactly 1 adjacent tile
- **State of the system**: New `Edge` with `addTile` called once (boundary of the board)
- **Expected output**: `getAdjacentTiles().size() == 1`
- **BVA note**: 1 is the minimum adjacent tile count. Boundaries: 1 (coastal/valid min), 2 (interior)

### TC5 – addTile stores the correct tile reference
- **State of the system**: New `Edge` with `addTile` called with a specific `Tile`
- **Expected output**: `getAdjacentTiles()` contains exactly that tile