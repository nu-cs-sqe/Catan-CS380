# BVA Analysis – `Board`

## Method under test: `create()`

The `create()` method randomly assigns all Catan pieces to their board positions.  
It produces 19 terrain tiles (with correct resource numbers), assigns 18 number tokens to the non-desert tiles, and places the robber on the desert.

---

### TC1 – Tile count is exactly 19
- **State of the system**: Fresh `Board` with a stubbed `Shuffler` (no-op)
- **Expected output**: `getTiles().size() == 19`
- **BVA note**: 19 is the only valid count. Boundaries: 18 (too few), 19 (valid), 20 (too many)

### TC2 – No tile in the list is null
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: Every element of `getTiles()` is non-null
- **BVA note**: Boundary between a populated slot and an absent (null) slot

### TC3 – FOREST tile count is exactly 4
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: 4 tiles have `ResourceType.FOREST`
- **BVA note**: Boundaries: 3 (too few), 4 (valid), 5 (too many)

### TC4 – PASTURE tile count is exactly 4
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: 4 tiles have `ResourceType.PASTURE`
- **BVA note**: Boundaries: 3 (too few), 4 (valid), 5 (too many)

### TC5 – FIELDS tile count is exactly 4
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: 4 tiles have `ResourceType.FIELDS`
- **BVA note**: Boundaries: 3 (too few), 4 (valid), 5 (too many)

### TC6 – HILLS tile count is exactly 3
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: 3 tiles have `ResourceType.HILLS`
- **BVA note**: Boundaries: 2 (too few), 3 (valid), 4 (too many)

### TC7 – MOUNTAINS tile count is exactly 3
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: 3 tiles have `ResourceType.MOUNTAINS`
- **BVA note**: Boundaries: 2 (too few), 3 (valid), 4 (too many)

### TC8 – DESERT tile count is exactly 1
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: 1 tile has `ResourceType.DESERT`
- **BVA note**: Boundaries: 0 (too few), 1 (valid), 2 (too many)

### TC9 – Desert tile has no number token (token = 0)
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: The DESERT tile's `getNumberToken() == 0`
- **BVA note**: Boundary between "no token" (0) and the minimum valid token value (2)

### TC10 – All non-desert tiles have a number token ≥ 2
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: Every non-DESERT tile has `getNumberToken() >= 2`
- **BVA note**: Boundary between "no token" (0) and minimum valid token (2); catches any tile left at default 0

### TC11 – Number token 2 appears exactly once
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: Exactly 1 tile has `getNumberToken() == 2`
- **BVA note**: Token 2 is the minimum token value; its frequency boundary is 0 (missing) → 1 (valid) → 2 (duplicate, invalid)

### TC12 – Number token 12 appears exactly once
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: Exactly 1 tile has `getNumberToken() == 12`
- **BVA note**: Token 12 is the maximum token value; same frequency boundaries as TC11

### TC13 – Number token 3 appears exactly twice
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: Exactly 2 tiles have `getNumberToken() == 3`
- **BVA note**: Token 3 is the second-lowest token; frequency boundary between 1 (too few) and 2 (valid) and 3 (too many)

### TC14 – Number token 11 appears exactly twice
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: Exactly 2 tiles have `getNumberToken() == 11`
- **BVA note**: Token 11 is the second-highest token; same frequency boundaries as TC13

### TC15 – Number token 6 appears exactly twice
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: Exactly 2 tiles have `getNumberToken() == 6`
- **BVA note**: Token 6 is one of the two high-probability "red" tokens; validates its frequency is not inflated or missing

### TC16 – Number token 8 appears exactly twice
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: Exactly 2 tiles have `getNumberToken() == 8`
- **BVA note**: Token 8 is the other high-probability "red" token; same boundary as TC15

### TC17 – Total number token count is exactly 18
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: Exactly 18 tiles have `getNumberToken() > 0`
- **BVA note**: 18 tokens for 18 non-desert tiles. Boundaries: 17 (too few), 18 (valid), 19 (too many)

### TC18 – Robber starts on the desert tile
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: The DESERT tile's `hasRobber() == true`
- **BVA note**: Exactly 1 tile has the robber. Boundaries: 0 robbers (missing), 1 (valid), 2 (too many)

### TC19 – Non-desert tiles do not have the robber
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: 0 non-DESERT tiles have `hasRobber() == true`
- **BVA note**: Boundary between 0 non-desert robbers (valid) and 1 (invalid); complements TC18

### TC20 – Total vertex count is exactly 54
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: `getVertices().size() == 54`
- **BVA note**: Boundaries: 53 (too few), 54 (valid), 55 (too many)

### TC21 – Interior vertex count (3 adjacent tiles) is exactly 24
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: 24 vertices have `getAdjacentTiles().size() == 3`
- **BVA note**: Boundaries: 23 (too few), 24 (valid), 25 (too many)

### TC22 – Coastal vertex count (fewer than 3 adjacent tiles) is exactly 30
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: 30 vertices have `getAdjacentTiles().size() < 3`
- **BVA note**: Boundaries: 29 (too few), 30 (valid), 31 (too many); complements TC21

### TC23 – Total edge count is exactly 72
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: `getEdges().size() == 72`
- **BVA note**: Boundaries: 71 (too few), 72 (valid), 73 (too many)

### TC24 – Interior edge count (2 adjacent tiles) is exactly 42
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: 42 edges have `getAdjacentTiles().size() == 2`
- **BVA note**: Boundaries: 41 (too few), 42 (valid), 43 (too many)

### TC25 – Coastal edge count (1 adjacent tile) is exactly 30
- **State of the system**: Fresh `Board` with a stubbed `Shuffler`
- **Expected output**: 30 edges have `getAdjacentTiles().size() == 1`
- **BVA note**: Boundaries: 29 (too few), 30 (valid), 31 (too many); complements TC24
