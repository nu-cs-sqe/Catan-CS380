## Design summary

Development cards are stored as a **`Map<DevelopmentCard, Integer>` (card type → count held) field
directly on `domain.Player`**, mirroring the existing `Map<Resource, Integer> resources`. There is
**no separate hand class**. `knightsPlayed` stays on `Player` (a *played* count that persists after the
Knight returns to the Bank, distinct from cards currently *held*). The VP-card contribution to score is
**derived from the map** (`getDevelopmentCardCount(VICTORY_POINT)`), so the old `victoryPointDevCards`
int is removed. The "can't play a card the turn you bought it" rule is **enforced by `Game`**, not
`Player`, and is covered by Game's BVA — not here.

Resource names follow merged `domain.Resource`: `GENERIC, WOOD, BRICK, SHEEP, WHEAT, ORE`
(`GENERIC` is not tradeable and is rejected by the resource-taking effects).

---

## Solitary tests

### Method under test: `DevelopmentCard` category predicates (`isPlayableAction()` / `isVictoryPoint()` / `isReturnableToBank()`)

|      | System under test            | Expected output                                                  | Implemented? |
|------|------------------------------|-----------------------------------------------------------------|--------------|
| TC1  | KNIGHT                       | isPlayableAction true, isVictoryPoint false, isReturnable true   | no           |
| TC2  | ROAD_BUILDING                | isPlayableAction true, isVictoryPoint false, isReturnable true   | no           |
| TC3  | YEAR_OF_PLENTY               | isPlayableAction true, isVictoryPoint false, isReturnable true   | no           |
| TC4  | MONOPOLY                     | isPlayableAction true, isVictoryPoint false, isReturnable true   | no           |
| TC5  | VICTORY_POINT                | isPlayableAction false, isVictoryPoint true, isReturnable false  | no           |


### Method under test: `Player.getDevelopmentCardCount(DevelopmentCard card)`

|      | System under test                       | Expected output                              | Implemented? |
|------|-----------------------------------------|----------------------------------------------|--------------|
| TC6  | freshly constructed player              | 0 for every development-card type            | no           |
| TC7  | card = null                             | NullPointerException                         | no           |


### Method under test: `Player.buyDevelopmentCard(DevelopmentCard card)`

|      | System under test                       | Expected output                              | Implemented? |
|------|-----------------------------------------|----------------------------------------------|--------------|
| TC8  | card = null                             | NullPointerException                         | no           |
| TC9  | buy KNIGHT                              | getDevelopmentCardCount(KNIGHT) = 1          | no           |
| TC10 | buy KNIGHT then buy KNIGHT             | getDevelopmentCardCount(KNIGHT) = 2 (accum.) | no           |
| TC11 | buy VICTORY_POINT                       | getDevelopmentCardCount(VICTORY_POINT) = 1   | no           |


### Method under test: `Player.playDevelopmentCard(DevelopmentCard card)`

|      | System under test                                   | Expected output                                              | Implemented? |
|------|-----------------------------------------------------|-------------------------------------------------------------|--------------|
| TC12 | buy KNIGHT, play KNIGHT                              | returns KNIGHT; count(KNIGHT) = 0; knightsPlayed = 1         | no           |
| TC13 | play KNIGHT when none held                           | IllegalStateException; state unchanged                      | no           |
| TC14 | buy VICTORY_POINT, play VICTORY_POINT               | IllegalStateException (VP cards are never played)            | no           |
| TC15 | play null                                           | NullPointerException                                        | no           |
| TC16 | buy KNIGHT & MONOPOLY, play KNIGHT                   | count(KNIGHT) = 0, count(MONOPOLY) = 1 (only that type)     | no           |
| TC17 | buy MONOPOLY, play MONOPOLY                          | returns MONOPOLY; count = 0; knightsPlayed unchanged = 0    | no           |

> Note: the "cannot play a card bought this turn" rule is **not** asserted here — `Player` allows
> playing any held card; `Game` enforces the timing and owns those boundary cases in its own BVA.


### Method under test: `Player.getVictoryPoints()` — development-card contribution

