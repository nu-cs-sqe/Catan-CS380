package domain;

import board.Board;
import board.Robber;
import board.Shuffler;
import board.Vertex;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import board.Tile;
import board.TileType;

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

    // TC6 – Rolling 7: player with 8+ cards must discard half
    @Test
    public void testRolling7PlayerWith8CardsDiscards4() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        for (int i = 0; i < 8; i++) {
            players.get(0).addResource(Resource.BRICK, 1);
        }

        Assertions.assertEquals(4, turnFlow.getDiscardCount(0));
    }

    // TC7 – Rolling 7: player with 7 cards does not discard
    @Test
    public void testRolling7PlayerWith7CardsNoDiscard() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        for (int i = 0; i < 7; i++) {
            players.get(0).addResource(Resource.BRICK, 1);
        }

        Assertions.assertEquals(0, turnFlow.getDiscardCount(0));
    }

    // TC8 – Rolling 7: odd card count rounds down
    @Test
    public void testRolling7OddCardCountRoundsDown() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        for (int i = 0; i < 9; i++) {
            players.get(0).addResource(Resource.BRICK, 1);
        }

        Assertions.assertEquals(4, turnFlow.getDiscardCount(0));
    }


    // TC9 – Robber must move to a different tile
    @Test
    public void testRobberMustMoveToDifferentTile() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();

        robber.setTile(board.getTile(0, 0));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> turnFlow.moveRobber(robber, board.getTile(0, 0)));
    }

    // TC10 – Robber can move to any other tile including desert
    @Test
    public void testRobberCanMoveToDesert() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();

        robber.setTile(board.getTile(0, 0));

        Tile desertTile = findDesertTile(board);
        Assertions.assertNotNull(desertTile);

        turnFlow.moveRobber(robber, desertTile);

        Assertions.assertEquals(desertTile.getQ(),
                robber.getTile().getQ());
        Assertions.assertEquals(desertTile.getR(),
                robber.getTile().getR());
    }

    // TC11 – Steal 1 resource from victim with resources
    @Test
    public void testStealResourceFromVictim() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        players.get(1).addResource(Resource.ORE, 1);

        turnFlow.stealResource(players.get(0), players.get(1));

        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.ORE));
        Assertions.assertEquals(0,
                players.get(1).getResourceCount(Resource.ORE));
    }

    private Tile findDesertTile(Board board) {
        for (Tile tile : board.getTiles()) {
            if (tile.getTileType() == TileType.DESERT) {
                return tile;
            }
        }
        return null;
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