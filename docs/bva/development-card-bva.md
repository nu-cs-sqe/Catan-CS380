## Solitary tests

### Method under test: `DevelopmentCard` category predicates (`isPlayableAction()` / `isVictoryPoint()` / `isReturnableToBank()`)

|      | System under test            | Expected output                                                  | Implemented? |
|------|------------------------------|-----------------------------------------------------------------|--------------|
| TC1  | KNIGHT                       | isPlayableAction true, isVictoryPoint false, isReturnable true   | no           |
| TC2  | ROAD_BUILDING                | isPlayableAction true, isVictoryPoint false, isReturnable true   | no           |
| TC3  | YEAR_OF_PLENTY               | isPlayableAction true, isVictoryPoint false, isReturnable true   | no           |
| TC4  | MONOPOLY                     | isPlayableAction true, isVictoryPoint false, isReturnable true   | no           |
| TC5  | VICTORY_POINT                | isPlayableAction false, isVictoryPoint true, isReturnable false  | no           |


### Method under test: `DevelopmentCardHand()` constructor

|      | System under test            | Expected output                                        | Implemented? |
|------|------------------------------|--------------------------------------------------------|--------------|
| TC6  | freshly constructed hand     | 0 VP cards, 0 total/playable of every card type        | no           |


### Method under test: `buy(DevelopmentCard card)`

|      | System under test                          | Expected output                                          | Implemented? |
|------|--------------------------------------------|----------------------------------------------------------|--------------|
| TC7  | card = null                                | NullPointerException                                      | no           |
| TC8  | buy KNIGHT (action card)                   | totalCount(KNIGHT) = 1, playableCount(KNIGHT) = 0 (locked)| no           |
| TC9  | buy VICTORY_POINT                          | victoryPointCardCount = 1 immediately                    | no           |
| TC10 | buy KNIGHT then buy KNIGHT (same turn)     | totalCount(KNIGHT) = 2 (accumulates)                     | no           |


### Method under test: `activateBoughtCards()`

|      | System under test                                   | Expected output                          | Implemented? |
|------|-----------------------------------------------------|------------------------------------------|--------------|
| TC11 | buy KNIGHT, then activateBoughtCards()              | playableCount(KNIGHT) = 1                | no           |
| TC12 | activateBoughtCards() on empty hand                | no change, no exception                  | no           |


### Method under test: `canPlay(DevelopmentCard card)`

|      | System under test                                   | Expected output                          | Implemented? |
|------|-----------------------------------------------------|------------------------------------------|--------------|
| TC13 | buy KNIGHT, before activate                          | canPlay(KNIGHT) = false (buy-delay)      | no           |
| TC14 | buy KNIGHT, after activateBoughtCards()             | canPlay(KNIGHT) = true                   | no           |
| TC15 | KNIGHT never bought                                  | canPlay(KNIGHT) = false                  | no           |
| TC16 | buy + activate VICTORY_POINT                         | canPlay(VICTORY_POINT) = false           | no           |


### Method under test: `play(DevelopmentCard card)`

|      | System under test                                   | Expected output                                         | Implemented? |
|------|-----------------------------------------------------|---------------------------------------------------------|--------------|
| TC17 | buy KNIGHT, activate, play KNIGHT                    | returns KNIGHT; totalCount = 0, playableCount = 0       | no           |
| TC18 | buy KNIGHT, play before activate (locked)           | IllegalStateException; totalCount(KNIGHT) unchanged = 1 | no           |
| TC19 | play KNIGHT never held                               | IllegalStateException                                   | no           |
| TC20 | buy + activate VICTORY_POINT, play VICTORY_POINT    | IllegalStateException (VP cards are never played)        | no           |
| TC21 | play null                                           | NullPointerException                                    | no           |


### Method under test: count accounting (`totalCount` / `playableCount` / `victoryPointCardCount`)

