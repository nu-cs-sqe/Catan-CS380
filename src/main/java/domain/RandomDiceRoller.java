package domain;

import java.util.Random;

public class RandomDiceRoller implements DiceRoller {

    private static final int DIE_SIDES = 6;
    private final Random random = new Random();

    @Override
    public int roll() {
        return random.nextInt(DIE_SIDES) + 1
                + random.nextInt(DIE_SIDES) + 1;
    }
}