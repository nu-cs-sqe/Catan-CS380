# BVA Analysis – `Game` Setup Phase

## Method under test: `Game` constructor / setup methods

The setup phase initializes the game with 3–4 players, determines turn order via dice rolls, and executes two rounds of settlement and road placement.

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
- **State of the system**: Game with stubbed board; player's second settlement borders ore and brick
- **Expected output**: Player has 1 ore and 1 brick; no resources from first settlement
- **BVA note**: Boundary between 0 resource grants (first settlement) and 1 per adjacent hex (second settlement)
- **Implemented**: [x]

### TC14 – Starting player begins the main game after setup
- **State of the system**: Game after full setup phase complete
- **Expected output**: `getCurrentPlayerIndex()` is the player who rolled highest
- **BVA note**: The starting player (last to place in round two) takes the first turn
- **Implemented**: [ ]