# BVA Analysis – `Game` Setup Phase

## Method under test: `Game` constructor / `setupPhase()`

The setup phase initializes the game with 3–4 players, determines turn order via dice rolls, and executes two rounds of settlement and road placement.

---

### TC1 – Game accepts exactly 3 players
- **State of the system**: New Game with 3 players
- **Expected output**: `getPlayers().size() == 3`
- **BVA note**: 3 is the minimum valid player count. Boundaries: 2 (invalid), 3 (valid)

### TC2 – Game accepts exactly 4 players
- **State of the system**: New Game with 4 players
- **Expected output**: `getPlayers().size() == 4`
- **BVA note**: 4 is the maximum valid player count. Boundaries: 4 (valid), 5 (invalid)

### TC3 – Game rejects 2 players
- **State of the system**: Attempt to create Game with 2 players
- **Expected output**: Throws `IllegalArgumentException`
- **BVA note**: Boundary between invalid (2) and minimum valid (3)

### TC4 – Game rejects 5 players
- **State of the system**: Attempt to create Game with 5 players
- **Expected output**: Throws `IllegalArgumentException`
- **BVA note**: Boundary between maximum valid (4) and invalid (5)

### TC5 – Player with highest dice roll goes first
- **State of the system**: Game with stubbed dice; player 2 rolls highest
- **Expected output**: `getCurrentPlayer()` is player 2
- **BVA note**: Boundary between the highest roll (first) and all other rolls (not first)

### TC6 – Turn order proceeds clockwise from the starting player
- **State of the system**: Game with 3 players; player 1 rolls highest
- **Expected output**: Turn order is [player1, player2, player0] (clockwise wrap)
- **BVA note**: Boundary at the last player wrapping back to the first

### TC7 – Tied dice rolls are re-rolled
- **State of the system**: Game with stubbed dice; two players tie on first roll, then one wins on re-roll
- **Expected output**: The player who wins the re-roll goes first
- **BVA note**: Boundary between unique highest roll (resolved) and tied highest roll (must re-roll)

### TC8 – Round one: each player places 1 settlement in clockwise order
- **State of the system**: Game with 3 players after setup round one
- **Expected output**: Each player has exactly 1 settlement placed
- **BVA note**: Boundaries: 0 settlements (before round one), 1 settlement (after round one), 2 (too many for round one)

### TC9 – Round one: each player places 1 road in clockwise order
- **State of the system**: Game with 3 players after setup round one
- **Expected output**: Each player has exactly 1 road placed
- **BVA note**: Boundaries: 0 roads (before round one), 1 road (after round one), 2 (too many for round one)

### TC10 – Round two proceeds in reverse (counterclockwise) order
- **State of the system**: Game with 3 players; turn order [P0, P1, P2]
- **Expected output**: Round two placement order is [P2, P1, P0]
- **BVA note**: Boundary between clockwise (round one) and counterclockwise (round two)

### TC11 – Round two: each player places 1 more settlement
- **State of the system**: Game with 3 players after both setup rounds
- **Expected output**: Each player has exactly 2 settlements placed
- **BVA note**: Boundaries: 1 settlement (after round one), 2 settlements (after round two), 3 (too many)

### TC12 – Round two: each player places 1 more road
- **State of the system**: Game with 3 players after both setup rounds
- **Expected output**: Each player has exactly 2 roads placed
- **BVA note**: Boundaries: 1 road (after round one), 2 roads (after round two), 3 (too many)

### TC13 – Players receive resources only from second settlement
- **State of the system**: Game with stubbed board; player's second settlement borders ore and brick hexes
- **Expected output**: Player has 1 ore and 1 brick; no resources from first settlement
- **BVA note**: Boundary between 0 resource grants (first settlement) and 1 grant per adjacent hex (second settlement)

### TC14 – Starting player begins the main game after setup
- **State of the system**: Game after full setup phase complete
- **Expected output**: `getCurrentPlayer()` is the player who rolled highest
- **BVA note**: The starting player (last to place in round two) takes the first turn