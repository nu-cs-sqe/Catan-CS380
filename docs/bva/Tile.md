# BVA Analysis – `Tile`

## Method under test: `Tile(TileType, int q, int r)` (constructor)

### TC1 – Constructor sets TileType correctly

- **State of the system**: New `Tile(FOREST, 0, 0)`
- **Expected output**: `getTileType() == FOREST`

### TC2 – Constructor sets q and r coordinates correctly

- **State of the system**: New `Tile(HILLS, -2, 1)`
- **Expected output**: `getQ() == -2`, `getR() == 1`

### TC3 – Number token defaults to 0

- **State of the system**: New `Tile(PASTURE, 0, 0)`, no `setNumberToken` called
- **Expected output**: `getNumberToken() == 0`

### TC4 – Robber defaults to false

- **State of the system**: New `Tile(DESERT, 0, 0)`, no `setHasRobber` called
- **Expected output**: `hasRobber() == false`
