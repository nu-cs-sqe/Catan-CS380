# Feature: Player — Integration Testing

Status of integration / cross-class testing for the Player slice, ready to split into PM board sub-issues. Maps to §14 of `CATAN_A_GRADE_GUIDE.md` (Week 7 §4).

## What is done
- [x] Solitary BVA rows for Player written (TC1–TC38) in `docs/bva/player-bva.md`.
- [x] Sociable BVA rows added (TC39–TC61) covering Player against real `Vertex`, `Edge`, `Tile`, `Harbor`.
- [x] Failing tests written for Player constructor (TC1–TC6) and `addResource` (TC7–TC11) in `src/test/java/domain/Player.java`.
- [x] `Player`, `Resource`, `PlayerColor` stubs in `src/main/java/domain/`.

## What is remaining

### A. Player ↔ board sociable tests (BVA TC39–TC61)
Unit-level sociable tests that use real `board` collaborators. Still belong in `src/test/java/domain/`.
- [ ] TC39–TC43 `placeSettlementAt(Vertex)` against real Vertex and Settlement.
- [ ] TC44–TC47 `placeRoadAt(Edge)` against real Edge.
- [ ] TC48–TC51 `upgradeToCityAt(Vertex)` against real Vertex.
- [ ] TC52–TC56 `collectFromTile(Tile)` against real Tile and Vertex.
- [ ] TC57–TC61 `getTradeRate(Resource)` against real Harbor accessed through Vertex.
- [ ] Blocker. Resolve the `board.Player` vs `domain.Player` split before writing TC39+. One canonical Player class must exist for `Vertex.setOwner` and `Edge.setOwner` to accept.

### B. End-to-end integration tests (per Guide §14)
Real classes, no mocks. Place in `src/test/java/integration/`.
- [ ] Setup → Turn. Complete setup, run one full turn, assert state consistency.
- [ ] Turn → Win. Script turns until a Player reaches 10 VP, assert game ends.
- [ ] Robber. Roll 7 → discard → move robber → steal, end-to-end.
- [ ] Trade. Domestic two-player trade, then 4:1 maritime trade.

### C. Supporting work
- [ ] Write `docs/integration-test-plan.md` summarizing the four scenarios above (required by Guide §14).
- [ ] Confirm JaCoCo line ≥ 90% and branch ≥ 85% once Player implementation lands (Guide §8).
- [ ] Confirm PIT mutation score ≥ 80% on the `domain.*` and `board.*` packages.

## Acceptance Criteria
- [ ] All Player BVA rows (TC1–TC61) flipped to `yes` in `docs/bva/player-bva.md`.
- [ ] `./gradlew test` passes locally and in CI.
- [ ] The four §14 integration scenarios each have at least one test file in `src/test/java/integration/`.
- [ ] No mocks used inside the `integration/` package.
- [ ] `docs/integration-test-plan.md` exists and matches the actual test files.
- [ ] JaCoCo and PIT thresholds met.

## Suggested PM board sub-issues
1. `test(player): sociable tests for Vertex placement (TC39–TC43)`
2. `test(player): sociable tests for Edge placement (TC44–TC47)`
3. `test(player): sociable tests for city upgrade (TC48–TC51)`
4. `test(player): sociable tests for resource collection (TC52–TC56)`
5. `test(player): sociable tests for harbor trade rate (TC57–TC61)`
6. `chore: consolidate board.Player and domain.Player into one class`
7. `test(integration): Setup → Turn end-to-end`
8. `test(integration): Turn → Win end-to-end`
9. `test(integration): Robber roll-7 → discard → move → steal`
10. `test(integration): Trade (domestic 1:1 and maritime 4:1)`
11. `docs: write docs/integration-test-plan.md`
