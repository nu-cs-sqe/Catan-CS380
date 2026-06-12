## BVA Analysis – `TurnFlow`

### Class under test: `TurnFlow`

Manages everything that happens during a player's turn: rolling dice, resource distribution, robber mechanics, development cards (buying, pending, playing), maritime trade, longest road, largest army, victory point tracking, and win condition checking.

---

## Resource Production

### TC1 – Roll produces resources for player with settlement on matching tile
- **State of the system**: Player has settlement adjacent to FOREST tile with token 5; robber not on tile
- **Expected output**: Player receives 1 WOOD when 5 is rolled
- **BVA note**: Boundary between matching roll (produces) and non-matching (nothing)
- **Implemented**: [x]

### TC2 – Roll does not produce for player without settlement on matching tile
- **State of the system**: Player has settlement adjacent to tile with token 5; roll is 6
- **Expected output**: Player receives nothing
- **BVA note**: Boundary between matching token (produces) and non-matching (nothing)
- **Implemented**: [x]

### TC3 – City on matching tile yields 2 resources
- **State of the system**: Player has city adjacent to tile with matching token
- **Expected output**: Player receives 2 of that resource
- **BVA note**: Boundary between settlement (1 resource) and city (2 resources)
- **Implemented**: [x]

### TC4 – Robber blocks resource production on its tile
- **State of the system**: Robber on tile with token 5; player has settlement adjacent
- **Expected output**: Player receives nothing when 5 is rolled
- **BVA note**: Boundary between robber absent (produces) and present (blocked)
- **Implemented**: [x]

---

## Rolling a 7 / Robber

### TC5 – Rolling 7 produces no resources
- **State of the system**: Dice returns 7; players have settlements on board
- **Expected output**: No player receives any resources
- **BVA note**: 7 is the boundary between resource rolls (2–6, 8–12) and robber trigger
- **Implemented**: [x]

### TC6 – Rolling 7: player with 8+ cards must discard half
- **State of the system**: Player has 8 resource cards; 7 is rolled
- **Expected output**: Player must discard 4 cards
- **BVA note**: Boundary between 7 cards (no discard) and 8 (must discard 4)
- **Implemented**: [x]

### TC7 – Rolling 7: player with 7 cards does not discard
- **State of the system**: Player has 7 resource cards; 7 is rolled
- **Expected output**: Player discards nothing
- **BVA note**: Boundary between 7 (safe) and 8 (must discard)
- **Implemented**: [x]

### TC8 – Rolling 7: odd card count rounds down
- **State of the system**: Player has 9 resource cards; 7 is rolled
- **Expected output**: Player must discard 4 (9/2 rounded down)
- **BVA note**: Boundary between even (exact half) and odd (round down)
- **Implemented**: [x]

### TC9 – Robber must move to a different tile
- **State of the system**: Robber on tile (0,0); player tries to move to (0,0)
- **Expected output**: Throws `IllegalArgumentException`
- **BVA note**: Boundary between same tile (invalid) and different tile (valid)
- **Implemented**: [x]

### TC10 – Robber can move to any other tile including desert
- **State of the system**: Robber on tile (0,0); player moves to desert tile
- **Expected output**: Move succeeds
- **BVA note**: Desert is a valid target; boundary between producing and non-producing tiles
- **Implemented**: [x]

### TC11 – Steal 1 resource from victim with resources
- **State of the system**: Robber moved; victim has settlement adjacent and holds resources
- **Expected output**: Victim loses 1 resource, current player gains 1
- **BVA note**: Boundary between 0 resources (nothing to steal) and 1+ (steal occurs)
- **Implemented**: [x]

### TC12 – Steal from victim with 0 resources does nothing
- **State of the system**: Robber moved; victim has settlement adjacent but 0 resources
- **Expected output**: No resources change hands
- **BVA note**: Boundary at 0 — steal attempted but nothing available
- **Implemented**: [x]

### TC13 – Cannot steal from yourself
- **State of the system**: Current player tries to steal from themselves
- **Expected output**: Throws `IllegalArgumentException`
- **BVA note**: Boundary between valid victim (different player) and invalid (self)
- **Implemented**: [x]

---

## Development Cards – Buying

### TC14 – Buy dev card with exact resources; card enters pending list
- **State of the system**: Player has exactly 1 ORE, 1 WHEAT, 1 SHEEP; deck non-empty
- **Expected output**: Card added to pending list; resources deducted
- **BVA note**: Boundary between insufficient resources (cannot buy) and exact cost (can buy)
- **Implemented**: [x]