|      | System under test                                                        | Expected output                                              | Implemented? |
|------|--------------------------------------------------------------------------|-------------------------------------------------------------|--------------|
| TC22 | buy KNIGHT + activate (turn 1), buy KNIGHT (turn 2)                       | totalCount(KNIGHT) = 2, playableCount(KNIGHT) = 1           | no           |
| TC23 | buy KNIGHT & MONOPOLY, activate, play KNIGHT                              | totalCount(KNIGHT) = 0, totalCount(MONOPOLY) = 1            | no           |


## Sociable tests

These model each action card's **effect**. They involve collaborators (Robber, Bank,
Board, other players), so they are sociable rather than solitary. Victory Point cards
have no playable effect (they only add VP) and are covered above.

### Method under test: `playKnight(targetTile, victim)` — move robber, steal, Largest Army (collaborators: Robber, Player)

|      | System under test                                              | Expected output                                                  | Implemented? |
|------|----------------------------------------------------------------|-----------------------------------------------------------------|--------------|
| TC24 | victim on target tile holds 1+ resources                       | robber on targetTile; 1 random resource moves victim → player    | no           |
| TC25 | victim on target tile holds 0 resources                        | robber moves; no resource stolen                                 | no           |
| TC26 | targetTile is the tile the robber already occupies             | IllegalArgumentException (robber must move)                      | no           |
| TC27 | no victim selected but a tile has no eligible opponents         | robber moves; no steal (boundary: 0 eligible victims)           | no           |
| TC28 | player's knightsPlayed = 2, then plays a 3rd                   | knightsPlayed = 3 → eligible for Largest Army (threshold)        | no           |
| TC29 | player has 3 knights & Largest Army, opponent reaches 4         | Largest Army moves to opponent (boundary: strictly greater)     | no           |


### Method under test: `playMonopoly(resource)` — take all of one resource from every opponent (collaborators: other Players)

|      | System under test                                              | Expected output                                                  | Implemented? |
|------|----------------------------------------------------------------|-----------------------------------------------------------------|--------------|
| TC30 | resource = null                                                | NullPointerException                                             | no           |
| TC31 | opponents hold 0 of the named resource                         | player gains 0; opponents unchanged (lower boundary)            | no           |
| TC32 | two opponents hold 3 and 2 WOOL                                | player gains 5 WOOL; both opponents' WOOL = 0                    | no           |
| TC33 | opponents hold mixed resources; player names WOOL              | only WOOL is taken; all other resource counts unchanged         | no           |


### Method under test: `playYearOfPlenty(first, second)` — draw 2 resources from the bank (collaborator: Bank)

|      | System under test                                              | Expected output                                                  | Implemented? |
|------|----------------------------------------------------------------|-----------------------------------------------------------------|--------------|
| TC34 | first = ORE, second = GRAIN, bank stocks both                  | player gains 1 ORE + 1 GRAIN; bank stock each −1                 | no           |
| TC35 | first = ORE, second = ORE, bank has ≥2 ORE                     | player gains 2 ORE; bank ORE −2 (same resource twice)           | no           |
| TC36 | bank holds exactly 1 of a requested resource                   | IllegalStateException; no resources moved (insufficient stock)  | no           |
| TC37 | first or second = null                                         | NullPointerException                                            | no           |


### Method under test: `playRoadBuilding()` — place up to 2 free roads (collaborators: Board, Player)

|      | System under test                                              | Expected output                                                  | Implemented? |
|------|----------------------------------------------------------------|-----------------------------------------------------------------|--------------|
| TC38 | player has ≥2 roads remaining and 2 legal edges                | 2 roads placed for free; no resources spent                     | no           |
| TC39 | player has exactly 1 road remaining                            | 1 road placed; second placement skipped (upper boundary)        | no           |
| TC40 | player has 0 roads remaining                                   | 0 roads placed (no-op boundary)                                 | no           |
| TC41 | a chosen edge is illegal (occupied / not connected)            | IllegalStateException; no road placed at that edge              | no           |
