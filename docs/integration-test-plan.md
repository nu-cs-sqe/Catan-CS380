# Integration Test Plan

Integration tests use real classes and no mocks. They live in `src/test/java/integration/`.
They check that collaborators behave correctly once wired together, which the solitary and
sociable unit tests in `src/test/java/domain/` and `src/test/java/board/` do not cover.

## Conventions

- Package name is `integration`.
- No mocks. Only real domain and board objects.
- Each scenario asserts a cross-class invariant. That means state which holds only when both
  collaborators behave correctly together, not just the behaviour of one class on its own.

## Scenarios

| # | Scenario | Collaborators | Invariant | Status |
|---|----------|---------------|-----------|--------|
| 1 | Trade, maritime 4 to 1 | Bank and Player | Resources stay conserved between bank stock and player hand | Done, BankPlayerTradeIntegrationTest |
| 2 | Setup to Turn | Game, SetupController, Board | Board is built and starting resources dealt before the first turn | Planned |
| 3 | Turn to Win | Game, Player, TurnFlow | Reaching 10 VP on a player own turn ends the game | Planned |
| 4 | Robber on roll of 7 | Game, Player, Robber | Roll 7, discard half, move, steal leaves the total resource count unchanged | Planned |

## Scenario 1, Trade maritime 4 to 1, implemented

File [BankPlayerTradeIntegrationTest.java](../src/test/java/integration/BankPlayerTradeIntegrationTest.java)

A real Bank distributes 4 wood to a real Player, the way a dice roll would. The player then
trades 4 wood back to the bank for 1 brick. The decisive assertion is conservation. For every
resource, player hand plus bank stock equals the bank full starting stock of 19. This holds
only when Player and Bank agree on the bookkeeping.

Two cases.

1. `shouldMoveResourcesFromBankToPlayer_whenBankDistributesWood` covers the distribution leg.
2. `shouldConserveResourcesAcrossBankAndPlayer_whenTradingFourWoodForOneBrick` covers the full trade.

## Scenarios 2 to 4, planned

Tracked in [IntegrationTesting.md](adnan-pm-board/IntegrationTesting.md), section B. They wait on
consolidating the `board.Player` and `domain.Player` split before the multi-class setup and turn
flows can run end to end.