### TC15 – Cannot buy dev card with insufficient resources
- **State of the system**: Player has 1 ORE, 1 WHEAT, 0 SHEEP
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between exact cost (valid) and 1 short (invalid)
- **Implemented**: [x]

### TC16 – Cannot buy dev card when deck is empty
- **State of the system**: Deck has 0 cards; player has sufficient resources
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between deck size 0 (cannot draw) and 1 (can draw)
- **Implemented**: [x]

### TC17 – Pending dev card cannot be played this turn
- **State of the system**: Player buys dev card; immediately tries to play it
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between cards in hand (playable) and cards in pending (not playable)
- **Implemented**: [x]

### TC18 – Pending dev card moves to player hand after endTurn
- **State of the system**: Player bought a dev card; endTurn called
- **Expected output**: Card removed from pending; added to player's dev card hand
- **BVA note**: Boundary between mid-turn (card locked) and end-of-turn (card available)
- **Implemented**: [x]

---

## Development Cards – Playing

### TC19 – Player plays first dev card this turn; succeeds
- **State of the system**: Player has dev card in hand; devCardPlayedThisTurn = false
- **Expected output**: Card effect executes; devCardPlayedThisTurn set to true
- **BVA note**: Boundary between 0 cards played (allowed) and 1 played (blocks further)
- **Implemented**: [x]

### TC20 – Cannot play second dev card same turn
- **State of the system**: Player already played one dev card this turn
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between 1 card played (blocked) and 0 (allowed)
- **Implemented**: [x]

### TC21 – Cannot play VICTORY_POINT card
- **State of the system**: Player has VICTORY_POINT card; attempts to play it
- **Expected output**: Throws `IllegalArgumentException`
- **BVA note**: Boundary between playable cards (KNIGHT, MONOPOLY, etc.) and passive (VICTORY_POINT)
- **Implemented**: [x]

### TC22 – KNIGHT: moves robber, steals, increments knights, checks largest army
- **State of the system**: Player plays KNIGHT; robber moves to new tile; victim adjacent
- **Expected output**: Robber moves; victim loses 1 resource; knightsPlayed incremented; updateLargestArmy called
- **BVA note**: Knight combines robber movement with army tracking
- **Implemented**: [x]

### TC23 – MONOPOLY: takes all of named resource from all other players
- **State of the system**: Player plays MONOPOLY naming WHEAT; opponents have 2 and 3
- **Expected output**: Active player gains 5 WHEAT; opponents drop to 0
- **BVA note**: Boundary between opponents having 0 (nothing) and >0 (full transfer)
- **Implemented**: [x]

### TC24 – MONOPOLY with GENERIC resource throws
- **State of the system**: Player plays MONOPOLY naming GENERIC
- **Expected output**: Throws `IllegalArgumentException`
- **BVA note**: Boundary between valid resource types and GENERIC (not a real resource)
- **Implemented**: [x]

### TC25 – ROAD_BUILDING: places 2 free roads
- **State of the system**: Player has 2+ roads remaining and a network to extend;
  plays ROAD_BUILDING naming two connected edges
- **Expected output**: Both roads placed successfully (free, no resources spent)
- **BVA note**: Boundary between 1 road remaining (cannot place pair) and 2 (can);
  the free roads are still subject to the connectivity rule
- **Implemented**: [x]

### TC26 – ROAD_BUILDING with 0 roads remaining throws
- **State of the system**: Player has 0 roads remaining
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between 0 roads (blocked) and 1+ (can place)
- **Implemented**: [x]

### TC27 – YEAR_OF_PLENTY: player receives 2 resources from bank
- **State of the system**: Player plays YEAR_OF_PLENTY requesting WOOD and ORE; bank has both
- **Expected output**: Player gains 1 WOOD and 1 ORE
- **BVA note**: Boundary between bank having 0 (throws) and 1+ (succeeds)
- **Implemented**: [x]

### TC28 – YEAR_OF_PLENTY: bank has 0 of requested resource throws
- **State of the system**: Player requests ORE; bank has 0
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between stock 0 (cannot give) and 1 (can give)
- **Implemented**: [x]

### TC29 – YEAR_OF_PLENTY: same resource twice; bank has 2+; succeeds
- **State of the system**: Player requests WOOD twice; bank has 2+
- **Expected output**: Player gains 2 WOOD
- **BVA note**: Boundary between bank stock 1 (insufficient) and 2 (sufficient)
- **Implemented**: [x]

---

## Maritime Trade

