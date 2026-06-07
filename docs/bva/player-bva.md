# BVA: Player

Boundary Value Analysis for the `Player` class. Each row maps to a test in `src/test/java/domain/PlayerTest.java`. Rulebook references live in `docs/requirements/game-rules.md`. **Solitary** tests exercise `Player` alone. **Sociable** tests use real `board.Vertex`, `Edge`, `Tile`, or `Harbor` collaborators (no mocks) to catch integration bugs.

---

## Solitary tests

### Method under test: `Player(String name, PlayerColor color)`

|      | System under test                | Expected output                        | Implemented? |
|------|----------------------------------|----------------------------------------|--------------|
| TC1  | name = "Alice", color = RED      | Player constructed, name/color set     | yes          |
| TC2  | name = null                      | NullPointerException                   | yes          |
| TC3  | name = ""                        | IllegalArgumentException               | yes          |
| TC4  | name = "   " (blank)             | IllegalArgumentException               | yes          |
| TC5  | color = null                     | NullPointerException                   | yes          |
| TC6  | freshly constructed              | 0 VP, 0 of every resource, full pieces | yes          |


### Method under test: `addResource(Resource r, int amount)`

|      | System under test                | Expected output                  | Implemented? |
|------|----------------------------------|----------------------------------|--------------|
| TC7  | amount = 1, BRICK count = 0      | BRICK = 1                        | yess           |
| TC8  | amount = 0                       | no change (boundary lower-valid) | yes          |
| TC9  | amount = -1                      | IllegalArgumentException         | yes           |
| TC10 | r = null, amount = 1             | NullPointerException             | yes          |
| TC11 | add 3 BRICK then add 2 BRICK     | BRICK = 5 (accumulates)          | yes           |


### Method under test: `removeResource(Resource r, int amount)`

|      | System under test              | Expected output                          | Implemented? |
|------|--------------------------------|------------------------------------------|--------------|
| TC12 | BRICK = 2, remove 2 BRICK      | BRICK = 0 (exact boundary)               | yes           |
| TC13 | BRICK = 2, remove 3 BRICK      | IllegalStateException, BRICK unchanged   | yes           |
| TC14 | amount = -1                    | IllegalArgumentException                 | yes           |


### Method under test: `hasResources(Map cost)`

|      | System under test                          | Expected output                            | Implemented? |
|------|--------------------------------------------|--------------------------------------------|--------------|
| TC15 | has {BRICK:1, LUMBER:1}, cost = settlement | hasResources true                          | yes           |
| TC16 | missing exactly 1 GRAIN of settlement cost | hasResources false (just-below boundary)   | yes           |


### Method under test: `discardOnSevenCount()`

|      | System under test    | Expected output                       | Implemented? |
|------|----------------------|---------------------------------------|--------------|
| TC17 | hand size 6          | 0 (below boundary)                    | yes           |
| TC18 | hand size 7          | 0 (exact boundary, no discard)        | yes           |
| TC19 | hand size 8          | 4 (just above boundary, round down)   | yes           |
| TC20 | hand size 9          | 4 (odd, round down)                   | yes           |
| TC21 | hand size 0          | 0 (empty hand)                        | yes           |


### Method under test: VP accounting (`getVictoryPoints()` / `hasWon()`)

|      | System under test                                              | Expected output                              | Implemented? |
|------|----------------------------------------------------------------|----------------------------------------------|--------------|
| TC22 | fresh player                                                   | VP = 0, hasWon = false                       | yes           |
| TC23 | 4 settlements + 1 city                                         | VP = 6                                       | yes           |
| TC24 | 3 settlements + 3 cities                                       | VP = 9, hasWon = false (just below boundary) | yes           |
| TC25 | 4 settlements + 3 cities                                       | VP = 10, hasWon = true (exact boundary)      | yes           |
| TC26 | 5 settlements + 3 cities                                       | VP = 11, hasWon = true (above boundary)      | yes           |
| TC27 | 3 settlements + 2 cities + longest road                        | VP = 9                                       | yes           |
| TC28 | 3 settlements + 2 cities + longest road + largest army         | VP = 11, hasWon = true                       | yes           |
| TC29 | award then revoke longest road                                 | VP returns to pre-award value                | yes           |
| TC30 | 5 VP dev cards held                                            | VP = 5                                       | yes           |


### Method under test: piece inventory boundaries

