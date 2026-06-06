# BVA: Board

---

### Method under test: `create()`

|      | System under test               | Expected output                                                                                                   | Implemented?       |
|------|---------------------------------|-------------------------------------------------------------------------------------------------------------------|--------------------|
| TC1  | Fresh `Board`, no-op `Shuffler` | `getTiles().size() == 19` (BVA: boundaries 18, 19, 20)                                                            | :white_check_mark: |
| TC2  | Fresh `Board`, no-op `Shuffler` | No element of `getTiles()` is null                                                                                | :white_check_mark: |
| TC3  | Fresh `Board`, no-op `Shuffler` | Count of FOREST tiles == 4 (BVA: boundaries 3, 4, 5)                                                              | :white_check_mark: |
| TC4  | Fresh `Board`, no-op `Shuffler` | Count of PASTURE tiles == 4 (BVA: boundaries 3, 4, 5)                                                             | :white_check_mark: |
| TC5  | Fresh `Board`, no-op `Shuffler` | Count of FIELDS tiles == 4 (BVA: boundaries 3, 4, 5)                                                              | :white_check_mark: |
| TC6  | Fresh `Board`, no-op `Shuffler` | Count of HILLS tiles == 3 (BVA: boundaries 2, 3, 4)                                                               | :white_check_mark: |
| TC7  | Fresh `Board`, no-op `Shuffler` | Count of MOUNTAINS tiles == 3 (BVA: boundaries 2, 3, 4)                                                           | :white_check_mark: |
| TC8  | Fresh `Board`, no-op `Shuffler` | Count of DESERT tiles == 1 (BVA: boundaries 0, 1, 2)                                                              | :white_check_mark: |
| TC9  | Fresh `Board`, no-op `Shuffler` | DESERT tile `getNumberToken() == 0` (BVA: boundary between 0 and minimum valid token 2)                           | :white_check_mark: |
| TC10 | Fresh `Board`, no-op `Shuffler` | All non-DESERT tiles have `getNumberToken() >= 2` (BVA: boundary at 2, the minimum valid token)                   | :white_check_mark: |
| TC11 | Fresh `Board`, no-op `Shuffler` | Token sequence (skipping DESERT) equals `[5,2,6,3,8,10,9,12,11,4,8,10,9,4,5,6,3,11]`                              | :white_check_mark: |
| TC12 | Fresh `Board`, no-op `Shuffler` | First token in sequence is 5 (BVA: lower boundary - position 0 of TOKEN_DISTRIBUTION)                             | :white_check_mark: |
| TC13 | Fresh `Board`, no-op `Shuffler` | Last token in sequence is 11 (BVA: upper boundary - position 17 of TOKEN_DISTRIBUTION)                            | :white_check_mark: |
| TC14 | Fresh `Board`, no-op `Shuffler` | Exactly 18 tiles have `getNumberToken() > 0` (BVA: boundaries 17, 18, 19)                                         | :white_check_mark: |
| TC15 | Fresh `Board`, no-op `Shuffler` | DESERT tile `hasRobber() == true`                                                                                 | :x:                |
| TC16 | Fresh `Board`, no-op `Shuffler` | 0 non-DESERT tiles have `hasRobber() == true`                                                                     | :x:                |
| TC17 | Fresh `Board`, no-op `Shuffler` | Exactly 1 tile has `hasRobber() == true` (BVA: boundaries 0, 1, 2)                                                | :x:                |
| TC18 | Fresh `Board`, no-op `Shuffler` | `getVertices().size() == 54` (BVA: boundaries 53, 54, 55)                                                         | :white_check_mark: |
| TC19 | Fresh `Board`, no-op `Shuffler` | 24 vertices have `getAdjacentTiles().size() == 3` (BVA: boundaries 23, 24, 25)                                    | :white_check_mark: |
| TC20 | Fresh `Board`, no-op `Shuffler` | 30 vertices have `getAdjacentTiles().size() < 3` (BVA: boundaries 29, 30, 31)                                     | :white_check_mark: |
| TC21 | Fresh `Board`, no-op `Shuffler` | `getEdges().size() == 72` (BVA: boundaries 71, 72, 73)                                                            | :white_check_mark: |
| TC22 | Fresh `Board`, no-op `Shuffler` | 42 edges have `getAdjacentTiles().size() == 2` (BVA: boundaries 41, 42, 43)                                       | :white_check_mark: |
| TC23 | Fresh `Board`, no-op `Shuffler` | 30 edges have `getAdjacentTiles().size() == 1` (BVA: boundaries 29, 30, 31)                                       | :white_check_mark: |
| TC24 | Fresh `Board`, no-op `Shuffler` | `getHarbors().size() == 9` (BVA: boundaries 8, 9, 10)                                                             | :white_check_mark: |
| TC25 | Fresh `Board`, no-op `Shuffler` | Exactly 4 harbors have type GENERIC                                                                               | :white_check_mark: |
| TC26 | Fresh `Board`, no-op `Shuffler` | Exactly 1 harbor each for WOOD, BRICK, SHEEP, WHEAT, ORE                                                          | :white_check_mark: |
| TC27 | Fresh `Board`, no-op `Shuffler` | Exactly 18 vertices have a non-null `getHarbor()` (BVA: boundaries 17, 18, 19)                                    | :white_check_mark: |
| TC28 | Fresh `Board`, no-op `Shuffler` | Each resource-specific harbor has `getExchangeRate() == 2` (BVA: min exchange rate)                               | :white_check_mark: |
| TC29 | Fresh `Board`, no-op `Shuffler` | Each GENERIC harbor has `getExchangeRate() == 3` (BVA: boundary between 2 and 3)                                  | :white_check_mark: |
| TC32 | Fresh `Board`, no-op `Shuffler` | Token value 2 (min valid token) appears exactly once across all tiles (BVA: lower boundary of token value range)  | :white_check_mark: |
| TC33 | Fresh `Board`, no-op `Shuffler` | Token value 12 (max valid token) appears exactly once across all tiles (BVA: upper boundary of token value range) | :white_check_mark: |

