package integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import board.Board;
import board.Edge;
import board.Shuffler;
import board.Vertex;
import domain.Bank;
import domain.Game;
import domain.Player;
import domain.PlayerColor;
import domain.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SetupToTurnIntegrationTest {

  private static final int SETUP_SPOTS = 6;
  private static final int STOCK_PER_RESOURCE = 19;
  private static final int RESOURCE_KINDS = 5;
  private static final int FULL_BANK_STOCK = STOCK_PER_RESOURCE * RESOURCE_KINDS;

  private Board board;
  private Bank bank;
  private List<Player> players;
  private Game game;

  @BeforeEach
  void setUp() {
    board = newBoard();
    bank = new Bank(cards -> { });
    players = threePlayers();
    game = new Game(players);
    runSnakeSetup(game, board, bank, pickSpacedResourceVertices(board, SETUP_SPOTS));
  }

  @Test
  void shouldMoveStartingResourcesFromBankToPlayers_whenSnakeSetupCompletes() {
    int dealt = totalPlayerResources(players);
    assertAll(
        () -> assertTrue(game.isSetupComplete()),
        () -> assertTrue(dealt > 0),
        () -> assertEquals(FULL_BANK_STOCK - dealt, totalBankStock(bank)));
  }

  @Test
  void shouldAdvanceToSecondPlayerInTurnOrder_whenFirstTurnEnds() {
    int[] order = game.getTurnOrder();
    assertEquals(order[0], game.getCurrentPlayerIndex());

    game.endTurn();

    assertEquals(order[1], game.getCurrentPlayerIndex());
  }

  private void runSnakeSetup(Game theGame, Board theBoard, Bank theBank, List<Vertex> spots) {
    int cursor = 0;
    while (!theGame.isSetupComplete()) {
      Vertex vertex = spots.get(cursor++);
      Edge road = freeIncidentEdge(theBoard, vertex);
      theGame.placeSetupSettlement(vertex, road, theBoard, theBank);
    }
  }

  private List<Vertex> pickSpacedResourceVertices(Board theBoard, int count) {
    List<Vertex> chosen = new ArrayList<>();
    for (Vertex vertex : theBoard.getVertices()) {
      if (chosen.size() == count) {
        break;
      }
      if (bordersResource(vertex) && isSpacedFrom(theBoard, chosen, vertex)) {
        chosen.add(vertex);
      }
    }
    return chosen;
  }

  private boolean isSpacedFrom(Board theBoard, List<Vertex> chosen, Vertex candidate) {
    for (Vertex picked : chosen) {
      if (adjacent(theBoard, candidate.getId(), picked.getId())) {
        return false;
      }
    }
    return true;
  }

  private boolean bordersResource(Vertex vertex) {
    return !vertex.getAdjacentTiles().isEmpty()
        && vertex.getAdjacentTiles().stream()
            .anyMatch(tile -> tileToResource(tile.getTileType()) != null);
  }

  private Resource tileToResource(board.TileType type) {
    switch (type) {
      case FOREST: return Resource.WOOD;
      case PASTURE: return Resource.SHEEP;
      case FIELDS: return Resource.WHEAT;
      case HILLS: return Resource.BRICK;
      case MOUNTAINS: return Resource.ORE;
      default: return null;
    }
  }

  private boolean adjacent(Board theBoard, String idA, String idB) {
    for (Edge edge : theBoard.getEdges()) {
      String[] ends = edge.getId().split("\\|");
      if ((ends[0].equals(idA) && ends[1].equals(idB))
          || (ends[0].equals(idB) && ends[1].equals(idA))) {
        return true;
      }
    }
    return false;
  }

  private Edge freeIncidentEdge(Board theBoard, Vertex vertex) {
    for (Edge edge : theBoard.getEdges()) {
      if (edge.getOwner() != null) {
        continue;
      }
      String[] ends = edge.getId().split("\\|");
      if (ends[0].equals(vertex.getId()) || ends[1].equals(vertex.getId())) {
        return edge;
      }
    }
    throw new IllegalStateException("no free incident edge");
  }

  private int totalPlayerResources(List<Player> hands) {
    int total = 0;
    for (Player player : hands) {
      for (Resource resource : Resource.values()) {
        if (resource != Resource.GENERIC) {
          total += player.getResourceCount(resource);
        }
      }
    }
    return total;
  }

  private int totalBankStock(Bank theBank) {
    int total = 0;
    for (Resource resource : Resource.values()) {
      if (resource != Resource.GENERIC) {
        total += theBank.getStock(resource);
      }
    }
    return total;
  }

  private Board newBoard() {
    Shuffler noOp = new Shuffler() {
      @Override
      public <T> void shuffle(List<T> list) {
      }
    };
    Board created = new Board(noOp);
    created.create();
    return created;
  }

  private List<Player> threePlayers() {
    return Arrays.asList(
        new Player("Alice", PlayerColor.RED),
        new Player("Bob", PlayerColor.BLUE),
        new Player("Carol", PlayerColor.PINK));
  }
}
