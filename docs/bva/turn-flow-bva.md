# BVA Analysis – `Game` Turn Flow

## Methods under test: turn-related methods in `Game`

Manages a single turn: roll dice, distribute resources, trade, build, play/buy development cards, check for winner, and advance to the next player.

---

## Resource Production

### TC1 – Rolling dice produces resources for players with settlements on matching hexes
- **State of the system**: Game after setup; player has settlement adjacent to hex with number 5; no robber on that hex
- **Expected output**: When 5 is rolled, player receives 1 resource from that hex
- **BVA note**: Boundary between matching roll (produces) and non-matching roll (no production)
- **Implemented**: [x]

### TC2 – Rolling dice does not produce resources for players without settlements on matching hexes
- **State of the system**: Game after setup; player has no settlement adjacent to hex with number 6
- **Expected output**: When 6 is rolled, player receives nothing
- **BVA note**: Boundary between having an adjacent settlement (produces) and not having one (nothing)
- **Implemented**: [x]

### TC3 – Rolling a 7 produces no resources for any player
- **State of the system**: Game after setup; dice roll returns 7
- **Expected output**: No player receives any resources
- **BVA note**: Boundary between 6 (normal production) and 7 (no production, robber activates)
- **Implemented**: [x]

### TC3b – Robber blocks resource production on its tile
- **State of the system**: Player has settlement adjacent to hex with number 5; robber is on that hex
- **Expected output**: When 5 is rolled, player receives nothing from that hex
- **BVA note**: Boundary between robber absent (produces) and robber present (blocked)
- **Implemented**: [x]

## Turn Advancement

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

## Win Condition

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

## Turn Phase Enforcement

### TC11 – Cannot trade before rolling dice
- **State of the system**: Turn just started, dice not rolled yet
- **Expected output**: Attempting to trade throws `IllegalStateException`
- **BVA note**: Boundary between ROLL phase (trade forbidden) and TRADE_BUILD phase (trade allowed)
- **Implemented**: [ ]

### TC12 – Cannot build before rolling dice
- **State of the system**: Turn just started, dice not rolled yet
- **Expected output**: Attempting to build throws `IllegalStateException`
- **BVA note**: Boundary between ROLL phase (build forbidden) and TRADE_BUILD phase (build allowed)
- **Implemented**: [ ]

### TC13 – Can trade and build in any order after rolling
- **State of the system**: Dice rolled, in TRADE_BUILD phase
- **Expected output**: Player can trade then build, or build then trade, without error
- **BVA note**: Boundary between ROLL phase (restricted) and TRADE_BUILD phase (open)
- **Implemented**: [ ]

### TC14 – Cannot roll dice twice in one turn
- **State of the system**: Dice already rolled this turn
- **Expected output**: Attempting to roll again throws `IllegalStateException`
- **BVA note**: Boundary between first roll (allowed) and second roll (forbidden)
- **Implemented**: [ ]

## Development Cards

### TC15 – Can play a development card before rolling dice
- **State of the system**: Turn just started, player holds a knight card from a previous turn
- **Expected output**: Playing the card succeeds
- **BVA note**: Boundary between dev card play (allowed any time) and trade/build (only after roll)
- **Implemented**: [ ]

### TC16 – Can play a development card after rolling dice
- **State of the system**: Dice rolled, player holds a knight card from a previous turn
- **Expected output**: Playing the card succeeds
- **BVA note**: Dev card play is allowed in both ROLL and TRADE_BUILD phases
- **Implemented**: [ ]

### TC17 – Cannot play more than 1 development card per turn
- **State of the system**: Player already played a dev card this turn
- **Expected output**: Attempting to play another throws `IllegalStateException`
- **BVA note**: Boundary between 0 cards played (allowed) and 1 card played (forbidden)
- **Implemented**: [ ]

### TC18 – Cannot play a development card bought this turn
- **State of the system**: Player bought a dev card this turn, tries to play it immediately
- **Expected output**: Throws `IllegalStateException`
- **BVA note**: Boundary between cards from previous turns (playable) and cards bought this turn (not playable)
- **Implemented**: [ ]

### TC19 – Cannot buy a development card before rolling dice
- **State of the system**: Turn just started, dice not rolled yet
- **Expected output**: Attempting to buy a dev card throws `IllegalStateException`
- **BVA note**: Buying is part of the build phase; boundary between ROLL phase (forbidden) and TRADE_BUILD phase (allowed)
- **Implemented**: [ ]

### TC20 – Can buy a development card after rolling dice
- **State of the system**: Dice rolled, player has enough resources
- **Expected output**: Buying the card succeeds, card added to player's hand
- **BVA note**: Boundary between insufficient resources (cannot buy) and sufficient resources (can buy)
- **Implemented**: [ ]