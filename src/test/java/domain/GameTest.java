package domain;

import board.Board;
import board.Shuffler;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class GameTest {

  private static final int TWO_PLAYERS = 2;
  private static final int FIVE_PLAYERS = 5;

  // Six pairwise non-adjacent vertices (distance-rule safe) with an incident
  // edge each: indices 0-2 are round one, 3-5 are round two. Round-two vertices
  // border single-resource tiles: 0,-8=SHEEP  -2,-8=WOOD  2,8=ORE.
  private static final String[] SETUP_VERTICES = {
    "-5,1", "5,1", "0,8", "0,-8", "-2,-8", "2,8"
  };
  private static final String[] SETUP_ROADS = {
    "-5,1|-4,2", "4,2|5,1", "-1,7|0,8", "-1,-7|0,-8", "-3,-7|-2,-8", "1,7|2,8"
  };

  // TC1 – Game accepts exactly 3 players
  @Test
  public void testGameAccepts3Players() {
    List<Player> players = createPlayers(3);
    Game game = new Game(players);
    Assertions.assertEquals(3, game.getPlayers().size());
  }

  // TC2 – Game accepts exactly 4 players
  @Test
  public void testGameAccepts4Players() {
    List<Player> players = createPlayers(4);
    Game game = new Game(players);
    Assertions.assertEquals(4, game.getPlayers().size());
  }

  // TC3 – Game rejects 2 players
  @Test
  public void testGameRejects2Players() {
    List<Player> players = createPlayers(TWO_PLAYERS);
    Assertions.assertThrows(IllegalArgumentException.class, () -> new Game(players));
  }

  // TC4 – Game rejects 5 players
  @Test
  public void testGameRejects5Players() {
    List<Player> players = createPlayers(FIVE_PLAYERS);
    Assertions.assertThrows(IllegalArgumentException.class, () -> new Game(players));
  }

  // TC5 – Player with highest dice roll goes first
  @Test
  public void testHighestRollGoesFirst() {
    int[] rolls = {5, 10, 3};
    List<Player> players = createPlayers(3);
    Game game = new Game(players, stubDiceRoller(rolls));
    Assertions.assertEquals(1, game.getFirstPlayerIndex());
  }

  // TC6 – Turn order proceeds clockwise from starting player
  @Test
  public void testTurnOrderClockwiseFromStartingPlayer() {
    int[] rolls = {4, 9, 6};
    List<Player> players = createPlayers(3);
    Game game = new Game(players, stubDiceRoller(rolls));
    Assertions.assertArrayEquals(new int[] {1, 2, 0}, game.getTurnOrder());
  }

  // TC7 – Tied dice rolls are re-rolled
  @Test
  public void testTiedRollsAreReRolled() {
    int[] rolls = {8, 8, 5, 3, 7};
    List<Player> players = createPlayers(3);
    Game game = new Game(players, stubDiceRoller(rolls));
    Assertions.assertEquals(1, game.getFirstPlayerIndex());
  }

  // TC8 – Round one: each player places 1 settlement
  @Test
  public void testRoundOneEachPlayerPlaces1Settlement() {
    List<Player> players = createPlayers(3);
    Game game = new Game(players, stubDiceRoller(new int[] {7, 5, 3}));
    Board board = createBoard();
    Bank bank = createBank();
    for (int i = 0; i < 3; i++) {
      placeSetup(game, board, bank, i);
    }
    for (Player player : game.getPlayers()) {
      Assertions.assertEquals(4, player.getRemainingSettlements());
    }
  }

  // TC9 – Round one: each player places 1 road
  @Test
  public void testRoundOneEachPlayerPlaces1Road() {
    List<Player> players = createPlayers(3);
    Game game = new Game(players, stubDiceRoller(new int[] {7, 5, 3}));
    Board board = createBoard();
    Bank bank = createBank();
    for (int i = 0; i < 3; i++) {
      placeSetup(game, board, bank, i);
    }
    for (Player player : game.getPlayers()) {
      Assertions.assertEquals(14, player.getRemainingRoads());
    }
  }

  // TC10 – Round two placement order is the reverse of round one
  @Test
  public void testRoundTwoOrderIsReversed() {
    List<Player> players = createPlayers(3);
    Game game = new Game(players, stubDiceRoller(new int[] {7, 5, 3}));
    Assertions.assertArrayEquals(new int[] {2, 1, 0}, game.getRoundTwoOrder());
  }

  // TC11 – After both rounds each player has 2 settlements
  @Test
  public void testAfterBothRoundsEachPlayerHas2Settlements() {
    List<Player> players = createPlayers(3);
    Game game = new Game(players, stubDiceRoller(new int[] {7, 5, 3}));
    Board board = createBoard();
    Bank bank = createBank();
    runFullSetup(game, board, bank);
    for (Player player : game.getPlayers()) {
      Assertions.assertEquals(3, player.getRemainingSettlements());
    }
  }

  // TC12 – After both rounds each player has 2 roads
  @Test
  public void testAfterBothRoundsEachPlayerHas2Roads() {
    List<Player> players = createPlayers(3);
    Game game = new Game(players, stubDiceRoller(new int[] {7, 5, 3}));
    Board board = createBoard();
    Bank bank = createBank();
    runFullSetup(game, board, bank);
    for (Player player : game.getPlayers()) {
      Assertions.assertEquals(13, player.getRemainingRoads());
    }
  }

  // TC13 – Resources granted only from the second settlement, derived from tiles
  @Test
  public void testPlayersReceiveResourcesFromSecondSettlementOnly() {
    List<Player> players = createPlayers(3);
    Game game = new Game(players, stubDiceRoller(new int[] {7, 5, 3}));
    Board board = createBoard();
    Bank bank = createBank();

    for (int i = 0; i < 3; i++) {
      placeSetup(game, board, bank, i);
    }
    for (Player player : game.getPlayers()) {
      for (Resource resource : Resource.values()) {
        Assertions.assertEquals(0, player.getResourceCount(resource));
      }
    }

    Resource[] expected = {Resource.SHEEP, Resource.WOOD, Resource.ORE};
    Player[] placer = new Player[3];
    for (int i = 0; i < 3; i++) {
      placer[i] = game.getCurrentSetupPlayer();
      placeSetup(game, board, bank, 3 + i);
    }
    for (int i = 0; i < 3; i++) {
      Assertions.assertEquals(1, placer[i].getResourceCount(expected[i]));
    }
  }

  // TC14 – Starting player begins the main game after setup
  @Test
  public void testStartingPlayerBeginsAfterSetup() {
    List<Player> players = createPlayers(3);
    Game game = new Game(players, stubDiceRoller(new int[] {4, 9, 6}));
    Board board = createBoard();
    Bank bank = createBank();
    runFullSetup(game, board, bank);
    Assertions.assertTrue(game.isSetupComplete());
    Assertions.assertEquals(1, game.getCurrentPlayerIndex());
  }

  // TC15 – Placing a setup settlement after setup is complete throws
  @Test
  public void testPlaceSetupAfterCompletionThrows() {
    List<Player> players = createPlayers(3);
    Game game = new Game(players, stubDiceRoller(new int[] {7, 5, 3}));
    Board board = createBoard();
    Bank bank = createBank();
    runFullSetup(game, board, bank);
    Assertions.assertTrue(game.isSetupComplete());
    Assertions.assertThrows(IllegalStateException.class,
        () -> game.placeSetupSettlement(board.getVertex("4,4"),
            board.getEdge("4,2|4,4"), board, bank));
  }

  // TC16 – Setup cursor follows turn order, then reverses for round two
  @Test
  public void testSetupCursorFollowsRoundOrder() {
    List<Player> players = createPlayers(3);
    Game game = new Game(players, stubDiceRoller(new int[] {4, 9, 6}));
    Board board = createBoard();
    Bank bank = createBank();
    int[] roundOne = {1, 2, 0};
    int[] roundTwo = {0, 2, 1};
    for (int i = 0; i < 3; i++) {
      Assertions.assertSame(players.get(roundOne[i]),
          game.getCurrentSetupPlayer());
      placeSetup(game, board, bank, i);
    }
    for (int i = 0; i < 3; i++) {
      Assertions.assertSame(players.get(roundTwo[i]),
          game.getCurrentSetupPlayer());
      placeSetup(game, board, bank, 3 + i);
    }
    Assertions.assertTrue(game.isSetupComplete());
  }

  // TC17 – getCurrentPlayerIndex tracks the active turn, not a fixed start
  @Test
  public void testGetCurrentPlayerIndexTracksTurns() {
    List<Player> players = createPlayers(3);
    Game game = new Game(players, stubDiceRoller(new int[] {4, 9, 6}));
    Board board = createBoard();
    Bank bank = createBank();
    runFullSetup(game, board, bank);

    // turn order is [1, 2, 0]; the starting player begins
    Assertions.assertEquals(1, game.getCurrentPlayerIndex());
    game.endTurn();
    Assertions.assertEquals(2, game.getCurrentPlayerIndex());
    game.endTurn();
    Assertions.assertEquals(0, game.getCurrentPlayerIndex());
    game.endTurn();
    Assertions.assertEquals(1, game.getCurrentPlayerIndex());
  }

  private void placeSetup(Game game, Board board, Bank bank, int i) {
    game.placeSetupSettlement(board.getVertex(SETUP_VERTICES[i]),
        board.getEdge(SETUP_ROADS[i]), board, bank);
  }

  private void runFullSetup(Game game, Board board, Bank bank) {
    for (int i = 0; i < SETUP_VERTICES.length; i++) {
      placeSetup(game, board, bank, i);
    }
  }

  private Board createBoard() {
    Board board = new Board(new Shuffler() {
      @Override
      public <T> void shuffle(List<T> list) {
      }
    });
    board.create();
    return board;
  }

  private Bank createBank() {
    return new Bank(cards -> { });
  }

  private List<Player> createPlayers(int count) {
    PlayerColor[] colors = PlayerColor.values();
    Player[] players = new Player[count];
    for (int i = 0; i < count; i++) {
      players[i] = new Player("Player" + i, colors[i % colors.length]);
    }
    return Arrays.asList(players);
  }

  private DiceRoller stubDiceRoller(int[] rolls) {
    return new DiceRoller() {
      private int index = 0;

      @Override
      public int roll() {
        return rolls[index++];
      }
    };
  }
}
