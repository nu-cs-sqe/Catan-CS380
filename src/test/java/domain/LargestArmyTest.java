package domain;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class LargestArmyTest {

    private static final int LARGEST_ARMY_VP = 2;

    // TC1 – No player has Largest Army with fewer than 3 knights
    @Test
    public void testNoLargestArmyUnder3Knights() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        players.get(0).playKnight();
        players.get(0).playKnight();
        players.get(1).playKnight();
        turnFlow.updateLargestArmy();
        Assertions.assertEquals(-1, turnFlow.getLargestArmyHolder());
        for (int i = 0; i < players.size(); i++) {
            Assertions.assertEquals(0, turnFlow.getVictoryPoints(i));
        }
    }

    // TC2 – First player to play 3 knights gets Largest Army
    @Test
    public void testFirstPlayerWith3KnightsGetsLargestArmy() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        players.get(0).playKnight();
        players.get(0).playKnight();
        players.get(0).playKnight();
        turnFlow.updateLargestArmy();
        Assertions.assertEquals(0, turnFlow.getLargestArmyHolder());
        Assertions.assertEquals(LARGEST_ARMY_VP,
                turnFlow.getVictoryPoints(0) - players.get(0).getVictoryPoints());
    }

    // TC3 – Another player with more knights takes Largest Army
    @Test
    public void testPlayerWithMoreKnightsTakesLargestArmy() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        players.get(0).playKnight();
        players.get(0).playKnight();
        players.get(0).playKnight();
        turnFlow.updateLargestArmy();
        Assertions.assertEquals(0, turnFlow.getLargestArmyHolder());

        players.get(1).playKnight();
        players.get(1).playKnight();
        players.get(1).playKnight();
        players.get(1).playKnight();
        turnFlow.updateLargestArmy();
        Assertions.assertEquals(1, turnFlow.getLargestArmyHolder());
    }

    // TC4 – Tied knight count does not change Largest Army holder
    @Test
    public void testTiedKnightCountDoesNotChangeLargestArmy() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        players.get(0).playKnight();
        players.get(0).playKnight();
        players.get(0).playKnight();
        turnFlow.updateLargestArmy();
        Assertions.assertEquals(0, turnFlow.getLargestArmyHolder());

        players.get(1).playKnight();
        players.get(1).playKnight();
        players.get(1).playKnight();
        turnFlow.updateLargestArmy();
        Assertions.assertEquals(0, turnFlow.getLargestArmyHolder());
    }

    private List<Player> createPlayers() {
        return Arrays.asList(
                new Player("Alice", PlayerColor.RED),
                new Player("Bob", PlayerColor.BLUE),
                new Player("Carol", PlayerColor.PINK)
        );
    }
}