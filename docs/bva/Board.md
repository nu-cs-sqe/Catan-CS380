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
