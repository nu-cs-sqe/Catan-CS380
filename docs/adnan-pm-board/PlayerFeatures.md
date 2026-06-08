# Feature: Player

## User Story
As a Catan player, I want my name, color, hand of resources, buildings, and victory points tracked correctly throughout the game, so that I always know what I can afford to build and whether I have won.

### Acceptance Criteria
- [ ] A Player must be constructed with a non-blank name and a non-null color.
- [ ] A new Player starts with 0 of every resource, 0 victory points, and a full piece inventory (5 settlements, 4 cities, 15 roads).
- [ ] Resources can be added and removed in non-negative amounts; removing more than the player holds is rejected without changing state.
- [ ] `hasResources(cost)` returns true only when the player holds at least every resource amount listed in the cost.
- [ ] On a roll of 7, a player holding more than 7 cards must discard exactly `floor(hand / 2)`; a player holding 7 or fewer discards nothing.
- [ ] Placing a settlement, city, or road past the rulebook cap is rejected.
- [ ] Victory points equal `settlements + 2·cities + 2·(longest road) + 2·(largest army) + VP dev cards`; the player has won iff VP ≥ 10.
- [ ] Playing a knight increments the knights-played count (used for Largest Army eligibility at 3+).

## Use Case 1: Create a Player
- **Actor:** Game Setup
- **Preconditions:**
  - A name and color have been chosen for this seat.
- **Main Flow:**
  1. System calls `new Player(name, color)`.
  2. System verifies the player reports 0 resources, 0 VP, and full piece inventory.
- **Alternate Flows:**
  - 1.a Name is null or blank → constructor throws and the seat is re-prompted.
  - 1.b Color is null → constructor throws and the seat is re-prompted.
- **Postconditions:**
  - The Player is ready to receive starting resources from the setup phase.

## Use Case 2: Spend Resources to Build a Settlement
- **Actor:** Player (on their turn)
- **Preconditions:**
  - It is the player's turn.
  - The player has at least 1 settlement remaining in inventory.
- **Main Flow:**
  1. System asks the Player if `hasResources({BRICK:1, LUMBER:1, WOOL:1, GRAIN:1})`.
  2. System calls `removeResource` for each of the four resources.
  3. System calls `placeSettlement()`, decrementing the inventory and incrementing VP by 1.
- **Alternate Flows:**
  - 1.a `hasResources` returns false → action is rejected, hand unchanged.
  - 3.a Settlement cap of 5 already reached → `placeSettlement()` throws; the previously removed resources must be restored by the caller.
- **Postconditions:**
  - The player's VP and piece inventory reflect the new settlement; the resource hand is reduced by the settlement cost.

## Use Case 3: Detect a Winner
- **Actor:** Turn Controller
- **Preconditions:**
  - A player has just completed a VP-affecting action (build, dev card play, or longest-road / largest-army award).
- **Main Flow:**
  1. Controller calls `getVictoryPoints()` to read the current total.
  2. Controller calls `hasWon()`.
  3. If `hasWon()` is true, the game ends and this player is declared the winner.
- **Alternate Flows:**
  - 2.a `hasWon()` returns false → the controller continues with normal turn flow.
  - 1.a Longest Road or Largest Army was just revoked from this player → VP drops below 10 before the check; no win is declared.
- **Postconditions:**
  - Game state either advances to the next turn or transitions to a terminal "winner declared" state.
