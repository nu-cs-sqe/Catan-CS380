# BVA Analysis – `Edge`

## Method under test: `Edge(String key, List<Tile> adjacentTiles)` (constructor)

### TC1 – Constructor sets key correctly ( :white_check_mark: )
- **State of the system**: New `Edge("0,0|1,0", emptyList)`
- **Expected output**: `getKey() == "0,0|1,0"`

### TC2 – Constructor sets adjacentTiles correctly ( :white_check_mark: )
- **State of the system**: New `Edge` with a list of 2 specific tiles
- **Expected output**: `getAdjacentTiles()` returns the same list

### TC3 – Interior edge has exactly 2 adjacent tiles ( :white_check_mark: )
- **State of the system**: New `Edge` with a list of 2 tiles (both surrounding hexes present)
- **Expected output**: `getAdjacentTiles().size() == 2`
- **BVA note**: 2 is the maximum adjacent tile count. Boundaries: 1 (coastal), 2 (interior/valid max)

### TC4 – Coastal edge has exactly 1 adjacent tile ( :white_check_mark: )
- **State of the system**: New `Edge` with a list of 1 tile (boundary of the board)
- **Expected output**: `getAdjacentTiles().size() == 1`
- **BVA note**: 1 is the minimum adjacent tile count. Boundaries: 1 (coastal/valid min), 2 (interior)