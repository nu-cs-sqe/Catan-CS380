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

    // TC12 – Steal from victim with 0 resources does nothing
    @Test
    public void testStealFromVictimWith0Resources() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        turnFlow.stealResource(players.get(0), players.get(1));

        for (Resource resource : Resource.values()) {
            Assertions.assertEquals(0,
                    players.get(0).getResourceCount(resource));
            Assertions.assertEquals(0,
                    players.get(1).getResourceCount(resource));
        }
    }

    // TC13 – Cannot steal from yourself
    @Test
    public void testCannotStealFromYourself() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        players.get(0).addResource(Resource.BRICK, 1);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> turnFlow.stealResource(players.get(0),
                        players.get(0)));
    }

    // TC14 – Buy dev card with exact resources enters pending list
    @Test
    public void testBuyDevCardWithExactResources() {
        List<Player> players = createPlayers();
        Bank bank = createBank();
        TurnFlow turnFlow = new TurnFlow(players);

        players.get(0).addResource(Resource.ORE, 1);
        players.get(0).addResource(Resource.WHEAT, 1);
        players.get(0).addResource(Resource.SHEEP, 1);

        turnFlow.buyDevelopmentCard(players.get(0), bank);

        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.ORE));
        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.WHEAT));
        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.SHEEP));
        Assertions.assertEquals(1, turnFlow.getPendingCardCount());
        Assertions.assertTrue(
                players.get(0).getDevelopmentCards().isEmpty());
    }

    // TC15 – Cannot buy dev card with insufficient resources
    @Test
    public void testCannotBuyDevCardWithInsufficientResources() {
        List<Player> players = createPlayers();
        Bank bank = createBank();
        TurnFlow turnFlow = new TurnFlow(players);

        players.get(0).addResource(Resource.ORE, 1);
        players.get(0).addResource(Resource.WHEAT, 1);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buyDevelopmentCard(players.get(0), bank));
    }

    // TC16 – Cannot buy dev card when deck is empty
    @Test
    public void testCannotBuyDevCardWhenDeckEmpty() {
        List<Player> players = createPlayers();
        Bank bank = createBank();
        TurnFlow turnFlow = new TurnFlow(players);

        // Draw all 25 cards to empty the deck
        for (int i = 0; i < 25; i++) {
            bank.drawDevelopmentCard();
        }

        players.get(0).addResource(Resource.ORE, 1);
        players.get(0).addResource(Resource.WHEAT, 1);
        players.get(0).addResource(Resource.SHEEP, 1);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buyDevelopmentCard(players.get(0), bank));
    }

    // TC17 – Pending dev card cannot be played this turn
    @Test
    public void testCannotPlayPendingDevCard() {
        List<Player> players = createPlayers();
        Bank bank = createBank();
        TurnFlow turnFlow = new TurnFlow(players);

        players.get(0).addResource(Resource.ORE, 1);
        players.get(0).addResource(Resource.WHEAT, 1);
        players.get(0).addResource(Resource.SHEEP, 1);

        turnFlow.buyDevelopmentCard(players.get(0), bank);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.playDevelopmentCard(players.get(0),
                        DevelopmentCard.KNIGHT));
    }

    // TC18 – Pending dev card moves to player hand after endTurn
    @Test
    public void testPendingCardMovesToHandAfterEndTurn() {
        List<Player> players = createPlayers();
        Bank bank = createBank();
        TurnFlow turnFlow = new TurnFlow(players);

        players.get(0).addResource(Resource.ORE, 1);
        players.get(0).addResource(Resource.WHEAT, 1);
        players.get(0).addResource(Resource.SHEEP, 1);

        turnFlow.buyDevelopmentCard(players.get(0), bank);
        Assertions.assertTrue(
                players.get(0).getDevelopmentCards().isEmpty());

        turnFlow.endTurn(players.get(0));

        Assertions.assertFalse(
                players.get(0).getDevelopmentCards().isEmpty());
        Assertions.assertEquals(0, turnFlow.getPendingCardCount());
    }



    // TC19 – Player plays first dev card this turn; succeeds
    @Test
    public void testPlayFirstDevCardSucceeds() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        players.get(0).addDevelopmentCard(DevelopmentCard.KNIGHT);

        Assertions.assertDoesNotThrow(
                () -> turnFlow.playDevelopmentCard(players.get(0),
                        DevelopmentCard.KNIGHT));
    }

    // TC20 – Cannot play second dev card same turn
    @Test
    public void testCannotPlaySecondDevCardSameTurn() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        players.get(0).addDevelopmentCard(DevelopmentCard.KNIGHT);
        players.get(0).addDevelopmentCard(DevelopmentCard.MONOPOLY);

        turnFlow.playDevelopmentCard(players.get(0),
                DevelopmentCard.KNIGHT);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.playDevelopmentCard(players.get(0),
                        DevelopmentCard.MONOPOLY));
    }

    // TC21 – Cannot play VICTORY_POINT card
    @Test
    public void testCannotPlayVictoryPointCard() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        players.get(0).addDevelopmentCard(
                DevelopmentCard.VICTORY_POINT);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> turnFlow.playDevelopmentCard(players.get(0),
                        DevelopmentCard.VICTORY_POINT));
    }

    // TC22 – KNIGHT: moves robber, steals, increments knights,
    // checks largest army
    @Test
    public void testPlayKnightMovesRobberAndIncrementsKnights() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();
        robber.setTile(board.getTile(0, 0));

        players.get(0).addDevelopmentCard(DevelopmentCard.KNIGHT);
        players.get(1).addResource(Resource.ORE, 1);

        Tile targetTile = board.getTile(-2, 0);
        turnFlow.playKnightCard(players.get(0), robber,
                targetTile, players.get(1));

        Assertions.assertEquals(1, players.get(0).getKnightsPlayed());
        Assertions.assertEquals(targetTile.getQ(),
                robber.getTile().getQ());
        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.ORE));
        Assertions.assertEquals(0,
                players.get(1).getResourceCount(Resource.ORE));
    }



    private Bank createBank() {
        return new Bank(cards -> { });
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