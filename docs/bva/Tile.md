**# BVA: Tile

---

### Method under test: `Tile(TileType, int q, int r)`

|     | System under test                                     | Expected output                                                             | Implemented?       |
|-----|-------------------------------------------------------|-----------------------------------------------------------------------------|--------------------|
| TC1 | `new Tile(FOREST, 0, 0)`                              | `getTileType() == FOREST`                                                   | :white_check_mark: |
| TC2 | `new Tile(HILLS, -2, 1)`                              | `getQ() == -2`, `getR() == 1` (BVA: q at minimum board boundary -2)         | :white_check_mark: |
| TC3 | `new Tile(PASTURE, 0, 0)`, no `setNumberToken` called | `getNumberToken() == 0` (BVA: boundary between 0 and minimum valid token 2) | :white_check_mark: |
| TC4 | `new Tile(DESERT, 0, 0)`, no `setHasRobber` called    | `hasRobber() == false` (BVA: boundary - robber absent by default)           | :white_check_mark: |

---

### Method under test: `setNumberToken(int)`

|     | System under test                          | Expected output                                               | Implemented? |
|-----|--------------------------------------------|---------------------------------------------------------------|--------------|
| TC5 | `setNumberToken(2)` - minimum valid token  | `getNumberToken() == 2` (BVA: lower boundary of token range)  | :x:          |
| TC6 | `setNumberToken(12)` - maximum valid token | `getNumberToken() == 12` (BVA: upper boundary of token range) | :x:          |
