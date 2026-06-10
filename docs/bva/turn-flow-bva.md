# BVA Analysis – `Game` Turn Flow

## Methods under test: turn-related methods in `Game`

Manages a single turn: roll dice, distribute resources, check for winner, and advance to the next player.

---

### TC1 – Rolling dice produces resources for players with settlements on matching hexes
- **State of the system**: Game after setup; player has settlement adjacent to hex with number 6
- **Expected output**: When 6 is rolled, player receives 1 resource from that hex
- **BVA note**: Boundary between matching roll (produces) and non-matching roll (no production)
- **Implemented**: [ ]

### TC2 – Rolling dice does not produce resources for players without settlements on matching hexes
- **State of the system**: Game after setup; player has no settlement adjacent to hex with number 6
- **Expected output**: When 6 is rolled, player receives nothing
- **BVA note**: Boundary between having an adjacent settlement (produces) and not having one (nothing)
- **Implemented**: [ ]

### TC3 – Rolling a 7 produces no resources for any player
- **State of the system**: Game after setup; dice roll returns 7
- **Expected output**: No player receives any resources
- **BVA note**: Boundary between 6 (normal production) and 7 (no production, robber activates)
- **Implemented**: [ ]

### TC4 – Turn advances to next player in clockwise order
- **State of the system**: Game with 3 players; current player is index 0
- **Expected output**: After advancing, current player is index 1
- **BVA note**: Boundary between current player and next player in turn order
- **Implemented**: [ ]

### TC5 – Turn wraps around from last player to first player
- **State of the system**: Game with 3 players; current player is the last in turn order
- **Expected output**: After advancing, current player wraps to first in turn order
- **BVA note**: Boundary between last player (wraps) and all others (increments)
- **Implemented**: [ ]

### TC6 – Game detects winner when current player reaches 10 VP
- **State of the system**: Current player has 10 victory points after building
- **Expected output**: `checkWinner()` returns true
- **BVA note**: Boundary between 9 VP (no win) and 10 VP (win)
- **Implemented**: [ ]

### TC7 – Game does not declare winner at 9 VP
- **State of the system**: Current player has 9 victory points
- **Expected output**: `checkWinner()` returns false
- **BVA note**: Boundary between 9 VP (no win) and 10 VP (win)
- **Implemented**: [ ]

### TC8 – Game does not declare winner if non-current player reaches 10 VP
- **State of the system**: Non-current player has 10 VP
- **Expected output**: `checkWinner()` returns false
- **BVA note**: Boundary between current player winning (valid) and other player winning (not yet)
- **Implemented**: [ ]

### TC9 – Game is over after winner is declared
- **State of the system**: Current player reached 10 VP
- **Expected output**: `isGameOver()` returns true
- **BVA note**: Boundary between game in progress (false) and game ended (true)
- **Implemented**: [ ]

### TC10 – Cannot advance turn after game is over
- **State of the system**: Game is over
- **Expected output**: `advanceTurn()` throws `IllegalStateException`
- **BVA note**: Boundary between active game (can advance) and ended game (cannot)
- **Implemented**: [ ]