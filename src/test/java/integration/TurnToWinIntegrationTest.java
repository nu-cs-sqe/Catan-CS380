package integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import board.Vertex;
import domain.Bank;
import domain.Player;
import domain.PlayerColor;
import domain.Resource;
import domain.TurnFlow;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TurnToWinIntegrationTest {

  private static final int WIN_VP = 10;

  private List<Player> players;
  private TurnFlow turnFlow;
  private Player leader;

  @BeforeEach
  void setUp() {
    players = Arrays.asList(
        new Player("Alice", PlayerColor.RED),
        new Player("Bob", PlayerColor.BLUE),
        new Player("Carol", PlayerColor.PINK));
    turnFlow = new TurnFlow(players, new Bank(cards -> { }));
    leader = players.get(0);
  }

  @Test
  void shouldEndGame_whenABuildActionPushesLeaderToTenVictoryPoints() {
    giveVictoryPointCards(leader, WIN_VP - 1);
    giveSettlementCost(leader);
    assertFalse(turnFlow.isGameOver());

    turnFlow.buildSettlement(leader, new Vertex("win-vertex"));

    assertAll(
        () -> assertTrue(turnFlow.isGameOver()),
        () -> assertTrue(turnFlow.checkWin(0)),
        () -> assertEquals(WIN_VP, turnFlow.getVictoryPoints(0)));
  }

  @Test
  void shouldKeepGameRunning_whenABuildActionLeavesLeaderBelowTen() {
    giveVictoryPointCards(leader, WIN_VP - 2);
    giveSettlementCost(leader);

    turnFlow.buildSettlement(leader, new Vertex("nine-vertex"));

    assertAll(
        () -> assertFalse(turnFlow.isGameOver()),
        () -> assertEquals(WIN_VP - 1, turnFlow.getVictoryPoints(0)));
  }

  private void giveVictoryPointCards(Player player, int count) {
    for (int i = 0; i < count; i++) {
      player.addVictoryPointDevCard();
    }
  }

  private void giveSettlementCost(Player player) {
    player.addResource(Resource.WOOD, 1);
    player.addResource(Resource.BRICK, 1);
    player.addResource(Resource.SHEEP, 1);
    player.addResource(Resource.WHEAT, 1);
  }
}