|      | System under test                       | Expected output                                | Implemented? |
|------|-----------------------------------------|------------------------------------------------|--------------|
| TC31 | fresh player                            | 5 settlements / 4 cities / 15 roads remaining  | no           |
| TC32 | placed 5 settlements, place a 6th       | IllegalStateException (cap boundary)           | no           |
| TC33 | placed 4 cities, place a 5th            | IllegalStateException                          | no           |
| TC34 | placed 15 roads, place a 16th           | IllegalStateException                          | no           |
| TC35 | upgrade settlement → city               | settlements +1 back, cities -1 from remaining  | no           |
| TC36 | upgrade with 0 settlements on board     | IllegalStateException                          | no           |


### Method under test: `playKnight()` / `getKnightsPlayed()`

|      | System under test            | Expected output                                  | Implemented? |
|------|------------------------------|--------------------------------------------------|--------------|
| TC37 | 0 knights played, play one   | knights = 1                                      | no           |
| TC38 | 2 knights played, play one   | knights = 3 (Largest Army eligibility boundary)  | no           |


---

## Sociable tests

### Method under test: `placeSettlementAt(Vertex v)` — collaborators: real `Vertex`, real `Settlement`

|      | System under test                                                 | Expected output                                                                                | Implemented? |
|------|-------------------------------------------------------------------|------------------------------------------------------------------------------------------------|--------------|
| TC39 | vertex unowned, player has 1+ settlement remaining                | `vertex.getOwner() == player`, `vertex.getSettlement() != null`, player's settlements -1       | no           |
| TC40 | vertex already has a settlement (any owner)                       | IllegalStateException, vertex unchanged, player inventory unchanged                            | no           |
| TC41 | vertex owned by another player                                    | IllegalStateException                                                                          | no           |
| TC42 | player has 0 settlements remaining                                | IllegalStateException (delegates to piece cap)                                                 | no           |
| TC43 | vertex = null                                                     | NullPointerException                                                                           | no           |


### Method under test: `placeRoadAt(Edge e)` — collaborators: real `Edge`

|      | System under test                                                 | Expected output                                                         | Implemented? |
|------|-------------------------------------------------------------------|-------------------------------------------------------------------------|--------------|
| TC44 | edge unowned, player has 1+ road remaining                        | `edge.getOwner() == player`, roads remaining -1                         | no           |
| TC45 | edge already owned (by anyone)                                    | IllegalStateException, edge unchanged                                   | no           |
| TC46 | player has 0 roads remaining                                      | IllegalStateException                                                   | no           |
| TC47 | edge = null                                                       | NullPointerException                                                    | no           |


### Method under test: `upgradeToCityAt(Vertex v)` — collaborators: real `Vertex`

|      | System under test                                                  | Expected output                                                                          | Implemented? |
|------|--------------------------------------------------------------------|------------------------------------------------------------------------------------------|--------------|
| TC48 | vertex has player's settlement, player has 1+ city remaining       | vertex now holds a city, settlements +1 back to inventory, cities -1 from inventory      | no           |
| TC49 | vertex has another player's settlement                             | IllegalStateException                                                                    | no           |
| TC50 | vertex has no settlement                                           | IllegalStateException                                                                    | no           |
| TC51 | player has 0 cities remaining                                      | IllegalStateException                                                                    | no           |


### Method under test: `collectFromTile(Tile t)` — collaborators: real `Tile`, real `Vertex`

|      | System under test                                              | Expected output                       | Implemented? |
|------|----------------------------------------------------------------|---------------------------------------|--------------|
| TC52 | tile = FOREST, player has settlement on adjacent vertex        | LUMBER +1                             | no           |
| TC53 | tile = FOREST, player has city on adjacent vertex              | LUMBER +2 (city yields double)        | no           |
| TC54 | tile = DESERT, player has settlement on adjacent vertex        | no resources gained                   | no           |
| TC55 | tile = HILLS, player has no settlement on any adjacent vertex  | no resources gained                   | no           |
| TC56 | tile = FIELDS, player has settlement on 2 adjacent vertices    | GRAIN +2 (one per settlement)         | no           |


### Method under test: `getTradeRate(Resource r)` — collaborators: real `Harbor` accessed through `Vertex.getHarbor()`

|      | System under test                                                | Expected output                          | Implemented? |
|------|------------------------------------------------------------------|------------------------------------------|--------------|
| TC57 | no settlement on any harbor vertex                               | 4 for every resource (default maritime)  | no           |
| TC58 | settlement on a GENERIC 3:1 harbor, querying BRICK               | 3                                        | no           |
| TC59 | settlement on a BRICK 2:1 harbor, querying BRICK                 | 2 (specific beats generic)               | no           |
| TC60 | settlement on a BRICK 2:1 harbor, querying LUMBER                | 4 (specific harbor does not apply)       | no           |
| TC61 | settlement on both GENERIC 3:1 and BRICK 2:1, querying BRICK     | 2 (best available wins)                  | no           |
