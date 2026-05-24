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

}