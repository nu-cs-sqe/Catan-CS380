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