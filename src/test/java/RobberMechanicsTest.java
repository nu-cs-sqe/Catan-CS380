package domain;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class RobberMechanicsTest {

    // TC7 – Player with 8+ cards must discard half
    @Test
    public void testPlayerWith8CardsMustDiscard4() {
        List<Player> players = createPlayers();
        Game game = createGame(players);

        for (int i = 0; i < 8; i++) {
            players.get(0).addResource(Resource.BRICK, 1);
        }

        Assertions.assertEquals(4, game.getDiscardCount(0));
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