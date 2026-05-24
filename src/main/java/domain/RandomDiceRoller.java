package domain;

import java.util.Random;

public class RandomDiceRoller implements DiceRoller {

    private final Random random = new Random();

    @Override
    public int roll() {
        return random.nextInt(6) + 1 + random.nextInt(6) + 1;
    }
}