# BVA Analysis – `Vertex`

## Method under test: `Vertex(String key, List<Tile> adjacentTiles)` (constructor)

### TC1 – Constructor sets key correctly ( :white_check_mark: )
- **State of the system**: New `Vertex("0,0|1,-1|1,0", emptyList)`
- **Expected output**: `getKey() == "0,0|1,-1|1,0"`

### TC2 – Constructor sets adjacentTiles correctly ( :white_check_mark: )
- **State of the system**: New `Vertex` with a list of 3 specific tiles
- **Expected output**: `getAdjacentTiles()` returns the same list

### TC3 – Interior vertex has exactly 3 adjacent tiles ( :white_check_mark: )
- **State of the system**: New `Vertex` with a list of 3 tiles (all three surrounding hexes present)
- **Expected output**: `getAdjacentTiles().size() == 3`
- **BVA note**: 3 is the maximum adjacent tile count. Boundaries: 2 (coastal), 3 (interior/valid max)

### TC4 – Coastal vertex has exactly 1 adjacent tile ( :white_check_mark: )
- **State of the system**: New `Vertex` with a list of 1 tile (corner of the board)
- **Expected output**: `getAdjacentTiles().size() == 1`
- **BVA note**: 1 is the minimum adjacent tile count. Boundaries: 1 (corner coastal), 2 (edge coastal)