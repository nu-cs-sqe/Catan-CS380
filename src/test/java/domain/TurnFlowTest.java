package domain;

import board.Board;
import board.Shuffler;
import board.Vertex;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import board.Robber;
public class TurnFlowTest {

    // TC1 – Rolling dice produces resources for players with
    // settlements on matching hexes
    @Test
    public void testRollProducesResourcesForAdjacentSettlement() {
        List<Player> players = createPlayers();
        Game game = createGame(players);
        Board board = createBoard();
        Robber robber = new Robber();

        Vertex vertex = board.getVertex("-3,1");
        vertex.setOwner(players.get(0));

        game.rollForProduction(board, robber, 5);

        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.WOOD));
    }

    // TC2 – Rolling dice does not produce resources for players
    // without settlements on matching hexes
    @Test
    public void testRollDoesNotProduceForNonAdjacentPlayer() {
        List<Player> players = createPlayers();
        Game game = createGame(players);
        Board board = createBoard();
        Robber robber = new Robber();

        // Player 0 has settlement on vertex "-3,1" (adjacent to
        // FOREST token 5), but we roll 6 instead
        Vertex vertex = board.getVertex("-3,1");
        vertex.setOwner(players.get(0));

        game.rollForProduction(board, robber, 6);

        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.WOOD));
    }

    // TC3 – Rolling a 7 produces no resources for any player
    @Test
    public void testRolling7ProducesNoResources() {
        List<Player> players = createPlayers();
        Game game = createGame(players);
        Board board = createBoard();
        Robber robber = new Robber();

        Vertex vertex1 = board.getVertex("-3,1");
        vertex1.setOwner(players.get(0));

        Vertex vertex2 = board.getVertex("1,-1");
        vertex2.setOwner(players.get(1));

        game.rollForProduction(board, robber, 7);

        for (Player player : players) {
            for (Resource resource : Resource.values()) {
                Assertions.assertEquals(0,
                        player.getResourceCount(resource));
            }
        }
    }

    // TC3b – Robber blocks resource production on its tile
    @Test
    public void testRobberBlocksResourceProduction() {
        List<Player> players = createPlayers();
        Game game = createGame(players);
        Board board = createBoard();
        Robber robber = new Robber();

        // Place settlement adjacent to FOREST tile at (-2,0) token 5
        Vertex vertex = board.getVertex("-3,1");
        vertex.setOwner(players.get(0));

        // Place robber on that FOREST tile
        robber.setTile(board.getTile(-2, 0));

        game.rollForProduction(board, robber, 5);

        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.WOOD));
    }

    // TC4 – Turn advances to next player in clockwise order
    @Test
    public void testTurnAdvancesToNextPlayer() {
        List<Player> players = createPlayers();
        int[] rolls = {7, 5, 3};
        Game game = new Game(players, stubDiceRoller(rolls));
        Assertions.assertEquals(0, game.getCurrentPlayerIndex());
        game.advanceTurn();
        Assertions.assertEquals(1, game.getCurrentPlayerIndex());
    }

    // TC5 – Turn wraps around from last player to first player
    @Test
    public void testTurnWrapsAroundToFirstPlayer() {
        List<Player> players = createPlayers();
        int[] rolls = {7, 5, 3};
        Game game = new Game(players, stubDiceRoller(rolls));
        game.advanceTurn();
        game.advanceTurn();
        Assertions.assertEquals(2, game.getCurrentPlayerIndex());
        game.advanceTurn();
        Assertions.assertEquals(0, game.getCurrentPlayerIndex());
    }

    // TC6 – Game detects winner when current player reaches 10 VP
    @Test
    public void testGameDetectsWinnerAt10VP() {
        List<Player> players = createPlayers();
        Game game = createGame(players);

        for (int i = 0; i < 9; i++) {
            game.playVictoryPointCard();
        }
        Assertions.assertFalse(game.isGameOver());

        game.playVictoryPointCard();
        Assertions.assertTrue(game.isGameOver());
    }

    // TC7 – Game does not declare winner at 9 VP
    @Test
    public void testGameDoesNotDeclareWinnerAt9VP() {
        List<Player> players = createPlayers();
        Game game = createGame(players);

        for (int i = 0; i < 9; i++) {
            game.playVictoryPointCard();
        }

        Assertions.assertFalse(game.isGameOver());
    }

    // TC8 – Game does not declare winner if non-current player
    // reaches 10 VP
    @Test
    public void testNonCurrentPlayerAt10VPDoesNotWin() {
        List<Player> players = createPlayers();
        int[] rolls = {7, 5, 3};
        Game game = new Game(players, stubDiceRoller(rolls));

        // Current player is index 0
        // Give 10 VP to player 1 (not current)
        for (int i = 0; i < 10; i++) {
            players.get(1).addVictoryPointDevCard();
        }

        game.checkWinner();
        Assertions.assertFalse(game.isGameOver());
    }

    // TC9 – Game is over after winner is declared
    @Test
    public void testGameIsOverAfterWinnerDeclared() {
        List<Player> players = createPlayers();
        Game game = createGame(players);

        for (int i = 0; i < 10; i++) {
            game.playVictoryPointCard();
        }

        Assertions.assertTrue(game.isGameOver());
        Assertions.assertEquals(game.getCurrentPlayerIndex(),
                game.getWinnerIndex());
    }

    // TC10 – Cannot advance turn after game is over
    @Test
    public void testCannotAdvanceTurnAfterGameOver() {
        List<Player> players = createPlayers();
        Game game = createGame(players);

        for (int i = 0; i < 10; i++) {
            game.playVictoryPointCard();
        }

        Assertions.assertTrue(game.isGameOver());
        Assertions.assertThrows(IllegalStateException.class,
                () -> game.advanceTurn());
    }

    // TC11 – Cannot trade before rolling dice
    @Test
    public void testCannotTradeBeforeRolling() {
        List<Player> players = createPlayers();
        Game game = createGame(players);

        Assertions.assertThrows(IllegalStateException.class,
                () -> game.trade());
    }

    private Board createBoard() {
        Shuffler noOp = new Shuffler() {
            @Override
            public <T> void shuffle(List<T> list) {
                // no-op
            }
        };
        Board board = new Board(noOp);
        board.create();
        return board;
    }

    private List<Player> createPlayers() {
        return Arrays.asList(
                new Player("Alice", PlayerColor.RED),
                new Player("Bob", PlayerColor.BLUE),
                new Player("Carol", PlayerColor.WHITE)
        );
    }

    private Game createGame(List<Player> players) {
        int[] rolls = {7, 5, 3};
        return new Game(players, stubDiceRoller(rolls));
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