---

### Method under test: `getTile(int q, int r)`

|      | System under test                                         | Expected output                                          | Implemented? |
|------|-----------------------------------------------------------|----------------------------------------------------------|--------------|
| TC34 | Board created; `getTile(0, 0)` - center position          | Returns non-null `Tile`                                  | :white_check_mark: |
| TC35 | Board created; `getTile(-2, 0)` - min-q boundary position | Returns non-null `Tile` (BVA: lower boundary of q range) | :white_check_mark: |
| TC36 | Board created; `getTile(2, 0)` - max-q boundary position  | Returns non-null `Tile` (BVA: upper boundary of q range) | :white_check_mark: |
| TC37 | Board created; `getTile(3, 0)` - just outside max-q       | Returns `null` (BVA: one past upper boundary)            | :white_check_mark: |

---

### Method under test: `getNeighbors(int q, int r)`

|      | System under test                                                                           | Expected output                                                          | Implemented? |
|------|---------------------------------------------------------------------------------------------|--------------------------------------------------------------------------|--------------|
| TC38 | Board created; `getNeighbors(0, 0)` - center tile, all 6 neighbors exist                    | Returns list of 6 tiles (BVA: max neighbor count)                        | :white_check_mark: |
| TC39 | Board created; `getNeighbors(-2, 0)` - corner tile, 3 neighbors missing                     | Returns list of 3 tiles (BVA: min neighbor count for a valid board tile) | :white_check_mark: |
| TC40 | Board created; `getNeighbors(5, 5)` - position far off the board, no neighbors in POSITIONS | Returns empty list (BVA: minimum = 0 neighbors)                          | :white_check_mark: |

---

### Method under test: `getVertex(String key)`

|      | System under test                                                   | Expected output                                             | Implemented? |
|------|---------------------------------------------------------------------|-------------------------------------------------------------|--------------|
| TC41 | Board created; query a known vertex key (e.g., `"-1,1"`)            | Returns non-null `Vertex`                                   | :white_check_mark: |
| TC42 | Board created; query a key that matches no vertex (e.g., `"99,99"`) | Returns `null` (BVA: null boundary - vertex does not exist) | :white_check_mark: |

---

### Method under test: `getEdge(String key)`

|      | System under test                                              | Expected output | Implemented?                                              |
|------|----------------------------------------------------------------|-----------------|-----------------------------------------------------------|
| TC43 | Board created; query a known edge key (e.g., `"-1,-1\|-1,1"`)   | Returns non-null `Edge`                                   | :white_check_mark: |
| TC44 | Board created; query a key that matches no edge (e.g., `"99,99\|100,100"`) | Returns `null` (BVA: null boundary - edge does not exist) | :white_check_mark: |

---

### Method under test: `getHarbor(String vertex1Id, String vertex2Id)`

|      | System under test                                                | Expected output                                                  | Implemented? |
|------|------------------------------------------------------------------|------------------------------------------------------------------|--------------|
| TC45 | Board created; query known harbor vertex pair `"-5,-1"`, `"-5,1"` | Returns non-null `Harbor` with type GENERIC                      | :white_check_mark: |
| TC46 | Board created; query an interior vertex pair that has no harbor   | Returns `null` (BVA: null boundary - no harbor at this location) | :white_check_mark: |
