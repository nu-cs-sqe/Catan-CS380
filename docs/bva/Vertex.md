# BVA Analysis – `Vertex`

## Method under test: `Vertex(String id)` (constructor) and `addTile(Tile tile)`

### TC1 – Constructor stores id correctly
- **State of the system**: New `Vertex("7")`
- **Expected output**: `getId() == "7"`

### TC2 – Constructor initializes adjacentTiles as empty
- **State of the system**: New `Vertex("0")`
- **Expected output**: `getAdjacentTiles().isEmpty() == true`

### TC3 – Interior vertex has exactly 3 adjacent tiles
- **State of the system**: New `Vertex` with `addTile` called three times (all surrounding hexes present)
- **Expected output**: `getAdjacentTiles().size() == 3`
- **BVA note**: 3 is the maximum adjacent tile count. Boundaries: 2 (coastal), 3 (interior/valid max)

### TC4 – Coastal vertex has exactly 1 adjacent tile
- **State of the system**: New `Vertex` with `addTile` called once (corner of the board)
- **Expected output**: `getAdjacentTiles().size() == 1`
- **BVA note**: 1 is the minimum adjacent tile count. Boundaries: 1 (corner coastal), 2 (edge coastal)

### TC5 – addTile stores the correct tile reference
- **State of the system**: New `Vertex` with `addTile` called with a specific `Tile`
- **Expected output**: `getAdjacentTiles()` contains exactly that tile
