# Player Integration Testing

Status of cross-class testing for the Player slice, ready to split into PM board sub-issues.

## What is done
- [x] Solitary BVA rows for Player written, TC1 to TC38, in `docs/bva/player-bva.md`.
- [x] Sociable BVA rows added, TC39 to TC61, covering Player against real `Vertex`, `Edge`, `Tile`, and `Harbor`.
- [x] Failing tests written for the Player constructor, TC1 to TC6, and `addResource`, TC7 to TC11.
- [x] `Player`, `Resource`, and `PlayerColor` stubs in `src/main/java/domain/`.

## What is remaining

### A. Player and board sociable tests, TC39 to TC61
Unit-level sociable tests that use real `board` collaborators. These still belong in `src/test/java/domain/`.
- [ ] TC39 to TC43, `placeSettlementAt(Vertex)` against a real Vertex and Settlement.
- [ ] TC44 to TC47, `placeRoadAt(Edge)` against a real Edge.
- [ ] TC48 to TC51, `upgradeToCityAt(Vertex)` against a real Vertex.
- [ ] TC52 to TC56, `collectFromTile(Tile)` against a real Tile and Vertex.
- [ ] TC57 to TC61, `getTradeRate(Resource)` against a real Harbor reached through a Vertex.
- [ ] Blocker. Resolve the `board.Player` versus `domain.Player` split before writing TC39 and beyond. One canonical Player class must exist for `Vertex.setOwner` and `Edge.setOwner` to accept.

### B. End-to-end integration tests
Real classes, no mocks, placed in `src/test/java/integration/`.
- [ ] Setup to Turn. Complete setup, run one full turn, and assert state consistency.
- [ ] Turn to Win. Script turns until a Player reaches 10 VP, then assert the game ends.
- [ ] Robber. Roll 7, discard, move the robber, and steal, end to end.
- [x] Trade, maritime 4 to 1. `integration/BankPlayerTradeIntegrationTest` checks the Bank and Player conservation invariant. A domestic two-player trade is still pending.

### C. Supporting work
- [x] Write `docs/integration-test-plan.md` summarizing the four scenarios above.
- [ ] Confirm JaCoCo line coverage at 90% or more and branch at 85% or more once the Player implementation lands.
- [ ] Confirm the PIT mutation score is 80% or more on the `domain.*` and `board.*` packages.

## Acceptance Criteria
- [ ] All Player BVA rows, TC1 to TC61, flipped to `yes` in `docs/bva/player-bva.md`.
- [ ] `./gradlew test` passes locally and in CI.
- [ ] Each of the four integration scenarios has at least one test file in `src/test/java/integration/`.
- [ ] No mocks used inside the `integration/` package.
- [ ] `docs/integration-test-plan.md` exists and matches the actual test files.
- [ ] JaCoCo and PIT thresholds met.

## Suggested PM board sub-issues
1. Sociable tests for Vertex placement, TC39 to TC43.
2. Sociable tests for Edge placement, TC44 to TC47.
3. Sociable tests for city upgrade, TC48 to TC51.
4. Sociable tests for resource collection, TC52 to TC56.
5. Sociable tests for harbor trade rate, TC57 to TC61.
6. Consolidate `board.Player` and `domain.Player` into one class.
7. Setup to Turn integration test.
8. Turn to Win integration test.
9. Robber roll 7, discard, move, steal integration test.
10. Trade integration test, domestic 1 to 1 and maritime 4 to 1.
11. Write `docs/integration-test-plan.md`.