### TC30 – Maritime trade at exact harbor rate succeeds
- **State of the system**: Player has 2:1 WOOD harbor; gives 2 WOOD for 1 BRICK
- **Expected output**: Player loses 2 WOOD, gains 1 BRICK
- **BVA note**: Boundary between below rate (invalid) and exact rate (valid)
- **Implemented**: [x]

### TC31 – Maritime trade below best rate throws
- **State of the system**: Player's best rate for WOOD is 2; gives 1
- **Expected output**: Throws `IllegalArgumentException`
- **BVA note**: Boundary between 1 (invalid) and 2 (valid minimum)
- **Implemented**: [x]

### TC32 – Maritime trade same resource give and receive throws
- **State of the system**: Player trades WOOD for WOOD
- **Expected output**: Throws `IllegalArgumentException`
- **BVA note**: Boundary between different resources (valid) and same (invalid)
- **Implemented**: [x]

### TC33 – Maritime trade when bank has 0 of receive resource throws
- **State of the system**: Bank has 0 ORE; player attempts to receive ORE
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between bank stock 0 (blocked) and 1 (allowed)
- **Implemented**: [x]

---

## Victory Points and Win Condition

### TC34 – getVictoryPoints includes largest army bonus
- **State of the system**: Player holds largest army
- **Expected output**: VP includes +2 for largest army
- **BVA note**: Boundary between holding bonus (+2) and not (no addition)
- **Implemented**: [x]

### TC35 – getVictoryPoints includes longest road bonus
- **State of the system**: Player holds longest road
- **Expected output**: VP includes +2 for longest road
- **BVA note**: Boundary between holding bonus (+2) and not (no addition)
- **Implemented**: [x]

### TC36 – checkWin returns true at exactly 10 VP
- **State of the system**: Current player has exactly 10 VP
- **Expected output**: Game ends
- **BVA note**: Boundary between 9 (continues) and 10 (ends)
- **Implemented**: [x]

### TC37 – checkWin returns false at 9 VP
- **State of the system**: Current player has 9 VP
- **Expected output**: Game continues
- **BVA note**: Boundary between 9 (below threshold) and 10 (at threshold)
- **Implemented**: [x]

### TC38 – checkWin called after every VP-changing action
- **State of the system**: Player at 9 VP builds a settlement,
  reaching 10 VP
- **Expected output**: `isGameOver()` is true immediately after the
  build, not deferred to end of turn
- **BVA note**: Win check is per-action, not per-turn; `buildSettlement`
  (TC44) triggers the check, as do `updateLargestArmy`/`updateLongestRoad`
- **Implemented**: [x]

---

## End Turn

### TC39 – endTurn resets devCardPlayedThisTurn to false
- **State of the system**: Player played dev card this turn; endTurn called
- **Expected output**: devCardPlayedThisTurn reset to false
- **BVA note**: Boundary between mid-turn (blocked) and post-endTurn (reset)
- **Implemented**: [x]

### TC40 – endTurn flushes pending dev cards to player hand
- **State of the system**: Player bought dev card this turn; endTurn called
- **Expected output**: Pending list empty; card in player hand
- **BVA note**: Boundary between mid-turn (locked) and end-of-turn (promoted)
- **Implemented**: [x]

### TC41 – endTurn advances to next player
- **State of the system**: Current player is index 0 in turn order
- **Expected output**: Current player becomes index 1
- **BVA note**: Boundary between current and next in clockwise order
- **Implemented**: [x]

### TC42 – endTurn wraps from last player to first
- **State of the system**: Current player is last in turn order
- **Expected output**: Current player wraps to first in turn order
- **BVA note**: Boundary between last player (wraps) and others (increments)
- **Implemented**: [x]

### TC43 – Cannot endTurn after game is over
- **State of the system**: Game is over
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between active game (can end turn) and ended game (cannot)
- **Implemented**: [x]

---

## Building Settlements

### TC44 – Build settlement with exact resources succeeds
- **State of the system**: Player has 1 WOOD, 1 BRICK, 1 SHEEP, 1 WHEAT; valid vertex
- **Expected output**: Settlement placed; resources deducted; checkWin called
- **BVA note**: Boundary between insufficient resources (cannot build) and exact cost (can build)
- **Implemented**: [x]

### TC45 – Build settlement with insufficient resources throws
- **State of the system**: Player has 1 WOOD, 1 BRICK, 1 SHEEP, 0 WHEAT
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between exact cost (valid) and 1 short (invalid)
- **Implemented**: [x]

