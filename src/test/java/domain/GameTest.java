package domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class GameTest {

    private static final int TWO_PLAYERS = 2;
    private static final int THREE_PLAYERS = 3;
    private static final int FOUR_PLAYERS = 4;
    private static final int FIVE_PLAYERS = 5;

    // TC1 – Game accepts exactly 3 players
    @Test
    public void testGameAccepts3Players() {
        Game game = new Game(THREE_PLAYERS);
        Assertions.assertEquals(THREE_PLAYERS, game.getNumberOfPlayers());
    }

    // TC2 – Game accepts exactly 4 players
    @Test
    public void testGameAccepts4Players() {
        Game game = new Game(FOUR_PLAYERS);
        Assertions.assertEquals(FOUR_PLAYERS, game.getNumberOfPlayers());
    }

    // TC3 – Game rejects 2 players
    @Test
    public void testGameRejects2Players() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Game(TWO_PLAYERS));
    }

    // TC4 – Game rejects 5 players
    @Test
    public void testGameRejects5Players() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Game(FIVE_PLAYERS));
    }

    // TC5 – Player with highest dice roll goes first
    @Test
    public void testHighestRollGoesFirst() {
        int[] rolls = {5, 10, 3};
        Game game = new Game(THREE_PLAYERS, stubDiceRoller(rolls));
        Assertions.assertEquals(1, game.getFirstPlayerIndex());
    }

    // TC6 – Turn order proceeds clockwise from starting player
    @Test
    public void testTurnOrderClockwiseFromStartingPlayer() {
        int[] rolls = {4, 9, 6};
        Game game = new Game(THREE_PLAYERS, stubDiceRoller(rolls));
        Assertions.assertArrayEquals(new int[]{1, 2, 0},
                game.getTurnOrder());
    }

    // TC7 – Tied dice rolls are re-rolled
    @Test
    public void testTiedRollsAreReRolled() {
        int[] rolls = {8, 8, 5, 3, 7};
        Game game = new Game(THREE_PLAYERS, stubDiceRoller(rolls));
        Assertions.assertEquals(1, game.getFirstPlayerIndex());
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

    // TC8 – Round one: each player places 1 settlement
    @Test
    public void testRoundOneEachPlayerPlaces1Settlement() {
        int[] rolls = {7, 5, 3};
        Game game = new Game(THREE_PLAYERS, stubDiceRoller(rolls));
        game.executeSetupRoundOne();
        int[] settlements = game.getSettlementsPerPlayer();
        for (int count : settlements) {
            Assertions.assertEquals(1, count);
        }
    }

    // TC9 – Round one: each player places 1 road
    @Test
    public void testRoundOneEachPlayerPlaces1Road() {
        int[] rolls = {7, 5, 3};
        Game game = new Game(THREE_PLAYERS, stubDiceRoller(rolls));
        game.executeSetupRoundOne();
        int[] roads = game.getRoadsPerPlayer();
        for (int count : roads) {
            Assertions.assertEquals(1, count);
        }
    }

    // TC10 – Round two proceeds in reverse order
    @Test
    public void testRoundTwoOrderIsReversed() {
        int[] rolls = {7, 5, 3};
        Game game = new Game(THREE_PLAYERS, stubDiceRoller(rolls));
        game.executeSetupRoundOne();
        int[] roundTwoOrder = game.getRoundTwoOrder();
        Assertions.assertArrayEquals(new int[]{2, 1, 0}, roundTwoOrder);
    }

    // TC11 – After both rounds each player has 2 settlements
    @Test
    public void testAfterBothRoundsEachPlayerHas2Settlements() {
        int[] rolls = {7, 5, 3};
        Game game = new Game(THREE_PLAYERS, stubDiceRoller(rolls));
        game.executeSetupRoundOne();
        game.executeSetupRoundTwo();
        int[] settlements = game.getSettlementsPerPlayer();
        for (int count : settlements) {
            Assertions.assertEquals(2, count);
        }
    }

    // TC12 – After both rounds each player has 2 roads
    @Test
    public void testAfterBothRoundsEachPlayerHas2Roads() {
        int[] rolls = {7, 5, 3};
        Game game = new Game(THREE_PLAYERS, stubDiceRoller(rolls));
        game.executeSetupRoundOne();
        game.executeSetupRoundTwo();
        int[] roads = game.getRoadsPerPlayer();
        for (int count : roads) {
            Assertions.assertEquals(2, count);
        }
    }

    // TC13 – Players receive resources only from second settlement
    @Test
    public void testPlayersReceiveResourcesFromSecondSettlementOnly() {
        int[] rolls = {7, 5, 3};
        Game game = new Game(THREE_PLAYERS, stubDiceRoller(rolls));

        game.executeSetupRoundOne();
        for (int i = 0; i < THREE_PLAYERS; i++) {
            Assertions.assertEquals(0, game.getTotalResources(i));
        }

        String[][] roundTwoResources = {
                {"WOOL", "WOOD"},
                {"WHEAT"},
                {"ORE", "BRICK"}
        };
        game.executeSetupRoundTwo(roundTwoResources);

        Assertions.assertEquals(2, game.getTotalResources(0));
        Assertions.assertEquals(1, game.getResourceCount(0, "WOOL"));
        Assertions.assertEquals(1, game.getResourceCount(0, "WOOD"));

        Assertions.assertEquals(1, game.getTotalResources(1));
        Assertions.assertEquals(1, game.getResourceCount(1, "WHEAT"));

        Assertions.assertEquals(2, game.getTotalResources(2));
        Assertions.assertEquals(1, game.getResourceCount(2, "ORE"));
        Assertions.assertEquals(1, game.getResourceCount(2, "BRICK"));
    }

    // TC14 – Starting player begins main game after setup
    @Test
    public void testStartingPlayerBeginsAfterSetup() {
        int[] rolls = {4, 9, 6};
        Game game = new Game(THREE_PLAYERS, stubDiceRoller(rolls));
        game.executeSetupRoundOne();
        game.executeSetupRoundTwo();
        Assertions.assertEquals(1, game.getCurrentPlayerIndex());
    }
    
}