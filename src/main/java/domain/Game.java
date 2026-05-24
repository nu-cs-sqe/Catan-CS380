package domain;

public class Game {

    private final int numberOfPlayers;
    private final int firstPlayerIndex;
    private final int[] turnOrder;

    public Game(int numberOfPlayers) {
        this(numberOfPlayers, new RandomDiceRoller());
    }

    public Game(int numberOfPlayers, DiceRoller diceRoller) {
        if (numberOfPlayers < 3 || numberOfPlayers > 4) {
            throw new IllegalArgumentException(
                    "Player count must be 3 or 4");
        }
        this.numberOfPlayers = numberOfPlayers;
        this.firstPlayerIndex = determineFirstPlayer(diceRoller);
        this.turnOrder = buildTurnOrder();
    }

    private int determineFirstPlayer(DiceRoller diceRoller) {
        int[] rolls = new int[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            rolls[i] = diceRoller.roll();
        }

        while (true) {
            int maxRoll = -1;
            int maxIndex = -1;
            boolean tied = false;

            for (int i = 0; i < rolls.length; i++) {
                if (rolls[i] > maxRoll) {
                    maxRoll = rolls[i];
                    maxIndex = i;
                    tied = false;
                } else if (rolls[i] == maxRoll) {
                    tied = true;
                }
            }

            if (!tied) {
                return maxIndex;
            }

            for (int i = 0; i < rolls.length; i++) {
                if (rolls[i] == maxRoll) {
                    rolls[i] = diceRoller.roll();
                } else {
                    rolls[i] = -1;
                }
            }
        }
    }

    private int[] buildTurnOrder() {
        int[] order = new int[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            order[i] = (firstPlayerIndex + i) % numberOfPlayers;
        }
        return order;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public int getFirstPlayerIndex() {
        return firstPlayerIndex;
    }

    public int[] getTurnOrder() {
        return turnOrder;
    }
}