|      | System under test                                   | Expected output                              | Implemented? |
|------|-----------------------------------------------------|----------------------------------------------|--------------|
| TC18 | buy VICTORY_POINT                                   | getVictoryPoints increases by 1              | no           |
| TC19 | buy KNIGHT (action card)                             | getVictoryPoints unchanged (not a VP card)   | no           |
| TC20 | buy 5 VICTORY_POINT                                  | VP contribution from dev cards = 5           | no           |


## Sociable tests

These model each action card's **effect**. With the merged single `domain.Player`, the effects are
**orchestrated by `Game`** (it holds the canonical `Player` objects and the `Bank`/`Robber`); they call
`player.playDevelopmentCard(card)` to remove the card, then resolve the effect against game state.
Note: `Vertex`/`Edge`/`Robber` return **defensive copies** of their `Player`, so an effect must mutate
the canonical `Player` objects held by `Game`, never a board/robber copy. Victory Point cards have no
playable effect (covered above). These are documented design — implement after Bank/Robber/Game integrate.

### Method under test: Knight effect — move robber, steal, Largest Army (collaborators: Game, Robber, Player)

|      | System under test                                              | Expected output                                                  | Implemented? |
|------|----------------------------------------------------------------|-----------------------------------------------------------------|--------------|
| TC21 | victim on target tile holds 1+ resources                       | robber on targetTile; 1 random resource moves victim → player    | no           |
| TC22 | victim on target tile holds 0 resources                        | robber moves; no resource stolen                                 | no           |
| TC23 | targetTile is the tile the robber already occupies             | IllegalArgumentException (robber must move)                      | no           |
| TC24 | target tile has no eligible opponents                          | robber moves; no steal (boundary: 0 eligible victims)           | no           |
| TC25 | player's knightsPlayed = 2, then plays a 3rd Knight            | knightsPlayed = 3 → eligible for Largest Army (threshold)        | no           |
| TC26 | player has 3 knights & Largest Army, opponent reaches 4         | Largest Army moves to opponent (boundary: strictly greater)     | no           |


### Method under test: Monopoly effect — take all of one resource from every opponent (collaborators: Game, other Players)

|      | System under test                                              | Expected output                                                  | Implemented? |
|------|----------------------------------------------------------------|-----------------------------------------------------------------|--------------|
| TC27 | resource = null                                                | NullPointerException                                             | no           |
| TC28 | opponents hold 0 of the named resource                         | player gains 0; opponents unchanged (lower boundary)            | no           |
| TC29 | two opponents hold 3 and 2 SHEEP                               | player gains 5 SHEEP; both opponents' SHEEP = 0                  | no           |
| TC30 | opponents hold mixed resources; player names SHEEP             | only SHEEP is taken; all other resource counts unchanged        | no           |
| TC31 | resource = GENERIC (non-tradeable)                            | IllegalArgumentException; no state changes                       | no           |


### Method under test: Year of Plenty effect — draw 2 resources from the bank (collaborators: Game, Bank)

|      | System under test                                              | Expected output                                                  | Implemented? |
|------|----------------------------------------------------------------|-----------------------------------------------------------------|--------------|
| TC32 | first = ORE, second = WHEAT, bank stocks both                  | player gains 1 ORE + 1 WHEAT; bank stock each −1                 | no           |
| TC33 | first = ORE, second = ORE, bank has ≥2 ORE                     | player gains 2 ORE; bank ORE −2 (same resource twice)           | no           |
| TC34 | bank holds exactly 1 of a requested resource                   | IllegalStateException; no resources moved (insufficient stock)  | no           |
| TC35 | first or second = null                                         | NullPointerException                                            | no           |
| TC36 | first or second = GENERIC (non-tradeable)                     | IllegalArgumentException; no resources moved                     | no           |


### Method under test: Road Building effect — place up to 2 free roads (collaborators: Game, Board, Player)

|      | System under test                                              | Expected output                                                  | Implemented? |
|------|----------------------------------------------------------------|-----------------------------------------------------------------|--------------|
| TC37 | player has ≥2 roads remaining and 2 legal edges                | 2 roads placed for free; no resources spent                     | no           |
| TC38 | player has exactly 1 road remaining                            | 1 road placed; second placement skipped (upper boundary)        | no           |
| TC39 | player has 0 roads remaining                                   | 0 roads placed (no-op boundary)                                 | no           |
| TC40 | a chosen edge is illegal (occupied / not connected)            | IllegalStateException; no road placed at that edge              | no           |
