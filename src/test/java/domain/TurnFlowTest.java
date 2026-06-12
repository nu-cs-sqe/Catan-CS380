package domain;

import board.Board;
import board.Robber;
import board.Shuffler;
import board.Vertex;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class TurnFlowTest {

    // TC1 – Roll produces resources for player with settlement
    // on matching tile
    @Test
    public void testRollProducesResourcesForAdjacentSettlement() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();

        // No-op shuffler: position (-2,0) is FOREST with token 5
        // Vertex "-3,1" is adjacent to that tile
        Vertex vertex = board.getVertex("-3,1");
        vertex.setOwner(players.get(0));

        turnFlow.rollForProduction(board, robber, 5);

        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.WOOD));
    }

    // TC2 – Roll does not produce for non-matching tile
    @Test
    public void testRollDoesNotProduceForNonMatchingTile() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();

        Vertex vertex = board.getVertex("-3,1");
        vertex.setOwner(players.get(0));

        // Token on that tile is 5, but we roll 6
        turnFlow.rollForProduction(board, robber, 6);

        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.WOOD));
    }

    // TC3 – City on matching tile yields 2 resources
    @Test
    public void testCityYields2Resources() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();

        Vertex vertex = board.getVertex("-3,1");
        vertex.setOwner(players.get(0));
        players.get(0).placeSettlement(vertex);
        players.get(0).upgradeSettlementToCity(vertex);

        turnFlow.rollForProduction(board, robber, 5);

        Assertions.assertEquals(2,
                players.get(0).getResourceCount(Resource.WOOD));
    }

    // TC4 – Robber blocks resource production on its tile
    @Test
    public void testRobberBlocksResourceProduction() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();

        Vertex vertex = board.getVertex("-3,1");
        vertex.setOwner(players.get(0));

        // Place robber on FOREST tile at (-2,0) with token 5
        robber.setTile(board.getTile(-2, 0));

        turnFlow.rollForProduction(board, robber, 5);

        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.WOOD));
    }

    // TC5 – Rolling 7 produces no resources
    @Test
    public void testRolling7ProducesNoResources() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();

        Vertex vertex = board.getVertex("-3,1");
        vertex.setOwner(players.get(0));

        turnFlow.rollForProduction(board, robber, 7);

        for (Resource resource : Resource.values()) {
            Assertions.assertEquals(0,
                    players.get(0).getResourceCount(resource));
        }
    }

    private Board createBoard() {
        Shuffler noOp = new Shuffler() {
            @Override
            public <T> void shuffle(List<T> list) {
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
}