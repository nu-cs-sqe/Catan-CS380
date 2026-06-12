# BVA Analysis – `Game` Setup Phase

## Method under test: `Game` constructor / setup methods

The setup phase initializes the game with 3–4 players, determines turn order via dice rolls, and runs two rounds of settlement and road placement.

Setup placement is **per-player and position-driven** (Option A). For the
current setup player the caller supplies a chosen board `Vertex` and an incident
`Edge`; `Game.placeSetupSettlement(vertex, road, board, bank)` delegates to the
validated `TurnFlow.buildSetupSettlement` / `buildSetupRoad` primitives (distance
rule + connectivity, free of cost) and, in round two only, grants one resource
per tile adjacent to the new settlement (drawn from the `Bank`). The cursor walks
`turnOrder` in round one and the reverse order in round two; `getCurrentSetupPlayer`
exposes whose placement is next and `isSetupComplete` reports when both rounds are done.

---

### TC1 – Game accepts exactly 3 players
- **State of the system**: New Game with 3 players
- **Expected output**: `getNumberOfPlayers() == 3`
- **BVA note**: 3 is the minimum valid player count. Boundaries: 2 (invalid), 3 (valid)
- **Implemented**: [x]

### TC2 – Game accepts exactly 4 players
- **State of the system**: New Game with 4 players
- **Expected output**: `getNumberOfPlayers() == 4`
- **BVA note**: 4 is the maximum valid player count. Boundaries: 4 (valid), 5 (invalid)
- **Implemented**: [x]

### TC3 – Game rejects 2 players
- **State of the system**: Attempt to create Game with 2 players
- **Expected output**: Throws `IllegalArgumentException`
- **BVA note**: Boundary between invalid (2) and minimum valid (3)
- **Implemented**: [x]

### TC4 – Game rejects 5 players
- **State of the system**: Attempt to create Game with 5 players
- **Expected output**: Throws `IllegalArgumentException`
- **BVA note**: Boundary between maximum valid (4) and invalid (5)
- **Implemented**: [x]

### TC5 – Player with highest dice roll goes first
- **State of the system**: Game with stubbed dice; player 1 rolls highest
- **Expected output**: `getFirstPlayerIndex()` is 1
- **BVA note**: Boundary between the highest roll (first) and all other rolls (not first)
- **Implemented**: [x]

### TC6 – Turn order proceeds clockwise from the starting player
- **State of the system**: Game with 3 players; player 1 rolls highest
- **Expected output**: Turn order is [1, 2, 0]
- **BVA note**: Boundary at the last player wrapping back to the first
- **Implemented**: [x]

### TC7 – Tied dice rolls are re-rolled
- **State of the system**: Game with stubbed dice; two players tie, then one wins re-roll
- **Expected output**: The player who wins the re-roll goes first
- **BVA note**: Boundary between unique highest roll (resolved) and tied highest roll (must re-roll)
- **Implemented**: [x]

### TC8 – Round one: each player places 1 settlement in clockwise order
- **State of the system**: Game with 3 players after setup round one
- **Expected output**: Each player has exactly 1 settlement placed
- **BVA note**: Boundaries: 0 settlements (before), 1 (after round one), 2 (too many)
- **Implemented**: [x]

### TC9 – Round one: each player places 1 road in clockwise order
- **State of the system**: Game with 3 players after setup round one
- **Expected output**: Each player has exactly 1 road placed
- **BVA note**: Boundaries: 0 roads (before), 1 (after round one), 2 (too many)
- **Implemented**: [x]

### TC10 – Round two proceeds in reverse (counterclockwise) order
- **State of the system**: Game with 3 players; turn order [0, 1, 2]
- **Expected output**: Round two placement order is [2, 1, 0]
- **BVA note**: Boundary between clockwise (round one) and counterclockwise (round two)
- **Implemented**: [x]

### TC11 – After both rounds each player has 2 settlements
- **State of the system**: Game with 3 players after both setup rounds
- **Expected output**: Each player has exactly 2 settlements placed
- **BVA note**: Boundaries: 1 (after round one), 2 (after round two), 3 (too many)
- **Implemented**: [x]

### TC12 – After both rounds each player has 2 roads
- **State of the system**: Game with 3 players after both setup rounds
- **Expected output**: Each player has exactly 2 roads placed
- **BVA note**: Boundaries: 1 (after round one), 2 (after round two), 3 (too many)
- **Implemented**: [x]

### TC13 – Players receive resources only from second settlement
- **State of the system**: Real board (no-op shuffler); each player's round-one
  settlement grants nothing; round-two settlements border known single-resource
  tiles (pasture/forest/mountains)
- **Expected output**: After round one every player has 0 resources; after round
  two each player holds exactly the resource derived from the tiles adjacent to
  their second settlement
- **BVA note**: Boundary between 0 resource grants (first settlement) and 1 per
  adjacent hex (second settlement); resources are now derived from the board, not injected
- **Implemented**: [x]

### TC14 – Starting player begins the main game after setup
- **State of the system**: Full setup driven through `placeSetupSettlement`
- **Expected output**: `isSetupComplete()` is true and `getCurrentPlayerIndex()`
  is the player who rolled highest
- **BVA note**: The starting player (first in turn order) takes the first turn
- **Implemented**: [x]

### TC15 – Placing a setup settlement after setup is complete throws
- **State of the system**: Both setup rounds finished (`isSetupComplete()` true)
- **Expected output**: `placeSetupSettlement` throws `IllegalStateException`
- **BVA note**: Boundary between the last legal placement (round two, last player)
  and any placement beyond it
- **Implemented**: [x]

### TC16 – Setup cursor follows turn order, then reverses for round two
- **State of the system**: 3 players, turn order [1, 2, 0]
- **Expected output**: `getCurrentSetupPlayer()` yields players 1, 2, 0 across
  round one and 0, 2, 1 across round two
- **BVA note**: Boundary between round one (clockwise) and round two
  (counterclockwise), and the round-one → round-two transition
- **Implemented**: [x]