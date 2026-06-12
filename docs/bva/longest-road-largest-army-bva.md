# BVA Analysis – Longest Road & Largest Army

## Methods under test: tracking of special cards in `Game`

Determines which player holds the Longest Road and Largest Army special cards (2 VP each).

---

## Largest Army

### TC1 – No player has Largest Army with fewer than 3 knights
- **State of the system**: All players have 2 or fewer knights played
- **Expected output**: No player has Largest Army
- **BVA note**: Boundary between 2 knights (not enough) and 3 (minimum for Largest Army)
- **Implemented**: [x]

### TC2 – First player to play 3 knights gets Largest Army
- **State of the system**: Player 0 plays 3 knights, others have fewer
- **Expected output**: Player 0 has Largest Army
- **BVA note**: 3 is the minimum threshold. Boundary: 2 (no army), 3 (gets it)
- **Implemented**: [x]

### TC3 – Another player with more knights takes Largest Army
- **State of the system**: Player 0 has 3 knights and Largest Army; Player 1 plays 4 knights
- **Expected output**: Player 1 has Largest Army, Player 0 loses it
- **BVA note**: Boundary between tied count (no change) and strictly more (takes it)
- **Implemented**: [x]

### TC4 – Tied knight count does not change Largest Army holder
- **State of the system**: Player 0 has 3 knights and Largest Army; Player 1 plays 3 knights
- **Expected output**: Player 0 still has Largest Army
- **BVA note**: Boundary between equal (no change) and strictly more (takes it)
- **Implemented**: [x]

## Longest Road

### TC5 – No player has Longest Road with fewer than 5 segments
- **State of the system**: All players have 4 or fewer continuous road segments
- **Expected output**: No player has Longest Road
- **BVA note**: Boundary between 4 segments (not enough) and 5 (minimum for Longest Road)
- **Implemented**: [x]

### TC6 – First player to build 5 continuous road segments gets Longest Road
- **State of the system**: Player 0 has 5 connected roads, others have fewer
- **Expected output**: Player 0 has Longest Road
- **BVA note**: 5 is the minimum threshold. Boundary: 4 (no road), 5 (gets it)
- **Implemented**: [x]

### TC7 – Another player with a longer road takes Longest Road
- **State of the system**: Player 0 has 5 roads and Longest Road; Player 1 builds 6 continuous roads
- **Expected output**: Player 1 has Longest Road, Player 0 loses it
- **BVA note**: Boundary between equal length (no change) and strictly longer (takes it)
- **Implemented**: [x]

### TC8 – Tied road length does not change Longest Road holder
- **State of the system**: Player 0 has 5 roads and Longest Road; Player 1 builds 5 continuous roads
- **Expected output**: Player 0 still has Longest Road
- **BVA note**: Boundary between equal (no change) and strictly longer (takes it)
- **Implemented**: [x]

### TC9 – Only the longest branch counts, not total roads
- **State of the system**: Player has 7 roads but they fork; longest branch is 4
- **Expected output**: Player's longest road is 4, not 7
- **BVA note**: Boundary between counting forks (incorrect) and single longest branch (correct)
- **Implemented**: [x]

### TC10 – Opponent settlement breaks a road
- **State of the system**: Player 0 has 6 continuous roads; opponent builds settlement in the middle
- **Expected output**: Player 0's longest road is recalculated as the longer of the two halves
- **BVA note**: Boundary between unbroken road (full length) and broken road (split)
- **Implemented**: [x]

### TC11 – Incumbent keeps Longest Road on a tie regardless of player index
- **State of the system**: Player 2 (a higher index) holds Longest Road with 5
  roads; player 0 (a lower index) then also reaches 5 roads
- **Expected output**: Player 2 keeps Longest Road; the title does not jump to the
  lower-index player on a tie
- **BVA note**: Exposes the index-order bug — the recompute must seed the current
  holder's length so only a strictly longer road takes the title (TC8 only covered
  a lower-index incumbent, which masks this)
- **Implemented**: [ ]