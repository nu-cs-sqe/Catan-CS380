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
| TC59 | buy/hold one VICTORY_POINT development card                     | VP increases by 1 (a held VP card scores)    | yes           |


### Method under test: `placeSettlement(Vertex v)`

|      | System under test                                     | Expected output                               | Implemented? |
|------|-------------------------------------------------------|-----------------------------------------------|--------------|
| TC31 | fresh player                                          | 5 settlements / 4 cities / 15 roads remaining | yes          |
| TC32 | placed 5 settlements, place a 6th                     | IllegalStateException (cap boundary)          | yes          |
| TC39 | vertex = null                                         | NullPointerException                          | yes          |
| TC40 | vertex already has a settlement owned by this player  | IllegalStateException                         | yes          |


### Method under test: `upgradeSettlementToCity(Vertex v)`

|      | System under test                              | Expected output                                                                    | Implemented? |
|------|------------------------------------------------|------------------------------------------------------------------------------------|--------------|
| TC33 | 4 cities already on board, attempt a 5th       | IllegalStateException                                                              | yes          |
| TC35 | player has a settlement at vertex, upgrade it  | Settlement.isCity() = true; getRemainingCities() −1; getRemainingSettlements() +1  | yes          |
| TC36 | vertex has no player settlement on it          | IllegalStateException                                                              | yes          |
| TC41 | vertex = null                                  | NullPointerException                                                               | yes          |
| TC42 | settlement at vertex is already a city         | IllegalStateException                                                              | yes          |


### Method under test: `placeRoad(Edge e)`

|      | System under test                | Expected output       | Implemented? |
|------|----------------------------------|-----------------------|--------------|
| TC34 | placed 15 roads, place a 16th    | IllegalStateException | yes          |
| TC43 | edge = null                      | NullPointerException  | yes          |
| TC58 | edge already owned by a player   | IllegalStateException (cannot build on an occupied edge) | yes |


### Method under test: `playKnight()` / `getKnightsPlayed()`

|      | System under test            | Expected output                                  | Implemented? |
|------|------------------------------|--------------------------------------------------|--------------|
| TC37 | 0 knights played, play one   | knights = 1                                      | yes           |
| TC38 | 2 knights played, play one   | knights = 3 (Largest Army eligibility boundary)  | yes           |


### Method under test: `getDevelopmentCards()`

|      | System under test                              | Expected output                                      | Implemented? |
|------|------------------------------------------------|------------------------------------------------------|--------------|
| TC44 | freshly constructed player                     | empty list (size 0, lower boundary)                  | yes          |
| TC45 | hand holds [KNIGHT, MONOPOLY]                  | list = [KNIGHT, MONOPOLY] (order preserved)          | yes          |
| TC46 | mutate the returned list                       | player's internal list unchanged (defensive copy)    | yes          |


### Method under test: `addDevelopmentCard(DevelopmentCard card)` (append a single card)

|      | System under test                              | Expected output                                      | Implemented? |
|------|------------------------------------------------|------------------------------------------------------|--------------|
| TC47 | empty hand, add KNIGHT                         | hand = [KNIGHT] (appended to empty)                  | yes          |
| TC48 | hand [KNIGHT], add MONOPOLY                    | hand = [KNIGHT, MONOPOLY] (size 2, order preserved)  | yes          |
| TC49 | card = null                                    | NullPointerException                                 | yes          |
| TC50 | hand [KNIGHT], add KNIGHT                      | hand = [KNIGHT, KNIGHT] (duplicates retained)        | yes          |


### Method under test: `addDevelopmentCards(List<DevelopmentCard> cards)` (concatenate temporary turn list)

|      | System under test                              | Expected output                                      | Implemented? |
|------|------------------------------------------------|------------------------------------------------------|--------------|
| TC51 | empty hand, add [KNIGHT, ROAD_BUILDING]        | hand = [KNIGHT, ROAD_BUILDING] (appended)            | yes          |
| TC52 | hand [KNIGHT], add [MONOPOLY]                  | hand = [KNIGHT, MONOPOLY] (size 2, order preserved)  | yes          |
| TC53 | hand [KNIGHT], add [] (empty)                  | hand = [KNIGHT] (no change, boundary lower-valid)    | yes          |
| TC54 | add a list already containing duplicates       | duplicates retained (e.g. [KNIGHT, KNIGHT])          | yes          |
| TC55 | cards = null                                   | NullPointerException                                 | yes          |
| TC56 | cards contains a null element                  | NullPointerException                                 | yes          |
| TC57 | mutate the source list after adding            | player's internal list unchanged (defensive copy)    | yes          |