package domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class RandomDiceRollerTest {

    private static final int MIN_ROLL = 2;
    private static final int MAX_ROLL = 12;
    private static final int NUM_ROLLS = 100;

    // TC1 & TC2 – Roll is within valid range [2, 12]
    @Test
    public void testRollIsWithinValidRange() {
        RandomDiceRoller roller = new RandomDiceRoller();
        for (int i = 0; i < NUM_ROLLS; i++) {
            int result = roller.roll();
            Assertions.assertTrue(result >= MIN_ROLL,
                    "Roll should be at least " + MIN_ROLL);
            Assertions.assertTrue(result <= MAX_ROLL,
                    "Roll should be at most " + MAX_ROLL);
        }
    }
}