### TC46 – Cannot build settlement on occupied vertex
- **State of the system**: Vertex already has a settlement
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between unoccupied (valid) and occupied (invalid)
- **Implemented**: [x]

### TC47 – Cannot build settlement violating distance rule
- **State of the system**: Adjacent vertex has a settlement
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between all neighbors empty (valid) and one neighbor occupied (invalid)
- **Implemented**: [x]

### TC48 – Cannot build settlement without adjacent road
- **State of the system**: Vertex has no adjacent road owned by player
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between 0 adjacent roads (invalid) and 1+ (valid)
- **Implemented**: [x]

### TC49 – Cannot build settlement with 0 pieces remaining
- **State of the system**: Player has placed all 5 settlements (none upgraded to city)
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between 0 remaining (blocked) and 1+ (allowed)
- **Implemented**: [x]

---

## Building Cities

### TC50 – Upgrade settlement to city with exact resources succeeds
- **State of the system**: Player has 3 ORE, 2 WHEAT; valid vertex with player's settlement
- **Expected output**: Settlement upgraded to city; resources deducted; checkWin called
- **BVA note**: Boundary between insufficient resources (cannot upgrade) and exact cost (can upgrade)
- **Implemented**: [x]

### TC51 – Upgrade settlement to city with insufficient resources throws
- **State of the system**: Player has 2 ORE, 2 WHEAT
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between exact cost (valid) and 1 short (invalid)
- **Implemented**: [x]

### TC52 – Cannot upgrade vertex without player's settlement
- **State of the system**: Vertex has no settlement or has another player's settlement
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between player's settlement (valid) and no settlement (invalid)
- **Implemented**: [x]

### TC53 – Cannot upgrade with 0 city pieces remaining
- **State of the system**: Player has placed all 4 cities
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between 0 remaining (blocked) and 1+ (allowed)
- **Implemented**: [x]

### TC54 – Upgrading city frees a settlement piece
- **State of the system**: Player has 5 settlements, 0 remaining; upgrades one to city
- **Expected output**: Player now has 1 remaining settlement piece
- **BVA note**: Boundary between 0 settlement pieces (blocked from building) and 1 (freed by upgrade)
- **Implemented**: [x]

---

## Building Roads

### TC55 – Build road with exact resources succeeds
- **State of the system**: Player has 1 WOOD, 1 BRICK; valid edge adjacent to player's network
- **Expected output**: Road placed; resources deducted; longest road updated; checkWin called
- **BVA note**: Boundary between insufficient resources (cannot build) and exact cost (can build)
- **Implemented**: [x]

### TC56 – Build road with insufficient resources throws
- **State of the system**: Player has 1 WOOD, 0 BRICK
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between exact cost (valid) and 1 short (invalid)
- **Implemented**: [x]

### TC57 – Cannot build road with 0 pieces remaining
- **State of the system**: Player has placed all 15 roads
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between 0 remaining (blocked) and 1+ (allowed)
- **Implemented**: [x]

### TC58 – Cannot build road disconnected from player's network
- **State of the system**: Edge is not adjacent to any road, settlement, or
  city owned by the player; player can afford the road
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between 0 adjacent owned roads/buildings (invalid)
  and 1+ (valid); a road must extend the player's own network
- **Implemented**: [x]

### TC59 – ROAD_BUILDING roads must connect to the player's network
- **State of the system**: Player plays ROAD_BUILDING naming edges not adjacent
  to any road, settlement, or city they own
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Free roads are still subject to connectivity; boundary between
  0 adjacent owned roads/buildings (invalid) and 1+ (valid)
- **Implemented**: [x]

---

## Setup Placement

### TC60 – Setup settlement is free and needs no adjacent road
- **State of the system**: Player with no resources and no roads places a setup
  settlement on an empty vertex that respects the distance rule
- **Expected output**: Settlement placed; no resources spent; no road required
- **BVA note**: Boundary between main-phase placement (requires a road + cost)
  and setup placement (free, no road)
- **Implemented**: [x]

### TC61 – Setup settlement violating the distance rule throws
- **State of the system**: An adjacent vertex already holds a settlement
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: The distance rule still applies during setup; boundary between
  all neighbors empty (valid) and one neighbor occupied (invalid)
- **Implemented**: [x]

### TC62 – Setup road is free and must connect to the player's settlement
- **State of the system**: Player places a setup road on an edge touching their
  setup settlement while holding no resources
- **Expected output**: Road placed; no resources spent
- **BVA note**: Boundary between main-phase road (requires cost) and setup road
  (free, still connected)
- **Implemented**: [ ]