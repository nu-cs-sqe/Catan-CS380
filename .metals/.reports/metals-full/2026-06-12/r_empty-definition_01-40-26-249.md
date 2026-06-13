error id: file://<WORKSPACE>/src/test/java/domain/TurnFlowTest.java:_empty_/Player#addResource#
file://<WORKSPACE>/src/test/java/domain/TurnFlowTest.java
empty definition using pc, found symbol in pc: _empty_/Player#addResource#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 23729
uri: file://<WORKSPACE>/src/test/java/domain/TurnFlowTest.java
text:
```scala
package domain;

import board.Board;
import board.Edge;
import board.Robber;
import board.Shuffler;
import board.Tile;
import board.TileType;
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
        TurnFlow turnFlow = new TurnFlow(players, createBank());

        players.get(0).addResource(Resource.ORE, 1);
        players.get(0).addResource(Resource.WHEAT, 1);
        players.get(0).addResource(Resource.SHEEP, 1);

        turnFlow.buyDevelopmentCard(players.get(0));

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
        TurnFlow turnFlow = new TurnFlow(players, createBank());

        players.get(0).addResource(Resource.ORE, 1);
        players.get(0).addResource(Resource.WHEAT, 1);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buyDevelopmentCard(players.get(0)));
    }

    // TC16 – Cannot buy dev card when deck is empty
    @Test
    public void testCannotBuyDevCardWhenDeckEmpty() {
        Bank bank = createBank();
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, bank);

        for (int i = 0; i < 25; i++) {
            bank.drawDevelopmentCard();
        }

        players.get(0).addResource(Resource.ORE, 1);
        players.get(0).addResource(Resource.WHEAT, 1);
        players.get(0).addResource(Resource.SHEEP, 1);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buyDevelopmentCard(players.get(0)));
    }

    // TC17 – Pending dev card cannot be played this turn
    @Test
    public void testCannotPlayPendingDevCard() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, createBank());

        players.get(0).addResource(Resource.ORE, 1);
        players.get(0).addResource(Resource.WHEAT, 1);
        players.get(0).addResource(Resource.SHEEP, 1);

        turnFlow.buyDevelopmentCard(players.get(0));

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.playDevelopmentCard(players.get(0),
                        DevelopmentCard.KNIGHT));
    }

    // TC18 – Pending dev card moves to player hand after endTurn
    @Test
    public void testPendingCardMovesToHandAfterEndTurn() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, createBank());

        players.get(0).addResource(Resource.ORE, 1);
        players.get(0).addResource(Resource.WHEAT, 1);
        players.get(0).addResource(Resource.SHEEP, 1);

        turnFlow.buyDevelopmentCard(players.get(0));
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

        Assertions.assertEquals(1,
                players.get(0).getKnightsPlayed());
        Assertions.assertEquals(targetTile.getQ(),
                robber.getTile().getQ());
        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.ORE));
        Assertions.assertEquals(0,
                players.get(1).getResourceCount(Resource.ORE));
    }

    // TC23 – MONOPOLY: takes all of named resource from other players
    @Test
    public void testPlayMonopolyTakesAllOfResource() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        players.get(0).addDevelopmentCard(DevelopmentCard.MONOPOLY);
        players.get(1).addResource(Resource.WHEAT, 2);
        players.get(2).addResource(Resource.WHEAT, 3);

        turnFlow.playMonopolyCard(players.get(0), Resource.WHEAT);

        Assertions.assertEquals(5,
                players.get(0).getResourceCount(Resource.WHEAT));
        Assertions.assertEquals(0,
                players.get(1).getResourceCount(Resource.WHEAT));
        Assertions.assertEquals(0,
                players.get(2).getResourceCount(Resource.WHEAT));
    }

    // TC24 – MONOPOLY with GENERIC resource throws
    @Test
    public void testPlayMonopolyWithGenericThrows() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        players.get(0).addDevelopmentCard(DevelopmentCard.MONOPOLY);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> turnFlow.playMonopolyCard(players.get(0),
                        Resource.GENERIC));
    }

    // TC25 – ROAD_BUILDING: places 2 free roads
    @Test
    public void testPlayRoadBuildingPlaces2Roads() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        players.get(0).addDevelopmentCard(
                DevelopmentCard.ROAD_BUILDING);

        Edge edge1 = board.getEdge("0,2|1,1");
        Edge edge2 = board.getEdge("1,-1|1,1");

        turnFlow.playRoadBuildingCard(players.get(0), edge1, edge2);

        Assertions.assertEquals(13,
                players.get(0).getRemainingRoads());
    }

    // TC26 – ROAD_BUILDING: player has 0 roads remaining; throws
    @Test
    public void testRoadBuildingWith0RoadsThrows() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        players.get(0).addDevelopmentCard(
                DevelopmentCard.ROAD_BUILDING);

        int edgeIndex = 0;
        for (Edge edge : board.getEdges()) {
            if (edgeIndex >= 15) {
                break;
            }
            players.get(0).placeRoad(edge);
            edgeIndex++;
        }

        Edge edge1 = board.getEdge("-3,-5|-2,-4");
        Edge edge2 = board.getEdge("-2,-4|-1,-5");

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.playRoadBuildingCard(players.get(0),
                        edge1, edge2));
    }

    // TC27 – YEAR_OF_PLENTY: player receives 2 resources from bank
    @Test
    public void testPlayYearOfPlentyReceives2Resources() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, createBank());

        players.get(0).addDevelopmentCard(
                DevelopmentCard.YEAR_OF_PLENTY);

        turnFlow.playYearOfPlentyCard(players.get(0),
                Resource.WOOD, Resource.ORE);

        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.WOOD));
        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.ORE));
    }

    // TC28 – YEAR_OF_PLENTY: bank has 0 of requested resource throws
    @Test
    public void testYearOfPlentyBankEmpty() {
        Bank bank = createBank();
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, bank);

        players.get(0).addDevelopmentCard(
                DevelopmentCard.YEAR_OF_PLENTY);

        bank.distributeResource(Resource.ORE, 19);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.playYearOfPlentyCard(players.get(0),
                        Resource.WOOD, Resource.ORE));
    }

    // TC29 – YEAR_OF_PLENTY: same resource twice succeeds
    @Test
    public void testYearOfPlentySameResourceTwice() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, createBank());

        players.get(0).addDevelopmentCard(
                DevelopmentCard.YEAR_OF_PLENTY);

        turnFlow.playYearOfPlentyCard(players.get(0),
                Resource.WOOD, Resource.WOOD);

        Assertions.assertEquals(2,
                players.get(0).getResourceCount(Resource.WOOD));
    }

    // TC30 – Maritime trade at exact harbor rate succeeds
    @Test
    public void testMaritimeTradeAtExactRate() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, createBank());

        players.get(0).addResource(Resource.WOOD, 4);

        turnFlow.maritimeTrade(players.get(0),
                Resource.WOOD, 4, Resource.BRICK);

        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.WOOD));
        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.BRICK));
    }

    // TC31 – Maritime trade below best rate throws
    @Test
    public void testMaritimeTradeBelowRateThrows() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, createBank());

        players.get(0).addResource(Resource.WOOD, 1);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> turnFlow.maritimeTrade(players.get(0),
                        Resource.WOOD, 1, Resource.BRICK));
    }

    // TC32 – Maritime trade same resource give and receive throws
    @Test
    public void testMaritimeTradeSameResourceThrows() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, createBank());

        players.get(0).addResource(Resource.WOOD, 4);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> turnFlow.maritimeTrade(players.get(0),
                        Resource.WOOD, 4, Resource.WOOD));
    }

    // TC33 – Maritime trade when bank has 0 of receive resource throws
    @Test
    public void testMaritimeTradeWhenBankEmptyThrows() {
        Bank bank = createBank();
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, bank);

        bank.distributeResource(Resource.BRICK, 19);
        players.get(0).addResource(Resource.WOOD, 4);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.maritimeTrade(players.get(0),
                        Resource.WOOD, 4, Resource.BRICK));
    }

    // TC34 – getVictoryPoints includes largest army bonus
    @Test
    public void testVictoryPointsIncludesLargestArmy() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        players.get(0).playKnight();
        players.get(0).playKnight();
        players.get(0).playKnight();
        turnFlow.updateLargestArmy();

        Assertions.assertEquals(2, turnFlow.getVictoryPoints(0));
    }

    // TC35 – getVictoryPoints includes longest road bonus
    @Test
    public void testVictoryPointsIncludesLongestRoad() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        setEdgeOwner(board, "0,2|1,1", players.get(0));
        setEdgeOwner(board, "1,-1|1,1", players.get(0));
        setEdgeOwner(board, "0,-2|1,-1", players.get(0));
        setEdgeOwner(board, "-1,-1|0,-2", players.get(0));
        setEdgeOwner(board, "-1,-1|-1,1", players.get(0));
        turnFlow.updateLongestRoad(board);

        Assertions.assertEquals(2, turnFlow.getVictoryPoints(0));
    }

    private void setEdgeOwner(Board board, String edgeKey,
                              Player player) {
        Edge edge = board.getEdge(edgeKey);
        if (edge != null) {
            edge.setOwner(player);
        }
    }

    // TC36 – checkWin returns true at exactly 10 VP
    @Test
    public void testCheckWinAt10VP() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        for (int i = 0; i < 10; i++) {
            players.get(0).addVictoryPointDevCard();
        }

        Assertions.assertTrue(turnFlow.checkWin(0));
    }

    // TC37 – checkWin returns false at 9 VP
    @Test
    public void testCheckWinAt9VP() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        for (int i = 0; i < 9; i++) {
            players.get(0).addVictoryPointDevCard();
        }

        Assertions.assertFalse(turnFlow.checkWin(0));
    }

    // TC38 – checkWin called after every VP-changing action;
    // game ends immediately, not at end of turn
    @Test
    public void testGameEndsImmediatelyWhenActionReaches10VP() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        for (int i = 0; i < 9; i++) {
            players.get(0).addVictoryPointDevCard();
        }
        giveSettlementCost(players.get(0));

        Assertions.assertFalse(turnFlow.isGameOver());

        turnFlow.buildSettlement(players.get(0), new Vertex("win-v1"));

        Assertions.assertTrue(turnFlow.isGameOver());
    }

    // TC39 – endTurn resets devCardPlayedThisTurn to false
    @Test
    public void testEndTurnResetsDevCardPlayedFlag() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        players.get(0).addDevelopmentCard(DevelopmentCard.KNIGHT);
        players.get(0).addDevelopmentCard(DevelopmentCard.MONOPOLY);

        turnFlow.playDevelopmentCard(players.get(0),
                DevelopmentCard.KNIGHT);
        turnFlow.endTurn(players.get(0));

        Assertions.assertDoesNotThrow(
                () -> turnFlow.playDevelopmentCard(players.get(0),
                        DevelopmentCard.MONOPOLY));
    }

    // TC40 – endTurn flushes pending dev cards to player hand
    @Test
    public void testEndTurnFlushesPendingDevCardsToHand() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, createBank());

        players.get(0).addResource(Resource.ORE, 1);
        players.get(0).addResource(Resource.WHEAT, 1);
        players.get(0).addRe@@source(Resource.SHEEP, 1);
        turnFlow.buyDevelopmentCard(players.get(0));

        turnFlow.endTurn(players.get(0));

        Assertions.assertEquals(0, turnFlow.getPendingCardCount());
        Assertions.assertEquals(1,
                players.get(0).getDevelopmentCards().size());
    }



    // TC44 – Build settlement with exact resources succeeds
    @Test
    public void testBuildSettlementWithExactResources() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Vertex vertex = new Vertex("build-v1");

        giveSettlementCost(players.get(0));

        turnFlow.buildSettlement(players.get(0), vertex);

        Assertions.assertNotNull(vertex.getSettlement());
        Assertions.assertEquals(4,
                players.get(0).getRemainingSettlements());
        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.WOOD));
        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.BRICK));
        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.SHEEP));
        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.WHEAT));
    }

    private void giveSettlementCost(Player player) {
        player.addResource(Resource.WOOD, 1);
        player.addResource(Resource.BRICK, 1);
        player.addResource(Resource.SHEEP, 1);
        player.addResource(Resource.WHEAT, 1);
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

    private Bank createBank() {
        return new Bank(cards -> { });
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Player#addResource#