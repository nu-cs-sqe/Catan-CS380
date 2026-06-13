# Integration Test Plan

Integration tests use real classes and no mocks. They live in `src/test/java/integration/`.
Three main features are covered.

---

## Feature under test: Trade – `Bank` with `Player`
File `BankPlayerTradeIntegrationTest`

### TC1 – Bank distributes wood to a player
- **State of the system**: Fresh bank and player, bank distributes 4 wood and the player takes it
- **Expected output**: Player holds 4 wood, bank stock drops to 15, hand plus stock stays at 19
- **Implemented**: [x]

### TC2 – Maritime 4 to 1 trade conserves resources
- **State of the system**: Player holds 4 wood, trades 4 wood back to the bank for 1 brick
- **Expected output**: Player has 0 wood and 1 brick, bank wood returns to 19 and brick is 18, every resource conserved across bank and player
- **Implemented**: [x]

---

## Feature under test: Setup to Turn – `Game` with `Board`, `Bank`, `Player`
File `SetupToTurnIntegrationTest`

### TC3 – Starting resources come from the bank
- **State of the system**: A real game runs both snake-order setup rounds on a real board
- **Expected output**: Total resources held by players is above 0 and equals exactly what the bank lost
- **Implemented**: [x]

### TC4 – First turn advances to the next player
- **State of the system**: Setup is complete, the first player ends the turn
- **Expected output**: The current player becomes the second player in the turn order
- **Implemented**: [x]

---

## Feature under test: Turn to Win – `TurnFlow` with `Player`
File `TurnToWinIntegrationTest`

### TC5 – Reaching 10 victory points ends the game
- **State of the system**: Leader holds 9 victory points, then builds one settlement
- **Expected output**: Game is over, the leader has 10 victory points
- **Implemented**: [x]

### TC6 – Staying below 10 keeps the game running
- **State of the system**: Leader holds 8 victory points, then builds one settlement
- **Expected output**: Game is not over, the leader has 9 victory points
- **Implemented**: [x]
