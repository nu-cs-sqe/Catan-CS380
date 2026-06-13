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
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class TurnFlowTest {

    // TC1 – Roll produces resources for player with settlement
    // on matching tile
    @Test
    public void testRollProducesResourcesForAdjacentSettlement() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, createBank());
        Board board = createBoard();
        Robber robber = new Robber();

        Vertex vertex = board.getVertex("-3,1");
        players.get(0).placeSettlement(vertex);

        turnFlow.rollForProduction(board, robber, 5);

        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.WOOD));
    }

    // TC72 – Production draws the distributed resources from the bank
    @Test
    public void testProductionDrawsFromBank() {
        List<Player> players = createPlayers();
        Bank bank = createBank();
        TurnFlow turnFlow = new TurnFlow(players, bank);
        Board board = createBoard();

        players.get(0).placeSettlement(board.getVertex("-3,1"));
        int before = bank.getStock(Resource.WOOD);

        turnFlow.rollForProduction(board, new Robber(), 5);

        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.WOOD));
        Assertions.assertEquals(before - 1,
                bank.getStock(Resource.WOOD));
    }

    // TC73 – Production withheld when the bank cannot supply every claimant
    @Test
    public void testProductionWithheldWhenBankShort() {
        List<Player> players = createPlayers();
        Bank bank = createBank();
        TurnFlow turnFlow = new TurnFlow(players, bank);
        Board board = createBoard();

        // Two players both produce WOOD from the token-5 forest tile
        players.get(0).placeSettlement(board.getVertex("-3,1"));
        players.get(1).placeSettlement(board.getVertex("-4,2"));

        // Leave only 1 WOOD in the bank — not enough for both claimants
        bank.distributeResource(Resource.WOOD,
                bank.getStock(Resource.WOOD) - 1);

        turnFlow.rollForProduction(board, new Robber(), 5);

        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.WOOD));
        Assertions.assertEquals(0,
                players.get(1).getResourceCount(Resource.WOOD));
    }

    // TC2 – Roll does not produce for non-matching tile
    @Test
    public void testRollDoesNotProduceForNonMatchingTile() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, createBank());
        Board board = createBoard();
        Robber robber = new Robber();

        Vertex vertex = board.getVertex("-3,1");
        players.get(0).placeSettlement(vertex);

        turnFlow.rollForProduction(board, robber, 6);

        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.WOOD));
    }

    // TC3 – City on matching tile yields 2 resources
    @Test
    public void testCityYields2Resources() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, createBank());
        Board board = createBoard();
        Robber robber = new Robber();

        Vertex vertex = board.getVertex("-3,1");
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
        TurnFlow turnFlow = new TurnFlow(players, createBank());
        Board board = createBoard();
        Robber robber = new Robber();

        Vertex vertex = board.getVertex("-3,1");
        players.get(0).placeSettlement(vertex);

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
        players.get(0).placeSettlement(vertex);

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

    // TC64 – Discarding the required number of cards succeeds
    @Test
    public void testDiscardRequiredCountSucceeds() {
        List<Player> players = createPlayers();
        Bank bank = createBank();
        TurnFlow turnFlow = new TurnFlow(players, bank);

        for (int i = 0; i < 8; i++) {
            bank.distributeResource(Resource.BRICK, 1);
            players.get(0).addResource(Resource.BRICK, 1);
        }
        int bankBefore = bank.getStock(Resource.BRICK);

        turnFlow.discard(players.get(0), Map.of(Resource.BRICK, 4));

        Assertions.assertEquals(4,
                players.get(0).getResourceCount(Resource.BRICK));
        Assertions.assertEquals(bankBefore + 4,
                bank.getStock(Resource.BRICK));
    }

    // TC65 – Discarding the wrong number of cards throws
    @Test
    public void testDiscardWrongCountThrows() {
        List<Player> players = createPlayers();
        Bank bank = createBank();
        TurnFlow turnFlow = new TurnFlow(players, bank);

        for (int i = 0; i < 8; i++) {
            players.get(0).addResource(Resource.BRICK, 1);
        }

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> turnFlow.discard(players.get(0),
                        Map.of(Resource.BRICK, 3)));
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
                () -> turnFlow.moveRobber(robber, board.getTile(0, 0),
                        board));
    }

    // TC78 – Moving the robber to a tile not on the board throws
    @Test
    public void testMoveRobberToNonBoardTileThrows() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();

        Tile offBoard = new Tile(TileType.DESERT, 99, 99);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> turnFlow.moveRobber(robber, offBoard, board));
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

        turnFlow.moveRobber(robber, desertTile, board);

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
        Board board = createBoard();
        Robber robber = new Robber();
        robber.setTile(board.getTile(-2, 0));

        players.get(1).placeSettlement(board.getVertex("-3,1"));
        players.get(1).addResource(Resource.ORE, 1);

        turnFlow.stealResource(players.get(0), players.get(1),
                robber, board);

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
        Board board = createBoard();
        Robber robber = new Robber();
        robber.setTile(board.getTile(-2, 0));

        players.get(1).placeSettlement(board.getVertex("-3,1"));

        turnFlow.stealResource(players.get(0), players.get(1),
                robber, board);

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
        Board board = createBoard();
        Robber robber = new Robber();

        players.get(0).addResource(Resource.BRICK, 1);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> turnFlow.stealResource(players.get(0),
                        players.get(0), robber, board));
    }

    // TC66 – stealCandidates lists players bordering the robber's tile
    @Test
    public void testStealCandidatesBorderingRobberTile() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();
        robber.setTile(board.getTile(-2, 0));

        players.get(1).placeSettlement(board.getVertex("-3,1"));
        players.get(2).placeSettlement(board.getVertex("2,8"));

        List<Player> candidates = turnFlow.stealCandidates(robber, board);

        Assertions.assertTrue(candidates.contains(players.get(1)));
        Assertions.assertFalse(candidates.contains(players.get(2)));
    }

    // TC67 – Stealing from a victim not bordering the robber throws
    @Test
    public void testStealFromNonBorderingVictimThrows() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();
        robber.setTile(board.getTile(-2, 0));

        players.get(1).placeSettlement(board.getVertex("2,8"));
        players.get(1).addResource(Resource.ORE, 1);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> turnFlow.stealResource(players.get(0),
                        players.get(1), robber, board));
    }

    // TC68 – Resolving a roll of 7 sets the robber pending, skips production
    @Test
    public void testResolveRollSevenSetsRobberPending() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();

        players.get(0).placeSettlement(board.getVertex("-3,1"));

        turnFlow.resolveRoll(board, robber, 7);

        Assertions.assertTrue(turnFlow.isRobberPending());
        for (Resource resource : Resource.values()) {
            Assertions.assertEquals(0,
                    players.get(0).getResourceCount(resource));
        }
    }

    // TC69 – Resolving a non-7 roll produces and leaves no robber pending
    @Test
    public void testResolveRollNonSevenProduces() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, createBank());
        Board board = createBoard();
        Robber robber = new Robber();

        players.get(0).placeSettlement(board.getVertex("-3,1"));

        turnFlow.resolveRoll(board, robber, 5);

        Assertions.assertFalse(turnFlow.isRobberPending());
        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.WOOD));
    }

    // TC70 – Taking another action while the robber is pending throws
    @Test
    public void testActionWhileRobberPendingThrows() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();

        turnFlow.resolveRoll(board, robber, 7);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.endTurn(players.get(0)));
    }

    // TC71 – Moving the robber and stealing clears the pending state
    @Test
    public void testMoveRobberAndStealClearsPending() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();
        Robber robber = new Robber();

        players.get(1).placeSettlement(board.getVertex("-3,1"));
        players.get(1).addResource(Resource.ORE, 1);

        turnFlow.resolveRoll(board, robber, 7);
        turnFlow.moveRobberAndSteal(robber, board.getTile(-2, 0),
                players.get(1), board);

        Assertions.assertFalse(turnFlow.isRobberPending());
        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.ORE));
        Assertions.assertEquals(0,
                players.get(1).getResourceCount(Resource.ORE));
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

    // TC76 – Buying a dev card with insufficient resources changes nothing
    @Test
    public void testBuyDevCardInsufficientLeavesResources() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players, createBank());

        players.get(0).addResource(Resource.ORE, 1);
        players.get(0).addResource(Resource.WHEAT, 1);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buyDevelopmentCard(players.get(0)));

        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.ORE));
        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.WHEAT));
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
        players.get(1).placeSettlement(board.getVertex("-3,1"));
        players.get(1).addResource(Resource.ORE, 1);

        Tile targetTile = board.getTile(-2, 0);
        turnFlow.playKnightCard(robber, targetTile,
                players.get(1), board);

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
        players.get(0).placeSettlement(board.getVertex("0,2"));

        Edge edge1 = board.getEdge("0,2|1,1");
        Edge edge2 = board.getEdge("1,-1|1,1");

        turnFlow.playRoadBuildingCard(players.get(0), edge1, edge2, board);

        Assertions.assertEquals(13,
                players.get(0).getRemainingRoads());
    }

    // TC59 – ROAD_BUILDING roads must connect to player's network
    @Test
    public void testRoadBuildingDisconnectedRoadThrows() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        players.get(0).addDevelopmentCard(
                DevelopmentCard.ROAD_BUILDING);

        Edge edge1 = board.getEdge("0,2|1,1");
        Edge edge2 = board.getEdge("1,-1|1,1");

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.playRoadBuildingCard(players.get(0),
                        edge1, edge2, board));
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
                        edge1, edge2, board));
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

    // TC79 – A player wins only on their own turn
    @Test
    public void testWinOnlyOnOwnTurn() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        for (int i = 0; i < 8; i++) {
            players.get(1).addVictoryPointDevCard();
        }
        setEdgeOwner(board, "0,2|1,1", players.get(1));
        setEdgeOwner(board, "1,-1|1,1", players.get(1));
        setEdgeOwner(board, "0,-2|1,-1", players.get(1));
        setEdgeOwner(board, "-1,-1|0,-2", players.get(1));
        setEdgeOwner(board, "-1,-1|-1,1", players.get(1));

        // Action resolved during player 0's turn pushes player 1 to 10 VP
        turnFlow.updateLongestRoad(board);
        Assertions.assertEquals(10, turnFlow.getVictoryPoints(1));
        Assertions.assertFalse(turnFlow.isGameOver());

        turnFlow.endTurn(players.get(0));

        Assertions.assertTrue(turnFlow.isGameOver());
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
        players.get(0).addResource(Resource.SHEEP, 1);
        turnFlow.buyDevelopmentCard(players.get(0));

        turnFlow.endTurn(players.get(0));

        Assertions.assertEquals(0, turnFlow.getPendingCardCount());
        Assertions.assertEquals(1,
                players.get(0).getDevelopmentCards().size());
    }

    // TC41 – endTurn advances to next player
    @Test
    public void testEndTurnAdvancesToNextPlayer() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        Assertions.assertEquals(0, turnFlow.getCurrentPlayerIndex());

        turnFlow.endTurn(players.get(0));

        Assertions.assertEquals(1, turnFlow.getCurrentPlayerIndex());
    }

    // TC42 – endTurn wraps from last player to first
    @Test
    public void testEndTurnWrapsFromLastToFirstPlayer() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        turnFlow.endTurn(players.get(0));
        turnFlow.endTurn(players.get(1));
        Assertions.assertEquals(2, turnFlow.getCurrentPlayerIndex());

        turnFlow.endTurn(players.get(2));

        Assertions.assertEquals(0, turnFlow.getCurrentPlayerIndex());
    }

    // TC43 – Cannot endTurn after game is over
    @Test
    public void testCannotEndTurnAfterGameOver() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);

        for (int i = 0; i < 9; i++) {
            players.get(0).addVictoryPointDevCard();
        }
        giveSettlementCost(players.get(0));
        turnFlow.buildSettlement(players.get(0), new Vertex("win-v"));

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.endTurn(players.get(0)));
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

    // TC45 – Build settlement with insufficient resources throws
    @Test
    public void testBuildSettlementInsufficientResourcesThrows() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Vertex vertex = new Vertex("build-v2");

        players.get(0).addResource(Resource.WOOD, 1);
        players.get(0).addResource(Resource.BRICK, 1);
        players.get(0).addResource(Resource.SHEEP, 1);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buildSettlement(players.get(0), vertex));
    }

    // TC46 – Cannot build settlement on occupied vertex
    @Test
    public void testBuildSettlementOnOccupiedVertexThrows() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Vertex vertex = new Vertex("build-v3");

        players.get(1).placeSettlement(vertex);
        giveSettlementCost(players.get(0));

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buildSettlement(players.get(0), vertex));
    }

    // TC47 – Cannot build settlement violating distance rule
    @Test
    public void testCannotBuildSettlementViolatingDistanceRule() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        Vertex vertex1 = board.getVertex("-3,1");
        Vertex vertex2 = board.getVertex("-3,-1");

        // Give road adjacent to vertex1
        Edge road1 = board.getEdge("-3,1|-2,2");
        road1.setOwner(players.get(0));

        // Give road adjacent to vertex2
        Edge road2 = board.getEdge("-3,-1|-3,1");
        road2.setOwner(players.get(0));

        players.get(0).addResource(Resource.WOOD, 2);
        players.get(0).addResource(Resource.BRICK, 2);
        players.get(0).addResource(Resource.SHEEP, 2);
        players.get(0).addResource(Resource.WHEAT, 2);

        turnFlow.buildSettlement(players.get(0), vertex1, board);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buildSettlement(players.get(0),
                        vertex2, board));
    }

    // TC48 – Cannot build settlement without adjacent road
    @Test
    public void testCannotBuildSettlementWithoutAdjacentRoad() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        Vertex vertex = board.getVertex("-3,1");

        players.get(0).addResource(Resource.WOOD, 1);
        players.get(0).addResource(Resource.BRICK, 1);
        players.get(0).addResource(Resource.SHEEP, 1);
        players.get(0).addResource(Resource.WHEAT, 1);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buildSettlement(players.get(0),
                        vertex, board));
    }

    // TC49 – Cannot build settlement with 0 pieces remaining
    @Test
    public void testCannotBuildSettlementWith0Remaining() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        // Place 5 settlements directly to exhaust pieces
        Vertex v1 = board.getVertex("-3,1");
        Vertex v2 = board.getVertex("-4,2");
        Vertex v3 = board.getVertex("3,-1");
        Vertex v4 = board.getVertex("3,1");
        Vertex v5 = board.getVertex("5,1");

        players.get(0).placeSettlement(v1);
        players.get(0).placeSettlement(v2);
        players.get(0).placeSettlement(v3);
        players.get(0).placeSettlement(v4);
        players.get(0).placeSettlement(v5);

        Vertex v6 = board.getVertex("-3,-1");
        Edge road = board.getEdge("-3,-1|-3,1");
        road.setOwner(players.get(0));

        players.get(0).addResource(Resource.WOOD, 1);
        players.get(0).addResource(Resource.BRICK, 1);
        players.get(0).addResource(Resource.SHEEP, 1);
        players.get(0).addResource(Resource.WHEAT, 1);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buildSettlement(players.get(0),
                        v6, board));
    }

    // TC60 – Setup settlement is free and needs no adjacent road
    @Test
    public void testBuildSetupSettlementFreeAndNoRoadRequired() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        Vertex vertex = board.getVertex("-3,1");

        turnFlow.buildSetupSettlement(players.get(0), vertex, board);

        Assertions.assertNotNull(vertex.getSettlement());
        Assertions.assertEquals(4,
                players.get(0).getRemainingSettlements());
        for (Resource resource : Resource.values()) {
            Assertions.assertEquals(0,
                    players.get(0).getResourceCount(resource));
        }
    }

    // TC61 – Setup settlement violating the distance rule throws
    @Test
    public void testBuildSetupSettlementDistanceRuleThrows() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        Vertex vertex1 = board.getVertex("-3,1");
        Vertex vertex2 = board.getVertex("-3,-1");

        turnFlow.buildSetupSettlement(players.get(0), vertex1, board);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buildSetupSettlement(players.get(1),
                        vertex2, board));
    }

    // TC62 – Setup road is free and must connect to player's settlement
    @Test
    public void testBuildSetupRoadFreeAndConnected() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        Vertex vertex = board.getVertex("0,2");
        turnFlow.buildSetupSettlement(players.get(0), vertex, board);

        Edge edge = board.getEdge("0,2|1,1");
        turnFlow.buildSetupRoad(players.get(0), edge, board);

        Assertions.assertEquals(14,
                players.get(0).getRemainingRoads());
        for (Resource resource : Resource.values()) {
            Assertions.assertEquals(0,
                    players.get(0).getResourceCount(resource));
        }
    }

    // TC63 – Setup resources derived from settlement's adjacent tiles
    @Test
    public void testGrantSetupResourcesFromAdjacentTiles() {
        List<Player> players = createPlayers();
        Bank bank = createBank();
        TurnFlow turnFlow = new TurnFlow(players, bank);
        Board board = createBoard();

        Vertex vertex = board.getVertex("0,2");
        players.get(0).placeSettlement(vertex);

        turnFlow.grantSetupResources(players.get(0), vertex);

        Assertions.assertEquals(2,
                players.get(0).getResourceCount(Resource.WHEAT));
        Assertions.assertEquals(1,
                players.get(0).getResourceCount(Resource.BRICK));
    }

    // TC50 – Upgrade settlement to city with exact resources
    @Test
    public void testUpgradeSettlementToCityWithExactResources() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        Vertex vertex = board.getVertex("-3,1");
        players.get(0).placeSettlement(vertex);

        players.get(0).addResource(Resource.ORE, 3);
        players.get(0).addResource(Resource.WHEAT, 2);

        turnFlow.buildCity(players.get(0), vertex);

        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.ORE));
        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.WHEAT));
        Assertions.assertTrue(vertex.getSettlement().isCity());
    }

    // TC51 – Upgrade to city with insufficient resources throws
    @Test
    public void testUpgradeToCityInsufficientResources() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        Vertex vertex = board.getVertex("-3,1");
        players.get(0).placeSettlement(vertex);

        players.get(0).addResource(Resource.ORE, 2);
        players.get(0).addResource(Resource.WHEAT, 2);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buildCity(players.get(0), vertex));
    }

    // TC52 – Cannot upgrade vertex without player's settlement
    @Test
    public void testCannotUpgradeWithoutSettlement() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        Vertex vertex = board.getVertex("-3,1");

        players.get(0).addResource(Resource.ORE, 3);
        players.get(0).addResource(Resource.WHEAT, 2);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buildCity(players.get(0), vertex));
    }

    // TC53 – Cannot upgrade with 0 city pieces remaining
    @Test
    public void testCannotUpgradeWith0CityPieces() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        Vertex v1 = board.getVertex("-3,1");
        Vertex v2 = board.getVertex("-4,2");
        Vertex v3 = board.getVertex("3,-1");
        Vertex v4 = board.getVertex("3,1");
        Vertex v5 = board.getVertex("5,1");

        players.get(0).placeSettlement(v1);
        players.get(0).placeSettlement(v2);
        players.get(0).placeSettlement(v3);
        players.get(0).placeSettlement(v4);
        players.get(0).placeSettlement(v5);

        players.get(0).upgradeSettlementToCity(v1);
        players.get(0).upgradeSettlementToCity(v2);
        players.get(0).upgradeSettlementToCity(v3);
        players.get(0).upgradeSettlementToCity(v4);

        players.get(0).addResource(Resource.ORE, 3);
        players.get(0).addResource(Resource.WHEAT, 2);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buildCity(players.get(0), v5));
    }

    // TC54 – Upgrading city frees a settlement piece
    @Test
    public void testUpgradingCityFreesSettlementPiece() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        Vertex v1 = board.getVertex("-3,1");
        Vertex v2 = board.getVertex("-4,2");
        Vertex v3 = board.getVertex("3,-1");
        Vertex v4 = board.getVertex("3,1");
        Vertex v5 = board.getVertex("5,1");

        players.get(0).placeSettlement(v1);
        players.get(0).placeSettlement(v2);
        players.get(0).placeSettlement(v3);
        players.get(0).placeSettlement(v4);
        players.get(0).placeSettlement(v5);

        Assertions.assertEquals(0,
                players.get(0).getRemainingSettlements());

        players.get(0).addResource(Resource.ORE, 3);
        players.get(0).addResource(Resource.WHEAT, 2);

        turnFlow.buildCity(players.get(0), v1);

        Assertions.assertEquals(1,
                players.get(0).getRemainingSettlements());
    }

    // TC55 – Build road with exact resources succeeds
    @Test
    public void testBuildRoadWithExactResources() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        Edge edge = board.getEdge("0,2|1,1");
        players.get(0).placeSettlement(board.getVertex("0,2"));

        players.get(0).addResource(Resource.WOOD, 1);
        players.get(0).addResource(Resource.BRICK, 1);

        turnFlow.buildRoad(players.get(0), edge, board);

        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.WOOD));
        Assertions.assertEquals(0,
                players.get(0).getResourceCount(Resource.BRICK));
        Assertions.assertEquals(14,
                players.get(0).getRemainingRoads());
    }

    // TC58 – Cannot build road disconnected from player's network
    @Test
    public void testCannotBuildRoadDisconnectedFromNetwork() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        Edge edge = board.getEdge("0,2|1,1");

        players.get(0).addResource(Resource.WOOD, 1);
        players.get(0).addResource(Resource.BRICK, 1);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buildRoad(players.get(0), edge, board));
    }

    // TC56 – Build road with insufficient resources throws
    @Test
    public void testBuildRoadInsufficientResources() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        Edge edge = board.getEdge("0,2|1,1");

        players.get(0).addResource(Resource.WOOD, 1);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buildRoad(players.get(0), edge, board));
    }

    // TC57 – Cannot build road with 0 pieces remaining
    @Test
    public void testCannotBuildRoadWith0Remaining() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        int edgeIndex = 0;
        for (Edge edge : board.getEdges()) {
            if (edgeIndex >= 15) {
                break;
            }
            players.get(0).placeRoad(edge);
            edgeIndex++;
        }

        Edge edge = board.getEdge("-3,-5|-2,-4");

        players.get(0).addResource(Resource.WOOD, 1);
        players.get(0).addResource(Resource.BRICK, 1);

        Assertions.assertThrows(IllegalStateException.class,
                () -> turnFlow.buildRoad(players.get(0), edge, board));
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