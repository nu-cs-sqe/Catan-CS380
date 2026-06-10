package domain;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class GameTest {

    private static final int TWO_PLAYERS = 2;
    private static final int FIVE_PLAYERS = 5;

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
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Game(players));
    }

    // TC4 – Game rejects 5 players
    @Test
    public void testGameRejects5Players() {
        List<Player> players = createPlayers(FIVE_PLAYERS);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Game(players));
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
        Assertions.assertArrayEquals(new int[]{1, 2, 0},
                game.getTurnOrder());
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
        int[] rolls = {7, 5, 3};
        List<Player> players = createPlayers(3);
        Game game = new Game(players, stubDiceRoller(rolls));
        game.executeSetupRoundOne();
        for (Player player : game.getPlayers()) {
            Assertions.assertEquals(4,
                    player.getRemainingSettlements());
        }
    }

    // TC9 – Round one: each player places 1 road
    @Test
    public void testRoundOneEachPlayerPlaces1Road() {
        int[] rolls = {7, 5, 3};
        List<Player> players = createPlayers(3);
        Game game = new Game(players, stubDiceRoller(rolls));
        game.executeSetupRoundOne();
        for (Player player : game.getPlayers()) {
            Assertions.assertEquals(14,
                    player.getRemainingRoads());
        }
    }

    // TC10 – Round two proceeds in reverse order
    @Test
    public void testRoundTwoOrderIsReversed() {
        int[] rolls = {7, 5, 3};
        List<Player> players = createPlayers(3);
        Game game = new Game(players, stubDiceRoller(rolls));
        game.executeSetupRoundOne();
        int[] roundTwoOrder = game.getRoundTwoOrder();
        Assertions.assertArrayEquals(new int[]{2, 1, 0}, roundTwoOrder);
    }

    // TC11 – After both rounds each player has 2 settlements
    @Test
    public void testAfterBothRoundsEachPlayerHas2Settlements() {
        int[] rolls = {7, 5, 3};
        List<Player> players = createPlayers(3);
        Game game = new Game(players, stubDiceRoller(rolls));
        game.executeSetupRoundOne();
        game.executeSetupRoundTwo();
        for (Player player : game.getPlayers()) {
            Assertions.assertEquals(3,
                    player.getRemainingSettlements());
        }
    }

    // TC12 – After both rounds each player has 2 roads
    @Test
    public void testAfterBothRoundsEachPlayerHas2Roads() {
        int[] rolls = {7, 5, 3};
        List<Player> players = createPlayers(3);
        Game game = new Game(players, stubDiceRoller(rolls));
        game.executeSetupRoundOne();
        game.executeSetupRoundTwo();
        for (Player player : game.getPlayers()) {
            Assertions.assertEquals(13,
                    player.getRemainingRoads());
        }
    }

    // TC13 – Players receive resources only from second settlement
    @Test
    public void testPlayersReceiveResourcesFromSecondSettlementOnly() {
        int[] rolls = {7, 5, 3};
        List<Player> players = createPlayers(3);
        Game game = new Game(players, stubDiceRoller(rolls));

        game.executeSetupRoundOne();
        for (Player player : game.getPlayers()) {
            for (Resource resource : Resource.values()) {
                Assertions.assertEquals(0,
                        player.getResourceCount(resource));
            }
        }

        Resource[][] roundTwoResources = {
                {Resource.WOOL, Resource.LUMBER},
                {Resource.GRAIN},
                {Resource.ORE, Resource.BRICK}
        };
        game.executeSetupRoundTwo(roundTwoResources);

        Player p0 = game.getPlayers().get(0);
        Assertions.assertEquals(1, p0.getResourceCount(Resource.WOOL));
        Assertions.assertEquals(1, p0.getResourceCount(Resource.LUMBER));

        Player p1 = game.getPlayers().get(1);
        Assertions.assertEquals(1, p1.getResourceCount(Resource.GRAIN));

        Player p2 = game.getPlayers().get(2);
        Assertions.assertEquals(1, p2.getResourceCount(Resource.ORE));
        Assertions.assertEquals(1, p2.getResourceCount(Resource.BRICK));
    }

    // TC14 – Starting player begins main game after setup
    @Test
    public void testStartingPlayerBeginsAfterSetup() {
        int[] rolls = {4, 9, 6};
        List<Player> players = createPlayers(3);
        Game game = new Game(players, stubDiceRoller(rolls));
        game.executeSetupRoundOne();
        game.executeSetupRoundTwo();
        Assertions.assertEquals(1, game.getCurrentPlayerIndex());
    }

    private List<Player> createPlayers(int count) {
        PlayerColor[] colors = PlayerColor.values();
        Player[] players = new Player[count];
        for (int i = 0; i < count; i++) {
            players[i] = new Player("Player" + i,
                    colors[i % colors.length]);
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